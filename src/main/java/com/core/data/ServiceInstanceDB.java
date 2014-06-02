package com.core.data;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.core.model.Service_Instance;

public class ServiceInstanceDB {
	
	@Inject private EntityManager em1;
	 
	 /**
	 * Returns a {@link Service_Instance} from the given id
	 * @param id
	 * 		The id of the {@link Service_Instance}  
	 * @author ammartinez
	 */
	 public Service_Instance findById(Long id) {
		Service_Instance service_instance = em1.find(Service_Instance.class, id);
	   	return service_instance;
	 }

}
