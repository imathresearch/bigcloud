package com.core.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import com.core.data.BC_UserDB;
import com.core.data.ExecutionDB;
import com.core.data.MainDB;
import com.core.data.ServiceInstanceDB;
import com.core.model.BC_Job;
import com.core.model.BC_User;
import com.core.model.Execution;
import com.core.model.Result_Data;
import com.core.model.Service;
import com.core.model.Execution.States;
import com.core.model.Service_Instance;
import com.core.rest.SessionService;
import com.core.util.BigCloudResponse;
import com.exception.iMathAPIException;
import com.util.AuthenticUser;

@RunWith(PowerMockRunner.class)
@PrepareForTest(com.api.iMathCloud.class)
public class TwitterControllerUnitTest {

	TwitterController tc = new TwitterController();
	// We do not mock the MainServiceDB. We mock the inside elements.
    private MainDB db;
	
    @Mock private EntityManager em;
    @Mock private BC_UserDB user;
    @Mock private ServiceInstanceDB instDB;
    @Mock private ExecutionDB exDB;
	

	@Before
    public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    
	    db = new MainDB();
	    db.setEntityManager(em);
	    db.setBC_UserDB(user);
	    db.setServiceInstanceDB(instDB);
	    db.setExecutionDB(exDB);
	    // We simulate the injections	    
	    tc.setMainDB(db);

	    PowerMockito.mockStatic(com.api.iMathCloud.class);
    }
	
	@Test
	// The service instance does not exist in the DB
	public void test_run_SentimentAnalysis_1() throws Exception {
		Long id_ServiceInstance = 1L;
		String query_terms = "term1,term2";
		Long track_time = 20L;
		String formatted_track_time = "16/90/2014";
		Long update_freq = 5L;
		
		when(db.getServiceInstanceDB().findById(id_ServiceInstance)).thenReturn(null);
		
		try{
			tc.run_SentimentAnalysis(id_ServiceInstance, query_terms, track_time, formatted_track_time, update_freq);
		}
		catch(WebApplicationException e){
			System.out.println("Catching exception 1");
			assertTrue(e.getResponse().getStatus() == 404);
		}
	}
	
	@Test
	// The service instance exist in the DB
	// but one of the three calls to iMathCloud API throws an exception
	public void test_run_SentimentAnalysis_2() throws Exception {
		Long id_ServiceInstance = 1L;
		String query_terms = "term1,term2";
		Long track_time = 20L;
		String formatted_track_time = "16/90/2014";
		Long update_freq = 5L;
		Service_Instance inst = new Service_Instance();
		BC_User user = new BC_User();
		user.setUserName("userName");
		user.setPassword("password");
		inst.setUser(user);
		inst.setId(1L);
		
		Long idFile = 10L;
		Long idJob = 10L;
		
		when(db.getServiceInstanceDB().findById(id_ServiceInstance)).thenReturn(inst);
		PowerMockito.when(com.api.iMathCloud.uploadFile((AuthenticUser)Matchers.any(), (String)Matchers.any(), (String)Matchers.any())).thenThrow(new iMathAPIException(iMathAPIException.API_ERROR.INTERNAL_SERVER_ERROR));

		try{
			tc.run_SentimentAnalysis(id_ServiceInstance, query_terms, track_time, formatted_track_time, update_freq);
		}
		catch(WebApplicationException e){
			System.out.println("Catching exception 2");
			assertTrue(e.getResponse().getStatus() == 500);
			
		}
	}

	@Test
	// The service instance exist in the DB
	// but the function findLastExecutionByServiceInstance throws an exception
	public void test_run_SentimentAnalysis_3() throws Exception {
		Long id_ServiceInstance = 1L;
		String query_terms = "term1,term2";
		Long track_time = 20L;
		String formatted_track_time = "16/90/2014";
		Long update_freq = 5L;
		Service_Instance inst = new Service_Instance();
		BC_User user = new BC_User();
		user.setUserName("userName");
		user.setPassword("password");
		inst.setUser(user);
		inst.setId(1L);
		
		Long idFile = 10L;
		Long idJob = 10L;
		
		when(db.getServiceInstanceDB().findById(id_ServiceInstance)).thenReturn(inst);
		PowerMockito.when(com.api.iMathCloud.uploadFile((AuthenticUser)Matchers.any(), (String)Matchers.any(), (String)Matchers.any())).thenReturn(idFile);
		PowerMockito.when(com.api.iMathCloud.runPythonJob((AuthenticUser)Matchers.any(), (Long)Matchers.anyLong())).thenReturn(idJob);
		when(db.getExecutionDB().findLastExecutionByServiceInstance(inst.getId())).thenThrow(new Exception());

		try{
			tc.run_SentimentAnalysis(id_ServiceInstance, query_terms, track_time, formatted_track_time, update_freq);
		}
		catch(Exception e){
			System.out.println("Catching exception 3");
			
			PowerMockito.verifyStatic(times(2));
			com.api.iMathCloud.uploadFile((AuthenticUser)Matchers.any(), (String)Matchers.any(), (String)Matchers.any());
			PowerMockito.verifyStatic(times(1));
			com.api.iMathCloud.runPythonJob((AuthenticUser)Matchers.any(), (Long)Matchers.anyLong());
		}
	}
	
	@Test
	// HAPPY PATH
	public void test_run_SentimentAnalysis_4() throws Exception {
		Long id_ServiceInstance = 1L;
		String query_terms = "term1,term2";
		Long track_time = 20L;
		String formatted_track_time = "16/90/2014";
		Long update_freq = 5L;
		Service_Instance inst = new Service_Instance();
		BC_User user = new BC_User();
		user.setUserName("userName");
		user.setPassword("password");
		inst.setUser(user);
		inst.setId(1L);
		
		Long idFile = 10L;
		Long idJob = 10L;
		List<Execution> list_exc = new ArrayList<Execution>();
		
		when(db.getServiceInstanceDB().findById(id_ServiceInstance)).thenReturn(inst);
		PowerMockito.when(com.api.iMathCloud.uploadFile((AuthenticUser)Matchers.any(), (String)Matchers.any(), (String)Matchers.any())).thenReturn(idFile);
		PowerMockito.when(com.api.iMathCloud.runPythonJob((AuthenticUser)Matchers.any(), (Long)Matchers.anyLong())).thenReturn(idJob);
		when(db.getExecutionDB().findLastExecutionByServiceInstance(inst.getId())).thenReturn(list_exc);

		BigCloudResponse.ServiceDTO out;
		out = tc.run_SentimentAnalysis(id_ServiceInstance, query_terms, track_time, formatted_track_time, update_freq);
		
		PowerMockito.verifyStatic(times(2));
		com.api.iMathCloud.uploadFile((AuthenticUser)Matchers.any(), (String)Matchers.any(), (String)Matchers.any());
		PowerMockito.verifyStatic(times(1));
		com.api.iMathCloud.runPythonJob((AuthenticUser)Matchers.any(), (Long)Matchers.anyLong());
		
		assertTrue(out.state == States.RUNNING);
		
	}
	
	@Test
	// the execution does not exist
	public void test_getExecutionState_1() throws Exception{
		Long idExecution = 1L;
		
		when(db.getExecutionDB().findById(idExecution)).thenReturn(null);
		
		try{
			tc.getExecutionState(idExecution);
		}
		catch(WebApplicationException e){
			System.out.println("Catching exception 1");
			assertTrue(e.getResponse().getStatus() == 404);
		}
	}
	
	@Test
	// the execution exists
	// but iMathCloud.getJobState throws an exception
	public void test_getExecutionState_2() throws Exception{
		Long idExecution = 1L;
		
		Execution ex = new Execution();
		ex.setId(1L);
		Service service = new Service();
		service.setName("Twitter Sentiment Analysis");
		Service_Instance inst = new Service_Instance();
		inst.setId(1L);
		inst.setService(service);
		BC_User user = new BC_User();
		user.setUserName("userName");
		user.setPassword("password");
		inst.setUser(user);
		ex.setServiceInstance(inst);
		BC_Job job = new BC_Job();
		job.setId(1L);
		job.setIdiMathCloud(2L);
		ex.setJob(job);
		
		when(db.getExecutionDB().findById(idExecution)).thenReturn(ex);
		PowerMockito.when(com.api.iMathCloud.getJobState((AuthenticUser)Matchers.any(), Matchers.anyLong())).thenThrow(new iMathAPIException(iMathAPIException.API_ERROR.INTERNAL_SERVER_ERROR));
		
		try{
			tc.getExecutionState(idExecution);
		}
		catch(WebApplicationException e){
			System.out.println("Catching exception 2");
			assertTrue(e.getResponse().getStatus() == 404);
		}
	}
	
	@Test
	// HAPPY PATH
	public void test_getExecutionState_3() throws Exception{
		Long idExecution = 1L;
		
		Execution ex = new Execution();
		ex.setId(1L);
		Service service = new Service();
		service.setName("Twitter Sentiment Analysis");
		Service_Instance inst = new Service_Instance();
		inst.setId(1L);
		inst.setService(service);
		BC_User user = new BC_User();
		user.setUserName("userName");
		user.setPassword("password");
		inst.setUser(user);
		ex.setServiceInstance(inst);
		BC_Job job = new BC_Job();
		job.setId(1L);
		job.setIdiMathCloud(2L);
		ex.setJob(job);
		
		String state = "PAUSED";
		
		when(db.getExecutionDB().findById(idExecution)).thenReturn(ex);
		PowerMockito.when(com.api.iMathCloud.getJobState((AuthenticUser)Matchers.any(), Matchers.anyLong())).thenReturn(state);
	
		BigCloudResponse.ServiceDTO out  = tc.getExecutionState(idExecution);
		
		new  BigCloudResponse.ServiceDTO(ex.getId(), ex.getJob().getId(), ex.getState());
		
		assertTrue(out.idExecution == ex.getId());
		assertTrue(out.idJob == ex.getJob().getId());
		assertTrue(out.state == Execution.States.valueOf(state));
		
		PowerMockito.verifyStatic(times(1));
		com.api.iMathCloud.getJobState((AuthenticUser)Matchers.any(), Matchers.anyLong());
	}

	@Test
	// the execution does not exist
	public void test_getExecutionParcialData_1() throws Exception{
		Long idExecution = 1L;
		
		when(db.getExecutionDB().findById(idExecution)).thenReturn(null);
		
		try{
			tc.getExecutionParcialData(idExecution);
		}
		catch(WebApplicationException e){
			System.out.println("Catching exception 1");
			assertTrue(e.getResponse().getStatus() == 404);
		}
	}
	
	@Test
	// the execution exists
	// but iMathCloud.getFileContent throws an exception
	public void test_getExecutionParcialData_2() throws Exception{
		Long idExecution = 1L;
		
		Execution ex = new Execution();
		ex.setId(1L);
		Service service = new Service();
		service.setName("Twitter Sentiment Analysis");
		Service_Instance inst = new Service_Instance();
		inst.setId(1L);
		inst.setService(service);
		BC_User user = new BC_User();
		user.setUserName("userName");
		user.setPassword("password");
		inst.setUser(user);
		ex.setServiceInstance(inst);
		BC_Job job = new BC_Job();
		job.setId(1L);
		job.setIdiMathCloud(2L);
		ex.setJob(job);
		Result_Data result = new Result_Data();
		result.setIdFile(3L);
		ex.setResult(result);
		
		when(db.getExecutionDB().findById(idExecution)).thenReturn(ex);
		PowerMockito.when(com.api.iMathCloud.getFileContent((AuthenticUser)Matchers.any(), Matchers.anyLong())).thenThrow(new iMathAPIException(iMathAPIException.API_ERROR.INTERNAL_SERVER_ERROR));
		
		try{
			tc.getExecutionParcialData(idExecution);
		}
		catch(WebApplicationException e){
			System.out.println("Catching exception 2");
			assertTrue(e.getResponse().getStatus() == 404);
		}
	}
	
	@Test
	// the execution exists
	// iMathCloud.getFileContent returns an empty string
	public void test_getExecutionParcialData_3() throws Exception{
		Long idExecution = 1L;
		
		Execution ex = new Execution();
		ex.setId(1L);
		Service service = new Service();
		service.setName("Twitter Sentiment Analysis");
		Service_Instance inst = new Service_Instance();
		inst.setId(1L);
		inst.setService(service);
		BC_User user = new BC_User();
		user.setUserName("userName");
		user.setPassword("password");
		inst.setUser(user);
		ex.setServiceInstance(inst);
		BC_Job job = new BC_Job();
		job.setId(1L);
		job.setIdiMathCloud(2L);
		ex.setJob(job);
		Result_Data result = new Result_Data();
		result.setIdFile(3L);
		ex.setResult(result);
		ex.setState(Execution.States.CANCELLED);
		
		List<String> data = new ArrayList<String>();
		
		
		when(db.getExecutionDB().findById(idExecution)).thenReturn(ex);
		PowerMockito.when(com.api.iMathCloud.getFileContent((AuthenticUser)Matchers.any(), Matchers.anyLong())).thenReturn(data);
		
		BigCloudResponse.ServiceDTO out = tc.getExecutionParcialData(idExecution);
		
		assertTrue(out.idExecution == ex.getId());
		assertTrue(out.idJob == ex.getJob().getId());
		assertTrue(out.state == ex.getState());
		assertTrue(out.dataResult.size() == 0);
		
		PowerMockito.verifyStatic(times(1));
		com.api.iMathCloud.getFileContent((AuthenticUser)Matchers.any(), Matchers.anyLong());
		
	}
	
	@Test
	// the execution exists
	// iMathCloud.getFileContent returns a non-empty string
	public void test_getExecutionParcialData_4() throws Exception{
		Long idExecution = 1L;
		
		Execution ex = new Execution();
		ex.setId(1L);
		Service service = new Service();
		service.setName("Twitter Sentiment Analysis");
		Service_Instance inst = new Service_Instance();
		inst.setId(1L);
		inst.setService(service);
		BC_User user = new BC_User();
		user.setUserName("userName");
		user.setPassword("password");
		inst.setUser(user);
		ex.setServiceInstance(inst);
		BC_Job job = new BC_Job();
		job.setId(1L);
		job.setIdiMathCloud(2L);
		ex.setJob(job);
		Result_Data result = new Result_Data();
		result.setIdFile(3L);
		ex.setResult(result);
		ex.setState(Execution.States.CANCELLED);
		
		List<String> data = new ArrayList<String>();
		String st = "{\"A\":[0.9],\"B\":[0.1]}";
		data.add(st);
		
		when(db.getExecutionDB().findById(idExecution)).thenReturn(ex);
		PowerMockito.when(com.api.iMathCloud.getFileContent((AuthenticUser)Matchers.any(), Matchers.anyLong())).thenReturn(data);
		
		BigCloudResponse.ServiceDTO out = tc.getExecutionParcialData(idExecution);
		
		assertTrue(out.idExecution == ex.getId());
		assertTrue(out.idJob == ex.getJob().getId());
		assertTrue(out.state == ex.getState());
		assertTrue(out.dataResult.get("A") == 0.9);
		assertTrue(out.dataResult.get("B") == 0.1);
		
		PowerMockito.verifyStatic(times(1));
		com.api.iMathCloud.getFileContent((AuthenticUser)Matchers.any(), Matchers.anyLong());
		
	}
	
}
