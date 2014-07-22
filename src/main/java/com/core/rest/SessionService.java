package com.core.rest;

import java.util.ArrayList;
import java.util.List;

import com.api.*;
import com.core.data.MainDB;
import com.core.model.BC_User;
import com.core.model.Execution;
import com.core.model.Execution.States;
import com.core.model.Service_Instance;
import com.core.util.BigCloudResponse;
import com.core.util.BigCloudResponse.ExecutionDTO;
import com.core.util.BigCloudResponse.InstanceDTO;
import com.core.util.Encryptor;
import com.util.AuthenticUser;

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

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;



/**
 * A REST web service that provides access to the user session
 * 
 * @author ammartinez
 */
@Path("/session_service_BC")
@RequestScoped
@Stateful
public class SessionService {
	
	@Inject private MainDB db;
	
	@GET
    @Path("/new_BGSession/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public BigCloudResponse.SessionDTO REST_requestBGSession(@PathParam("id") String userName) {
		//TODO: Authenticate the call. Make sure that it is done from index.html
		// and that the user is authenticated
		
		Encryptor.init();
		String message = "Session created"; 
		String code = "202";
		BigCloudResponse.SessionDTO out = new BigCloudResponse.SessionDTO(message, code);
		return out;
		
		/*boolean session = false;
		try {
			
			BC_User user = db.getBC_UserDB().findById(userName);
			
			//One user must exist
			if(user!= null){
				//System.out.println("name " + user.getFirstName());
				AuthenticUser auser = new AuthenticUser(userName, user.getPassword());
				session = iMathCloud.requestSession(auser);
			}
		}
		catch (Exception e) {
			//LOG.severe("Error creating a session for " + userName);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		BigCloudResponse.SessionDTO out; 
		if(session){
			String message = "Session created"; 
			String code = "202"; //ACEPTED
			out = new BigCloudResponse.SessionDTO(message, code);
		}
		else{
			String message = "Session could not be created"; 
			String code = "404"; //NOT FOUND
			out = new BigCloudResponse.SessionDTO(message, code);
		}
		return out;*/
    }
	
	@GET
	@Path("/getInstances/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<InstanceDTO> REST_requestUserInstances(@PathParam("id") String userName) {
		
		//Encryptor.init();
		
		System.out.println("Service instance");
		List<Service_Instance> instances = db.getServiceInstanceDB().findByUser(userName);
		System.out.println("Service instance list got");
		List<InstanceDTO> out = new ArrayList<InstanceDTO>();
		
		for (Service_Instance inst: instances){
			InstanceDTO inst_dto = new InstanceDTO(inst.getId(), inst.getUser().getUserName(), inst.getService().getName());
			out.add(inst_dto);
		}
		
		return out;
	}
	
	
	@GET
    @Path("/getActiveExecutions/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ExecutionDTO> REST_requestActiveExecutions(@PathParam("id") String userName) {
				
		List<Service_Instance> instances = db.getServiceInstanceDB().findByUser(userName);
		
		List<ExecutionDTO> out = new ArrayList<ExecutionDTO>();
		
		try{
			for (Service_Instance inst : instances){
				List<Execution> ex = db.getExecutionDB().findLastExecutionByServiceInstance(inst.getId());
				if(ex.size() != 0){
					ExecutionDTO ex_dto = new ExecutionDTO(inst.getService().getName(), inst.getId(), ex.get(0).getId(), ex.get(0).getConfiguration(), ex.get(0).getState() );
					out.add(ex_dto);
				}
			}
			
			return out;
		}
		catch(Exception e){
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	/*
	public class StateResponse {
		public String message;
		public String code;
		public StateResponse() {}
	}*/

	
	public void setMainDB (MainDB db){
		this.db = db;
	}
}
