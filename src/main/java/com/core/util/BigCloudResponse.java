package com.core.util;

public class BigCloudResponse {
	
	static public class ServiceDTO{
		
		public Long idExecution;
		public Long idJob;
		public String state;
		
		public ServiceDTO(){}
		
		public ServiceDTO(Long idExecution, Long idJob, String state){
			this.idExecution = idExecution;
			this.idJob = idJob;
			this.state = state;
		}
	}

}
