package com.core.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.api.iMathCloud;
import com.core.data.MainDB;
import com.core.model.BC_User;
import com.core.model.Execution;
import com.core.model.Execution.States;
import com.core.service.TwitterController;
import com.core.util.BigCloudResponse;
import com.core.util.MapUtils;
import com.util.AuthenticUser;




/**
 * A REST web service that provides access to the different service controllers
 * 
 * @author ipinyol
 */
@Path("/service_service")
@RequestScoped
@Stateful
public class ServiceService {
	
	@Inject private TwitterController tc;
	@Inject private MainDB db;
	@Inject private Logger LOG;
	
	@POST
    @Path("/submitService/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public BigCloudResponse.ServiceDTO REST_submitService(String json_params) {
		
		try{
			
			//System.out.println("Submit service " + json_params);
			BigCloudResponse.ServiceDTO out = new BigCloudResponse.ServiceDTO();
	    	MapUtils.MyMap <String, String> mm = new MapUtils.MyMap<String, String>();   	
	    	mm.jsonToMap(json_params);
	    	//System.out.println("jsonToMap " + json_params);
	    	    	
	    	String service_name = mm.getValue("service");
	    	switch(service_name){
	    		case "SAForm":
	    			Long id_ServiceInstance = Long.parseLong(mm.getValue("instance"));
	    			String query_terms = mm.getValue("query_terms");
	    			Long track_time = Long.parseLong(mm.getValue("track_time"));
	    			String formatted_track_time = mm.getValue("format_track_time");
	    			Long update_freq = Long.parseLong(mm.getValue("update_freq"));	    			
	    			out = tc.run_SentimentAnalysis(id_ServiceInstance, query_terms, track_time, formatted_track_time, update_freq);
	    			//System.out.println("Confirmation run_SentimentAnalysis ");
	    			break;
	    		default:
	    			//System.out.println("Unknown Service");
	    			throw new WebApplicationException(Response.Status.NOT_FOUND);
	    			//break;   				
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
    @Path("/getLastExecution/{id_instance}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public BigCloudResponse.ServiceDTO REST_getLastExecution(@PathParam("id_instance") Long id_instance ) {
		
		BigCloudResponse.ServiceDTO out = null;
		
		try{
			List<Execution> list_exc = db.getExecutionDB().findLastExecutionByServiceInstance(id_instance);
			if(list_exc.size() == 1){
				out = new BigCloudResponse.ServiceDTO(list_exc.get(0).getId(), list_exc.get(0).getJob().getId(), list_exc.get(0).getState());
			}
    	
		}
		catch(WebApplicationException e){
			throw e;
		}
		catch(Exception e){
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		return out;
		
	}
	
	@GET
    @Path("/stopService/{idInstance}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public BigCloudResponse.ServiceDTO REST_stopService(@PathParam("idInstance") Long idInstance ) {
		
		BigCloudResponse.ServiceDTO out = null;
		try{
			
			//1. We get the last execution of that service instance
			List<Execution> list_exc = db.getExecutionDB().findLastExecutionByServiceInstance(idInstance);
			
			//The size of list_exc can be 1 or 0
			//1 means that the service has been executed at least once
			//0 means that the service has never been executed
			if(list_exc.size() == 1){
				//2. Check if the execution associated to this service instance is running or paused.
				Execution ex = list_exc.get(0);
				States st = ex.getState();
				if ( st == Execution.States.RUNNING || st == Execution.States.PAUSED){
					// In positive case, we stop the execution.
					Long idJob_iMathCloud = ex.getJob().getIdiMathCloud();
					BC_User user = ex.getServiceInstance().getUser();
					AuthenticUser auser = new AuthenticUser(user.getUserName(), user.getPassword());					
					boolean success = iMathCloud.stopJob(auser, idJob_iMathCloud);
					ex.setState(Execution.States.CANCELLED);
					out = new BigCloudResponse.ServiceDTO(ex.getId(), ex.getJob().getId(), ex.getState());					
				}								
			}
			//3. If there is no execution or the last execution is not 'active', out = null is returned
		}
		catch(WebApplicationException e){
			throw e;
		}
		catch(Exception e){
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		return out;		
	}
	
	
	
		
}
