package com.core.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.core.model.Execution.States;

public class BigCloudResponse {
	
	static public class ServiceDTO{
		
		public Long idExecution;
		public Long idJob;
		public States state;
		public Map<String, Double> dataResult;
		
		public ServiceDTO(){}
		
		public ServiceDTO(Long idExecution, Long idJob, States state){
			this.idExecution = idExecution;
			this.idJob = idJob;
			this.state = state;
		}
		
		public ServiceDTO(Long idExecution, Long idJob, States state, Map<String, Double> dataResult){
			this.idExecution = idExecution;
			this.idJob = idJob;
			this.state = state;
			this.dataResult = dataResult;
		}
	}
	
	static public class ExecutionDTO{
		
		public String service;
		public Long idInstance;
		public Long idExecution;
		public String params;
		public States state;
		
		public ExecutionDTO(){}
		
		public ExecutionDTO(String service, Long idInstance, Long idExecution, String params, States state){
			this.service = service;
			this.idExecution = idExecution;
			this.idInstance = idInstance;
			this.params = params;
			this.state = state;
		}
		
	}
	
static public class InstanceDTO{
		
		public Long idInstance;
		public String userName;
		public String serviceName;
		
		public InstanceDTO(){}
		
		public InstanceDTO(Long idInstance, String userName, String serviceName){
			this.idInstance = idInstance;
			this.userName = userName;
			this.serviceName = serviceName;
		}
		
	}

}
