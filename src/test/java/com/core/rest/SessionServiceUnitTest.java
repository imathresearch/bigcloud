package com.core.rest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.core.data.BC_UserDB;
import com.core.data.ExecutionDB;
import com.core.data.MainDB;
import com.core.data.ServiceInstanceDB;
import com.core.model.BC_User;
import com.core.model.Execution;
import com.core.model.Execution.States;
import com.core.model.Service;
import com.core.model.Service_Instance;
import com.core.service.TwitterController;
import com.core.util.BigCloudResponse;
import com.core.util.BigCloudResponse.ExecutionDTO;
import com.exception.iMathAPIException;
import com.util.AuthenticUser;


@RunWith(PowerMockRunner.class)
@PrepareForTest(com.api.iMathCloud.class)

public class SessionServiceUnitTest {
	
	SessionService ss = new SessionService();
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
	    ss.setMainDB(db);

	    PowerMockito.mockStatic(com.api.iMathCloud.class);
    }
	
	@Test
	// The username does not exist in the DB
	public void test_requestBGSession_1(){
	
		String userName = "userName";
		
		when(db.getBC_UserDB().findById(userName)).thenReturn(null);
		
		BigCloudResponse.SessionDTO out;
		
		out = ss.REST_requestBGSession(userName);
		
		assertTrue(out.message.equals("Session could not be created"));
		assertTrue(out.code.equals("404"));		
	}
	
	@Test
	// The username exists in the DB
	// but the authentification is not correct, so the method of the API return false
	public void test_requestBGSession_2() throws iMathAPIException{
	
		String userName = "userName";
		String password = "password";
		BC_User user = new BC_User();
		user.setUserName(userName);
		user.setPassword(password);
		
		
		when(db.getBC_UserDB().findById(userName)).thenReturn(user);
		PowerMockito.when(com.api.iMathCloud.requestSession((AuthenticUser)Matchers.any())).thenReturn(false);
		
		BigCloudResponse.SessionDTO out;
		
		out = ss.REST_requestBGSession(userName);
		
		assertTrue(out.message.equals("Session could not be created"));
		assertTrue(out.code.equals("404"));		
	}
	
	@Test
	// The username exists in the DB
	// but an exception is thrown by the API method
	public void test_requestBGSession_3() throws iMathAPIException{
	
		String userName = "userName";
		String password = "password";
		BC_User user = new BC_User();
		user.setUserName(userName);
		user.setPassword(password);
		
		when(db.getBC_UserDB().findById(userName)).thenReturn(user);
		PowerMockito.when(com.api.iMathCloud.requestSession((AuthenticUser)Matchers.any())).thenThrow(new iMathAPIException(iMathAPIException.API_ERROR.INTERNAL_SERVER_ERROR));
		
		try{
			ss.REST_requestBGSession(userName);
		}
		catch(WebApplicationException e){
			assertTrue(e.getResponse().getStatus() == 404);
		}
			
	}	
	
	@Test
	// The username exists in the DB
	// and the session is correctly requested
	// HAPPY PATH
	public void test_requestBGSession_4() throws iMathAPIException{
	
		String userName = "userName";
		String password = "password";
		BC_User user = new BC_User();
		user.setUserName(userName);
		user.setPassword(password);
		
		when(db.getBC_UserDB().findById(userName)).thenReturn(user);
		PowerMockito.when(com.api.iMathCloud.requestSession((AuthenticUser)Matchers.any())).thenReturn(true);
		
		BigCloudResponse.SessionDTO out = ss.REST_requestBGSession(userName);
		
		assertTrue(out.message.equals("Session created"));
		assertTrue(out.code.equals("202"));		
			
	}
	
	@Test
	// The user does not have instance of any service
	public void test_requestActiveExecutions_1(){
		String userName = "userName";
		
		List<Service_Instance> list_instances = new ArrayList<Service_Instance>();
		List<ExecutionDTO> out;
		
		when(db.getServiceInstanceDB().findByUser(userName)).thenReturn(list_instances);
		
		out = ss.REST_requestActiveExecutions(userName);
		
		assertTrue(out.size() == 0);
		
	}
	
	@Test
	// The user have a service instance
	// but there is not execution related to this instance
	public void test_requestActiveExecutions_2() throws Exception{
		String userName = "userName";
		Service_Instance inst = new Service_Instance();
		inst.setId(1L);
		List<Service_Instance> list_instances = new ArrayList<Service_Instance>();
		list_instances.add(inst);		
		List<Execution> list_executions = new ArrayList<Execution>();
		
		when(db.getServiceInstanceDB().findByUser(userName)).thenReturn(list_instances);
		when(db.getExecutionDB().findLastExecutionByServiceInstance(inst.getId())).thenReturn(list_executions);
		
		List<ExecutionDTO> out;
		out = ss.REST_requestActiveExecutions(userName);
		
		assertTrue(out.size() == 0);
		
	}
	
	@Test
	// The user have a service instance 
	// and an execution of this instance is running (it is not important the state)
	// HAPPY PATH
	public void test_requestActiveExecutions_3() throws Exception{
		String userName = "userName";
		Service_Instance inst = new Service_Instance();
		Service service = new Service();
		service.setName("service");
		inst.setId(1L);
		inst.setService(service);
		List<Service_Instance> list_instances = new ArrayList<Service_Instance>();
		list_instances.add(inst);		
		List<Execution> list_executions = new ArrayList<Execution>();
		Execution ex = new Execution();
		ex.setId(1L);
		ex.setConfiguration("configuration");
		ex.setState(States.RUNNING);
		list_executions.add(ex);
		
		when(db.getServiceInstanceDB().findByUser(userName)).thenReturn(list_instances);
		when(db.getExecutionDB().findLastExecutionByServiceInstance(inst.getId())).thenReturn(list_executions);
		
		List<ExecutionDTO> out;
		out = ss.REST_requestActiveExecutions(userName);
		
		assertTrue(out.size() == 1);
		assertTrue(out.get(0).idExecution == ex.getId());
		assertTrue(out.get(0).idInstance == inst.getId());
		assertTrue(out.get(0).service == inst.getService().getName());
		assertTrue(out.get(0).state == ex.getState());
		
	}
	
	@Test
	// The user have a service instance
	// and the function findLastExecutionByServiceInstance throws an exception
	// because more than one active executions have been found 
	public void test_requestActiveExecutions_4() throws Exception{
		String userName = "userName";
		Service_Instance inst = new Service_Instance();
		inst.setId(1L);
		List<Service_Instance> list_instances = new ArrayList<Service_Instance>();
		list_instances.add(inst);		
	
		
		when(db.getServiceInstanceDB().findByUser(userName)).thenReturn(list_instances);
		when(db.getExecutionDB().findLastExecutionByServiceInstance(inst.getId())).thenThrow(new Exception());
		
		try{
			ss.REST_requestActiveExecutions(userName);
		}
		catch(WebApplicationException e){
			assertTrue(e.getResponse().getStatus() == 500);			
		}
		
	}

}
