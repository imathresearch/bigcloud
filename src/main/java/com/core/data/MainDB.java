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
	@Inject private ServiceDB service;
	
	public BC_UserDB getBC_UserDB() {
    	return this.bc_userDB;
    }
	
	public void setBC_UserDB(BC_UserDB user) {
    	this.bc_userDB = user;
    }
	
	public ServiceInstanceDB getServiceInstanceDB(){
		return this.service_instance;
	}
	
	public void setServiceInstanceDB(ServiceInstanceDB inst){
		this.service_instance = inst;
	}
	
	public ExecutionDB getExecutionDB(){
		return this.execution;
	}
	
	public void setExecutionDB(ExecutionDB e){
		this.execution = e;
	}
	
	public void setEntityManager(EntityManager em){
		this.em = em;
	}
	
	public ServiceDB getServiceDB(){
		return this.service;
	}
	
	public void setServiceDB(ServiceDB s){
		this.service = s;
	}
	
	 public void makePersistent(Object obj) throws Exception {
	    	em.persist(obj);
	    	em.flush();
	    	
	 }
	
}
