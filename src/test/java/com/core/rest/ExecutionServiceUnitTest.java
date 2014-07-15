package com.core.rest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;

import com.core.data.ExecutionDB;
import com.core.data.MainDB;
import com.core.model.Execution;
import com.core.model.Service;
import com.core.model.Service_Instance;
import com.core.model.Execution.States;
import com.core.service.TwitterController;
import com.core.util.BigCloudResponse;


public class ExecutionServiceUnitTest {

	ExecutionService ss = new ExecutionService();
	// We do not mock the MainServiceDB. We mock the inside elements.
    private MainDB db;
	
    @Mock private EntityManager em;
    @Mock private ExecutionDB exDB;
    @Mock private TwitterController tc;
	
	
	@Before
    public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    
	    db = new MainDB();
	    db.setEntityManager(em);
	    db.setExecutionDB(exDB);
	    
	    // We simulate the injections	
	    ss.setMainDB(db);
	    ss.setTwitterController(tc);
	    PowerMockito.mockStatic(com.api.iMathCloud.class);
    }

	@Test
	// The execution does not exist
	public void test_getExecutionState_1(){
		Long idExecution = 1L;
		
		when (db.getExecutionDB().findById(idExecution)).thenReturn(null);
		try{
			ss.REST_getExecutionState(idExecution);
		}
		catch(WebApplicationException e){
			System.out.println("Catching exception 1");
			assertTrue(e.getResponse().getStatus() == 404);
		}
	}
	
	@Test
	// The execution exists
	// but the name of the service does not match with any available
	public void test_getExecutionState_2(){
		Long idExecution = 1L;
		Execution ex = new Execution();
		ex.setId(1L);
		Service service = new Service();
		service.setName("service");
		Service_Instance inst = new Service_Instance();
		inst.setId(1L);
		inst.setService(service);
		ex.setServiceInstance(inst);
		
		
		when (db.getExecutionDB().findById(idExecution)).thenReturn(ex);
		try{
			ss.REST_getExecutionState(idExecution);
		}
		catch(WebApplicationException e){
			System.out.println("Catching exception 2");
			assertTrue(e.getResponse().getStatus() == 404);
		}
	}
	
	@Test
	// The execution exists
	// the name of the service is correct
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
		ex.setServiceInstance(inst);
		
		BigCloudResponse.ServiceDTO param = new BigCloudResponse.ServiceDTO(1L, 1L, States.RUNNING);
		
		when (db.getExecutionDB().findById(idExecution)).thenReturn(ex);
		when(tc.getExecutionState(idExecution)).thenReturn(param);
		
		
		BigCloudResponse.ServiceDTO out;
		out = ss.REST_getExecutionState(idExecution);
		
		assertTrue(out.idExecution == 1L);
		assertTrue(out.idJob == 1L);
		assertTrue(out.state == States.RUNNING);
		
		verify(tc, times(1)).getExecutionState(idExecution);
		
	}
	
	@Test
	// The execution exists
	// the name of the service is correct
	// but tc.getExecutionState throws an exception
	public void test_getExecutionState_4() throws Exception{
		Long idExecution = 1L;
		Execution ex = new Execution();
		ex.setId(1L);
		Service service = new Service();
		service.setName("Twitter Sentiment Analysis");
		Service_Instance inst = new Service_Instance();
		inst.setId(1L);
		inst.setService(service);
		ex.setServiceInstance(inst);
				
		when (db.getExecutionDB().findById(idExecution)).thenReturn(ex);
		when(tc.getExecutionState(idExecution)).thenThrow(new WebApplicationException(Response.Status.NOT_FOUND));
		
		
		try{
			ss.REST_getExecutionState(idExecution);
		}
		catch(WebApplicationException e){
			System.out.println("Catching exception 4");	
			assertTrue(e.getResponse().getStatus() == 404);
		}
				
	}
	
	
	@Test
	// The execution does not exist
	public void test_getExecutionParcialData_1(){
		Long idExecution = 1L;
		
		when (db.getExecutionDB().findById(idExecution)).thenReturn(null);
		try{
			ss.REST_getExecutionParcialData(idExecution);
		}
		catch(WebApplicationException e){
			System.out.println("Catching exception 1");
			assertTrue(e.getResponse().getStatus() == 404);
		}
	}
	
	@Test
	// The execution exists
	// but the name of the service does not match with any available
	public void test_getExecutionParcialData_2(){
		Long idExecution = 1L;
		Execution ex = new Execution();
		ex.setId(1L);
		Service service = new Service();
		service.setName("service");
		Service_Instance inst = new Service_Instance();
		inst.setId(1L);
		inst.setService(service);
		ex.setServiceInstance(inst);
		
		
		when (db.getExecutionDB().findById(idExecution)).thenReturn(ex);
		try{
			ss.REST_getExecutionParcialData(idExecution);
		}
		catch(WebApplicationException e){
			System.out.println("Catching exception 2");
			assertTrue(e.getResponse().getStatus() == 404);
		}
	}
	
	@Test
	// The execution exists
	// the name of the service is correct
	// HAPPY PATH
	public void test_getExecutionParcialData_3() throws Exception{
		Long idExecution = 1L;
		Execution ex = new Execution();
		ex.setId(1L);
		Service service = new Service();
		service.setName("Twitter Sentiment Analysis");
		Service_Instance inst = new Service_Instance();
		inst.setId(1L);
		inst.setService(service);
		ex.setServiceInstance(inst);
		
		BigCloudResponse.ServiceDTO param = new BigCloudResponse.ServiceDTO(1L, 1L, States.RUNNING);
		
		when (db.getExecutionDB().findById(idExecution)).thenReturn(ex);
		when(tc.getExecutionParcialData(idExecution)).thenReturn(param);
		
		
		BigCloudResponse.ServiceDTO out;
		out = ss.REST_getExecutionParcialData(idExecution);
		
		assertTrue(out.idExecution == 1L);
		assertTrue(out.idJob == 1L);
		assertTrue(out.state == States.RUNNING);
		
		verify(tc, times(1)).getExecutionParcialData(idExecution);
		
	}
	
	@Test
	// The execution exists
	// the name of the service is correct
	// but tc.getExecutionParcialData throws an exception
	public void test_getExecutionParcialData_4() throws Exception{
		Long idExecution = 1L;
		Execution ex = new Execution();
		ex.setId(1L);
		Service service = new Service();
		service.setName("Twitter Sentiment Analysis");
		Service_Instance inst = new Service_Instance();
		inst.setId(1L);
		inst.setService(service);
		ex.setServiceInstance(inst);
				
		when (db.getExecutionDB().findById(idExecution)).thenReturn(ex);
		when(tc.getExecutionParcialData(idExecution)).thenThrow(new WebApplicationException(Response.Status.NOT_FOUND));
		
		
		try{
			ss.REST_getExecutionParcialData(idExecution);
		}
		catch(WebApplicationException e){
			System.out.println("Catching exception 4");	
			assertTrue(e.getResponse().getStatus() == 404);
		}
				
	}
	
	
	
}
