package com.core.data;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.core.model.Execution;



@RequestScoped
public class MainDB {

	@Inject private BC_UserDB bc_userDB;
	@Inject private ServiceInstanceDB service_instance;
	@Inject private ExecutionDB execution;
	@Inject private EntityManager em;
	
	public BC_UserDB getBC_UserDB() {
    	return this.bc_userDB;
    }
	
	public ServiceInstanceDB getServiceInstanceDB(){
		return this.service_instance;
	}
	
	public ExecutionDB getExecutionDB(){
		return this.execution;
	}
	
	 public void makePersistent(Object obj) throws Exception {
	    	em.persist(obj);
	    	em.flush();
	    	
	 }
	
}
