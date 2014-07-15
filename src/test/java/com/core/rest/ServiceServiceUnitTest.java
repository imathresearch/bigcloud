package com.core.rest;



import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;

import com.api.iMathCloud;
import com.core.data.ExecutionDB;
import com.core.data.MainDB;
import com.core.model.BC_Job;
import com.core.model.BC_User;
import com.core.model.Execution;
import com.core.model.Execution.States;
import com.core.model.Service_Instance;
import com.core.service.TwitterController;
import com.core.util.BigCloudResponse;
import com.core.util.MapUtils;
import com.util.AuthenticUser;

@RunWith(PowerMockRunner.class)
@PrepareForTest(com.api.iMathCloud.class)

public class ServiceServiceUnitTest {
	
	
	ServiceService ss = new ServiceService();
	// We do not mock the MainServiceDB. We mock the inside elements.
    private MainDB db;
	
    @Mock private EntityManager em;
    @Mock private ExecutionDB exDB;
	@Mock private TwitterController tc;
	@Mock private Logger LOG;
	
	
	@Before
    public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    
	    db = new MainDB();
	    db.setEntityManager(em);
	    db.setExecutionDB(exDB);
	    
	    // We simulate the injections
	    ss.setTwitterController(tc);
	    ss.setMainDB(db);
	    ss.setLogger(LOG);
	    PowerMockito.mockStatic(com.api.iMathCloud.class);
    }
	
	@Test
	//Json input is empty
	public void test_submitService_1(){
		
		String json = new String();
		try{
			ss.REST_submitService(json);
		}
		catch(Exception e){
			assertTrue(e instanceof WebApplicationException);
		}
	}
	
	@Test
	//Miss one of the MAIN TWO fields of the json input string (service or instance)
	public void test_submitService_2() throws Exception{
		MapUtils.MyMap<String,String> m = new MapUtils.MyMap<String,String>();
		m.setValue("service", "SAForm");
		String jsonMap = m.createJsonString();
			
		try{
			ss.REST_submitService(jsonMap);
		}
		catch(WebApplicationException e){
			assertTrue(e.getResponse().getStatus() == 404);
		}
	}
	
	
	@Test
	//findLastExecutionByServiceInstance function throws an exception
	public void test_submitService_3() throws Exception{
		MapUtils.MyMap<String,String> m = new MapUtils.MyMap<String,String>();
		m.setValue("service", "SAForm");
		m.setValue("instance", "2");
		String jsonMap = m.createJsonString();
		
		when(db.getExecutionDB().findLastExecutionByServiceInstance(2L)).thenThrow(new Exception());
		
		try{
			ss.REST_submitService(jsonMap);
		}
		catch(WebApplicationException e){
			assertTrue(e.getResponse().getStatus() == 500);
		}
	}
	
	@Test
	//findLastExecutionByServiceInstance function return a list of executions
	//where there is only one, and this one is paused
	//So, an exception is thrown
	public void test_submitService_4() throws Exception{
		MapUtils.MyMap<String,String> m = new MapUtils.MyMap<String,String>();
		m.setValue("service", "SAForm");
		m.setValue("instance", "2");
		String jsonMap = m.createJsonString();
		
		Execution ex = new Execution();
		ex.setState(States.PAUSED);
		List <Execution> list_ex = new ArrayList<Execution>();
		list_ex.add(ex);
		
		when(db.getExecutionDB().findLastExecutionByServiceInstance(2L)).thenReturn(list_ex);
		
		try{
			ss.REST_submitService(jsonMap);
		}
		catch(WebApplicationException e){
			assertTrue(e.getResponse().getStatus() == 500);
			
		}
	}
	
	@Test
	//findLastExecutionByServiceInstance function return a list of executions
	//where there is only one, and this one is finished ok
	//so, we check the service name
	//One or more of the other params are missing 
	public void test_submitService_5() throws Exception{
		MapUtils.MyMap<String,String> m = new MapUtils.MyMap<String,String>();
		m.setValue("service", "SAForm");
		m.setValue("instance", "2");
		String jsonMap = m.createJsonString();
		
		Execution ex = new Execution();
		ex.setState(States.FINISHED_OK);
		List <Execution> list_ex = new ArrayList<Execution>();
		list_ex.add(ex);
		
		when(db.getExecutionDB().findLastExecutionByServiceInstance(2L)).thenReturn(list_ex);
		
		try{
			ss.REST_submitService(jsonMap);
		}
		catch(WebApplicationException e){
			assertTrue(e.getResponse().getStatus() == 404);
			
		}
	}
	
	@Test
	//findLastExecutionByServiceInstance function return a list of executions
	//where there is only one, and this one is finished ok
	//so, we check the service name
	//Finally we submit the service
	// HAPPY PATH
	public void test_submitService_6() throws Exception{
		MapUtils.MyMap<String,String> m = new MapUtils.MyMap<String,String>();
		m.setValue("service", "SAForm");
		m.setValue("instance", "2");
		m.setValue("query_terms", "TERM1,TERM2");
		m.setValue("track_time", "500");
		m.setValue("format_track_time", "15/09/2014");
		m.setValue("update_freq", "15");
		String jsonMap = m.createJsonString();
		
		Execution ex = new Execution();
		ex.setState(States.FINISHED_OK);
		List <Execution> list_ex = new ArrayList<Execution>();
		list_ex.add(ex);
		BigCloudResponse.ServiceDTO param = new BigCloudResponse.ServiceDTO(1L, 1L, States.RUNNING);
		BigCloudResponse.ServiceDTO out = null;
		
		when(db.getExecutionDB().findLastExecutionByServiceInstance(2L)).thenReturn(list_ex);
		when(tc.run_SentimentAnalysis((Long)Matchers.any(), (String)Matchers.any(), (Long)Matchers.any(), (String)Matchers.any(), (Long)Matchers.any())).thenReturn(param);
		
	
		out = ss.REST_submitService(jsonMap);
			
		assertTrue(out.idExecution == 1L);
		assertTrue(out.idJob == 1L);
		assertTrue(out.state == States.RUNNING);
		
		verify(tc).run_SentimentAnalysis(Long.parseLong(m.getValue("instance")), m.getValue("query_terms"), Long.parseLong(m.getValue("track_time")), m.getValue("format_track_time"), Long.parseLong(m.getValue("update_freq")));
	}
	
	@Test
	//findLastExecutionByServiceInstance function return a list of executions
	//where there is only one, and this one is finished ok
	//so, we check the service name
	//and this name is not known
	public void test_submitService_7() throws Exception{
		MapUtils.MyMap<String,String> m = new MapUtils.MyMap<String,String>();
		m.setValue("service", "Unknown");
		m.setValue("instance", "2");
		String jsonMap = m.createJsonString();
		
		Execution ex = new Execution();
		ex.setState(States.FINISHED_OK);
		List <Execution> list_ex = new ArrayList<Execution>();
		list_ex.add(ex);
		BigCloudResponse.ServiceDTO out = null;
		
		when(db.getExecutionDB().findLastExecutionByServiceInstance(2L)).thenReturn(list_ex);
		
		try{
			out = ss.REST_submitService(jsonMap);
		}
		catch(WebApplicationException e){
			assertTrue(e.getResponse().getStatus() == 404);
		}		
	}
	
	@Test
	// findLastExecutionByServiceInstance function throws an exception 
	public void test_stopService_1() throws Exception{
		
		Long idInstance = 2L;
		when(db.getExecutionDB().findLastExecutionByServiceInstance(idInstance)).thenThrow(new Exception());
		
		try{
			ss.REST_stopService(idInstance);
		}
		catch(WebApplicationException e){
			assertTrue(e.getResponse().getStatus() == 500);
		}
	}
	
	@Test
	// findLastExecutionByServiceInstance function returns an empty list
	public void test_stopService_2() throws Exception{
		
		Long idInstance = 2L;
		List <Execution> list_ex = new ArrayList<Execution>();
		BigCloudResponse.ServiceDTO out = new BigCloudResponse.ServiceDTO();
		
		when(db.getExecutionDB().findLastExecutionByServiceInstance(idInstance)).thenReturn(list_ex);		
		
		out = ss.REST_stopService(idInstance);
		
		assertTrue(out == null);
	}
	
	@Test
	// findLastExecutionByServiceInstance function returns a list of size one
	// This list contains an execution which is running, so we stopped it
	// HAPPY PATH
	public void test_stopService_3() throws Exception{
		
		Long idInstance = 2L;
		
		Execution ex = new Execution();
		BC_Job job = new BC_Job();
		job.setId(1L);
		job.setIdiMathCloud(10L);
		BC_User user = new BC_User();
		user.setUserName("userName");
		user.setPassword("password");
		Service_Instance inst = new Service_Instance();
		inst.setUser(user);
		ex.setId(1L);
		ex.setJob(job);
		ex.setState(States.RUNNING);
		ex.setServiceInstance(inst);
		
		List <Execution> list_ex = new ArrayList<Execution>();
		list_ex.add(ex);
		BigCloudResponse.ServiceDTO out = new BigCloudResponse.ServiceDTO();
			
		when(db.getExecutionDB().findLastExecutionByServiceInstance(idInstance)).thenReturn(list_ex);
		AuthenticUser auser = new AuthenticUser (user.getUserName(), user.getPassword());
		PowerMockito.when(com.api.iMathCloud.stopJob(auser, ex.getJob().getIdiMathCloud())).thenReturn(true);
		
		out = ss.REST_stopService(idInstance);
				
		assertTrue(out.idExecution == ex.getId());
		assertTrue(out.idJob == ex.getJob().getId());
		assertTrue(out.state == States.CANCELLED);
	}
	
	
	@Test
	// findLastExecutionByServiceInstance function returns a list of size one
	// This list contains an execution which is already finished, so we cannot stopped it
	public void test_stopService_4() throws Exception{
		
		Long idInstance = 2L;
		
		Execution ex = new Execution();		
		Service_Instance inst = new Service_Instance();	
		ex.setId(1L);		
		ex.setState(States.FINISHED_OK);
		ex.setServiceInstance(inst);
		
		List <Execution> list_ex = new ArrayList<Execution>();
		list_ex.add(ex);
		BigCloudResponse.ServiceDTO out = new BigCloudResponse.ServiceDTO();
			
		when(db.getExecutionDB().findLastExecutionByServiceInstance(idInstance)).thenReturn(list_ex);
		
		out = ss.REST_stopService(idInstance);
		
		assertTrue(out == null);
	}
	
}
