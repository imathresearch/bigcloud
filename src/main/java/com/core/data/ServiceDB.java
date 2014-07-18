package com.core.data;


import javax.inject.Inject;
import javax.persistence.EntityManager;


import com.core.model.Service;
import com.core.model.Service_Instance;

public class ServiceDB {
	
	@Inject private EntityManager em;
	 
	 /**
	 * Returns a {@link Service} from the given id
	 * @param id
	 * 		The id of the {@link Service_Instance}  
	 * @author ammartinez
	 */
	 public Service findById(Long id) {
		Service service = em.find(Service.class, id);
	   	return service;
	 }
	 
}

