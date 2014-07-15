package com.core.rest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

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

import com.api.iMathCloud;
import com.core.data.BC_UserDB;
import com.core.data.ExecutionDB;
import com.core.data.MainDB;
import com.core.model.BC_User;
import com.core.service.TwitterController;
import com.exception.iMathAPIException;
import com.util.AuthenticUser;

@RunWith(PowerMockRunner.class)
@PrepareForTest(com.api.iMathCloud.class)
public class JobServiceUnitTest {

	JobService js = new JobService();
	// We do not mock the MainServiceDB. We mock the inside elements.
    private MainDB db;
	
    @Mock private EntityManager em;
    @Mock private BC_UserDB user;
	
	@Before
    public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    
	    db = new MainDB();
	    db.setEntityManager(em);
	    db.setBC_UserDB(user);
	    
	    // We simulate the injections	
	    js.setMainDB(db);
	  
	    PowerMockito.mockStatic(com.api.iMathCloud.class);
    }
	
	@Test
	// the username of the jobs does not exist
	public void test_getJobsBC_1(){
		String userName = "userName";
		
		when(db.getBC_UserDB().findById(userName)).thenReturn(null);
		
		try{
			js.REST_getJobsBC(userName);
		}
		catch(WebApplicationException e){
			System.out.println("Catching Exception 1");
			assertTrue(e.getResponse().getStatus() == 404);
		}
	}
	
	@Test
	// the username of the jobs exists
	// but  iMathCloud.getJobs throws an exception
	public void test_getJobsBC_2() throws iMathAPIException{
		String userName = "userName";
		BC_User user = new BC_User();
		user.setUserName(userName);
		user.setPassword("password");
		
		when(db.getBC_UserDB().findById(userName)).thenReturn(user);
		PowerMockito.when(com.api.iMathCloud.getJobs((AuthenticUser)Matchers.any())).thenThrow(new iMathAPIException(iMathAPIException.API_ERROR.INTERNAL_SERVER_ERROR));
		
		try{
			js.REST_getJobsBC(userName);
		}
		catch(WebApplicationException e){
			System.out.println("Catching Exception 2");
			assertTrue(e.getResponse().getStatus() == 404);
		}
	}
	
	@Test
	// the username of the jobs exists
	// but  iMathCloud.getJobs returns a string that represents the jobs
	public void test_getJobsBC_3() throws iMathAPIException{
		String userName = "userName";
		BC_User user = new BC_User();
		user.setUserName(userName);
		user.setPassword("password");
		String jobs = "job1,job2,job3";
		
		when(db.getBC_UserDB().findById(userName)).thenReturn(user);
		PowerMockito.when(com.api.iMathCloud.getJobs((AuthenticUser)Matchers.any())).thenReturn(jobs);
		
		String out;
		out = js.REST_getJobsBC(userName);
		
		assertTrue(out.equals(jobs));
		
	}

}
