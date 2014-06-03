package com.core.util;

import com.core.model.Execution.States;

public class BigCloudResponse {
	
	static public class ServiceDTO{
		
		public Long idExecution;
		public Long idJob;
		public States state;
		
		public ServiceDTO(){}
		
		public ServiceDTO(Long idExecution, Long idJob, States state){
			this.idExecution = idExecution;
			this.idJob = idJob;
			this.state = state;
		}
	}

}
