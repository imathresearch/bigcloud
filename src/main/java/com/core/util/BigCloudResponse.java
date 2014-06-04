package com.core.util;

import java.util.Map;

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

}
