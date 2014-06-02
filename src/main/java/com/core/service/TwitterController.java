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

import com.api.iMathCloud;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TwitterController extends AbstractController {
	
	@Inject private MainDB db;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public BigCloudResponse.ServiceDTO run_SentimentAnalysis(Long id_ServiceInstance, String query_terms, Long track_time) throws Exception{
		
		System.out.println("Running run_SentimentAnalysis");
		Constants C = new Constants();
		
		
		Service_Instance instance = db.getServiceInstanceDB().findById(id_ServiceInstance);
		AuthenticUser auser = new AuthenticUser(instance.getUser().getUserName(), instance.getUser().getPassword());
		
		System.out.println("user " + instance.getUser().getUserName());
		//1. Create a new file that represents the job to be executed
		
		//1.1 Copy the template file to the correct location and with another name
		File job_file = SA_createJobFileService(instance);
		
		//1.2 Change label in the file by the parameters.
		//TODO
				
		//2. Upload file to iMathCloud. Get idFile
		Long idFile = iMathCloud.uploadFile(auser, job_file.getPath(), "");
		
		//3. Submit job. Get idJob
		Long idJob = iMathCloud.runPythonJob(auser, idFile);
		
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
		
		BigCloudResponse.ServiceDTO out = new  BigCloudResponse.ServiceDTO(ex.getId(), job.getId(), ex.getState().name());
		return out;	
		
	}
	
	public BigCloudResponse.ServiceDTO getExecutionState(Long idExecution) throws Exception{
		
		
		Execution ex = db.getExecutionDB().findById(idExecution);
		Long imathCloud_idJob = ex.getJob().getIdiMathCloud();
		
		AuthenticUser auser = new AuthenticUser(ex.getServiceInstance().getUser().getUserName(), ex.getServiceInstance().getUser().getPassword());
		
		String state = iMathCloud.getJobState(auser, idExecution);
		
		//ex.setState(state);
		db.makePersistent(ex);
		
		BigCloudResponse.ServiceDTO out = new  BigCloudResponse.ServiceDTO(ex.getId(), ex.getJob().getId(), ex.getState().name());
		
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
