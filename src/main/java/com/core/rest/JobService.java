package com.core.rest;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.api.iMathCloud;
import com.core.data.MainDB;
import com.core.model.BC_User;
import com.util.AuthenticUser;


/**
 * A REST web service that provides access to the user jobs
 * 
 * @author ammartinez
 */
@Path("/job_service_BC")
@RequestScoped
@Stateful
public class JobService {
	
	@Inject private MainDB db;
	
	@GET
    @Path("/getJobs_BC/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String REST_getJobsBC(@PathParam("id") String userName) {
		//TODO: Authenticate the call. Make sure that it is done from index.html
		// and that the user is authenticated
		String jobs = new String();
		//System.out.println("getJobsBC");
		BC_User user = db.getBC_UserDB().findById(userName);
		
		if(user != null){
			
			AuthenticUser auser = new AuthenticUser(user.getUserName(),user.getPassword());
			try {
				jobs = iMathCloud.getJobs(auser);
			}
			catch (Exception e) {
				//LOG.severe("Error creating a session for " + userName);
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
			
			return jobs;
		}
		else{
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
    }
	
	
	public void setMainDB (MainDB db){
		this.db = db;
	}

}
