package com.core.rest;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
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
import com.core.model.Execution;
import com.core.service.TwitterController;
import com.core.util.BigCloudResponse;
import com.util.AuthenticUser;



/**
 * A REST web service that provides access to services related to executions
 * 
 * @author ammartinez
 */
@Path("/execution_service")
@RequestScoped
@Stateful
public class ExecutionService {
	
	@Inject private MainDB db;
	@Inject private TwitterController tc;
	
	@GET
    @Path("/executionState/{idExecution}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public BigCloudResponse.ServiceDTO REST_getExecutionState(@PathParam("idExecution") Long idExecution ) {
		
		try{
			Execution ex = db.getExecutionDB().findById(idExecution);
			if(ex != null){
				String name_service = ex.getServiceInstance().getService().getName();
				BigCloudResponse.ServiceDTO out = new BigCloudResponse.ServiceDTO();
				switch(name_service){
					case "Twitter Sentiment Analysis":
						out = tc.getExecutionState(idExecution);
						break;				
					default:
						//System.out.println("Unknown Service");
		    			throw new WebApplicationException(Response.Status.NOT_FOUND);
						
				}
				return out;
			}
			else{
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
    	
		}
		catch(WebApplicationException e){
			throw e;
		}
		catch(Exception e){
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GET
    @Path("/getExecutionData/{idExecution}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public BigCloudResponse.ServiceDTO REST_getExecutionData(@PathParam("idExecution") Long idExecution ) {
		
		try{
			Execution ex = db.getExecutionDB().findById(idExecution);
			String name_service = ex.getServiceInstance().getService().getName();
			BigCloudResponse.ServiceDTO out = new BigCloudResponse.ServiceDTO();
			switch(name_service){
				case "Twitter Sentiment Analysis":
					out = tc.getExecutionData(idExecution);
					break;				
				default:
					//System.out.println("Unknown Service");
	    			throw new WebApplicationException(Response.Status.NOT_FOUND);
					
			}
			return out;
    	
		}
		catch(WebApplicationException e){
			throw e;
		}
		catch(Exception e){
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GET
    @Path("/getExecutionParcialData/{idExecution}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public BigCloudResponse.ServiceDTO REST_getExecutionParcialData(@PathParam("idExecution") Long idExecution ) {
		
		try{
			Execution ex = db.getExecutionDB().findById(idExecution);
			if(ex != null){
				String name_service = ex.getServiceInstance().getService().getName();
				BigCloudResponse.ServiceDTO out = new BigCloudResponse.ServiceDTO();
				switch(name_service){
					case "Twitter Sentiment Analysis":
						out = tc.getExecutionParcialData(idExecution);
						break;				
					default:
						//System.out.println("Unknown Service");
		    			throw new WebApplicationException(Response.Status.NOT_FOUND);
						
				}
				return out;
			}
			else{
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
    	
		}
		catch(WebApplicationException e){
			throw e;
		}
		catch(Exception e){
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GET
    @Path("/stopExecution/{idExecution}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public boolean REST_stopExecution(@PathParam("idExecution") Long idExecution ) {
		
		boolean success = false;
		try{
			Execution ex = db.getExecutionDB().findById(idExecution);
			Long idJob_iMathCloud = ex.getJob().getIdiMathCloud();
			BC_User user = ex.getServiceInstance().getUser();
			AuthenticUser auser = new AuthenticUser(user.getUserName(), user.getPassword());
			
			success = iMathCloud.stopJob(auser, idJob_iMathCloud);
    	
		}
		catch(WebApplicationException e){
			throw e;
		}
		catch(Exception e){
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		return success;
		
	}
	
	public void setMainDB (MainDB db){
		this.db = db;
	}
	
	public void setTwitterController(TwitterController tc){
		this.tc = tc;
	}

}
