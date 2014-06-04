package com.core.service;

import com.core.data.MainDB;
import com.core.model.BC_Job;
import com.core.model.Execution;
import com.core.model.Service_Instance;
import com.core.util.*;
import com.util.AuthenticUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
		
		//1.1 Copy the template file to the correct location, with another name and parameterised
		String quote_query = "\""+query_terms+"\"";
		File job_file = SA_createJobFileService(instance, quote_query, track_time);
				
		//2. Upload file to iMathCloud. Get idFile
		//3. Submit job. Get idJob
		Long idFile = 0L;
		Long idJob = 0L;
		try{
			idFile = iMathCloud.uploadFile(auser, job_file.getPath(), "");
			Calendar cal = Calendar.getInstance();
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.MEDIUM);

            System.out.println("TIME --- "  + df.format(cal.getTime()));
			idJob = iMathCloud.runPythonJob(auser, idFile);
            System.out.println("TIME --- "  + df.format(cal.getTime()));
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
		
		BigCloudResponse.ServiceDTO out = new  BigCloudResponse.ServiceDTO(ex.getId(), ex.getJob().getId(), ex.getState());
		
		System.out.println("Execution final state " + ex.getState().ordinal());
		
		return out;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public BigCloudResponse.ServiceDTO getExecutionData(Long idExecution) throws Exception{
		
		//1. First we find the imathCloud job associated to the execution
		Execution ex = db.getExecutionDB().findById(idExecution);
		Long imathCloud_idJob = ex.getJob().getIdiMathCloud();
		
		//2. Create the user to be authenthicated in the rest call of iMathCloud
		AuthenticUser auser = new AuthenticUser(ex.getServiceInstance().getUser().getUserName(), ex.getServiceInstance().getUser().getPassword());
		
		//3. Get the outputfiles associated to the job
		Map<String, Long> list_files = new HashMap<String, Long>(); 
		try{
			list_files = iMathCloud.getJobOutputFiles(auser, imathCloud_idJob);
		}
		catch (iMathAPIException e){
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		catch (IOException e){
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		//4. The [idiMathJob_job_file_name.out] outputfile contains the interesting data
		//4.1 Obtain the full name of the job (includes the complete path)
		String job_full_file_name = ex.getJob().getName();
		//4.2 Take only the name of the file that represents the job
		String [] full_name_split = job_full_file_name.split("/");
		String file_name = full_name_split[full_name_split.length-1];
		//4.3 Build the name of the .out file, which depends of the job id of iMathCloud
		String outputfile_name = String.valueOf(ex.getJob().getIdiMathCloud()) + "_" + file_name.replaceAll(".py$", ".out");
		
		//5. Get id of .out file
		Long id_outputfile = list_files.get(outputfile_name);
		
		//6. Get content of the output file. A string per line
		// In the case of sentiment analysis, the content represents is a dictionary, where each key 
		// represent a query terms and has associated a list of sentiment associated to
		// each gathered tweet. It is in only one line
		List<String> content; 
		try{
			content = iMathCloud.getFileContent(auser, id_outputfile);
		}
		catch(Exception e){
			throw e;
		}
		
		Map<String, Double> processed_data = SA_processSentimentData(content.get(0));		
		
		BigCloudResponse.ServiceDTO out = new  BigCloudResponse.ServiceDTO(ex.getId(), ex.getJob().getId(), ex.getState(), processed_data);
		
		return out;
	}
	
	public Map<String, Double> SA_processSentimentData(String raw_data){
		
		MapUtils.MyMap<String, List<Double>> mm = new MapUtils.MyMap<String, List<Double>>();
		mm.jsonToMap(raw_data);
		
		Map<String, Double> output_map = new HashMap<String, Double>();
		Double count, mean;
		for (String key : mm.getMap().keySet()){
			List<Double> list_sentimentScore = mm.getMap().get(key);
			count = 0D;
			for(Double f: list_sentimentScore){
				count += f;
			}
			mean = count/list_sentimentScore.size();
			output_map.put(key, mean);
		}
		
		return output_map;
		
	}
	
	public File SA_createJobFileService(Service_Instance s, String query_terms, Long track_time) throws IOException{
		
		Constants C = new Constants();
		File f_src = new File(C.JOB_SA_TEMPLATE);
		//New file
		String uid = "_" + UUID.randomUUID().toString();
		String name_dst = String.valueOf(s.getId()) + "_" + s.getUser().getUserName() + uid;
		File f_dst = new File (C.JOBS_FILES_DIR + "/" + name_dst + ".py");		
		
		//FileUtils.copyFiles(f_src, f_dst);
		
		BufferedReader reader = new BufferedReader(new FileReader(f_src));
		StringBuffer buffer = new StringBuffer();
		String line;
		while((line = reader.readLine()) != null) {
		    buffer.append(line);
		    buffer.append("\r\n");
		}
		reader.close();

		Map<String, String> ReplacementMap = new HashMap<String, String>();
		ReplacementMap.put("<<QUERY>>", query_terms);
		ReplacementMap.put("<<TIME>>", String.valueOf(track_time));

		String toWrite = buffer.toString();
		for (Map.Entry<String, String> entry : ReplacementMap.entrySet()) {
		    toWrite = toWrite.replaceAll(entry.getKey(), entry.getValue());
		}

		FileWriter writer = new FileWriter(f_dst.getPath());
		writer.write(toWrite);
		writer.close();
		
		return f_dst;
		
	}

}
