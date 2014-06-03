package com.core.service;

import com.core.data.MainDB;
import com.core.model.BC_Job;
import com.core.model.Execution;
import com.core.model.Service_Instance;
import com.core.util.*;
import com.util.AuthenticUser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.api.iMathCloud;
import com.exception.*;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TwitterController extends AbstractController {
	
	@Inject private MainDB db;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public BigCloudResponse.ServiceDTO run_SentimentAnalysis(Long id_ServiceInstance, String query_terms, Long track_time) throws Exception {
		
		System.out.println("Running run_SentimentAnalysis");
		
		Service_Instance instance = db.getServiceInstanceDB().findById(id_ServiceInstance);
		AuthenticUser auser = new AuthenticUser(instance.getUser().getUserName(), instance.getUser().getPassword());
		
		System.out.println("user " + instance.getUser().getUserName());
		//1. Create a new file that represents the job to be executed
		
		//1.1 Copy the template file to the correct location and with another name
		File job_file = SA_createJobFileService(instance);
		
		//1.2 Change label in the file by the parameters.
		//TODO
				
		//2. Upload file to iMathCloud. Get idFile
		//3. Submit job. Get idJob
		Long idFile = 0L;
		Long idJob = 0L;
		try{
			idFile = iMathCloud.uploadFile(auser, job_file.getPath(), "");
			idJob = iMathCloud.runPythonJob(auser, idFile);
		}
		catch (IOException | iMathAPIException e){
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
			
		//4.Persist the job Object and the execution Object
		BC_Job job = new BC_Job();
		job.setIdiMathCloud(idJob);
		job.setName(job_file.getPath());
		//Create the execution associated to this service instance 
		Execution ex = new Execution();
		ex.setJob(job);
		ex.setServiceInstance(instance);
		ex.setState(Execution.States.RUNNING);
		Map<String,String> m = new HashMap<String, String>();
		m.put("query_terms", query_terms);
		m.put("track_time", String.valueOf(track_time));
		String string_m = m.toString();
		System.out.println("Configuration string " + string_m);
		ex.setConfiguration(string_m);
		db.makePersistent(ex);
		
		BigCloudResponse.ServiceDTO out = new  BigCloudResponse.ServiceDTO(ex.getId(), job.getId(), ex.getState());
		return out;	
		
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public BigCloudResponse.ServiceDTO getExecutionState(Long idExecution) throws Exception{
		
		System.out.println("Function getExecutionState");
		
		//1. First we find the imathCloud job associated to the execution
		Execution ex = db.getExecutionDB().findById(idExecution);
		Long imathCloud_idJob = ex.getJob().getIdiMathCloud();
		
		System.out.println("Execution initial state " + ex.getState().ordinal());
		
		//2. Create the user to be authenthicated in the rest call of iMathCloud
		AuthenticUser auser = new AuthenticUser(ex.getServiceInstance().getUser().getUserName(), ex.getServiceInstance().getUser().getPassword());
		
		//3. Get the state associated to the job
		String state; 
		try{
			state = iMathCloud.getJobState(auser, imathCloud_idJob);
		}
		catch (iMathAPIException e){
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		catch (IOException e){
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		//4. Update the state of the execution according to the state of the job
		ex.setState(Execution.States.valueOf(state));
		//db.makePersistent(ex);
		
		BigCloudResponse.ServiceDTO out = new  BigCloudResponse.ServiceDTO(ex.getId(), ex.getJob().getId(), ex.getState());
		
		System.out.println("Execution final state " + ex.getState().ordinal());
		
		return out;
	}
	
	public File SA_createJobFileService(Service_Instance s) throws IOException{
		
		Constants C = new Constants();
		File f_src = new File(C.JOB_SA_TEMPLATE);
		//New file
		String uid = "_" + UUID.randomUUID().toString();
		String name_dst = String.valueOf(s.getId()) + "_" + s.getUser().getUserName() + uid;
		File f_dst = new File (C.JOBS_FILES_DIR + "/" + name_dst + ".py");		
		
		FileUtils.copyFiles(f_src, f_dst);
		
		return f_dst;
		
	}

}
