package com.core.rest;

import com.api.*;
import com.core.data.MainDB;
import com.core.model.BC_User;
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
    public StateResponse REST_requestBGSession(@PathParam("id") String userName) {
		//TODO: Authenticate the call. Make sure that it is done from index.html
		// and that the user is authenticated
		boolean session = false;
		System.out.println("new_BGSession");
		try {
			BC_User user = db.getBC_UserDB().findById(userName);
			//One user must exist
			if(user!= null){
				System.out.println("name " + user.getFirstName());
				AuthenticUser auser = new AuthenticUser(userName, "h1i1m1");
				session = iMathCloud.requestSession(auser);
			}
		}
		catch (Exception e) {
			//LOG.severe("Error creating a session for " + userName);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		StateResponse rp = new StateResponse();
		if(session){
			rp.message = "Session created"; 
			rp.code = "202";
		}
		else{
			rp.message = "Session could not be created"; 
			rp.code = "404";
		}
		return rp;
    }
	
	private class StateResponse {
		public String message;
		public String code;
		public StateResponse() {}
	}

}
