package com.core.data;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.core.model.Execution;

@RequestScoped
public class ExecutionDB {
	
	@Inject private EntityManager em1;
	 
	 /**
	 * Returns a {@link Execution} from the given id
	 * @param id
	 * 		The id of the {@link Execution}  
	 * @author ammartinez
	 */
	 public Execution findById(Long id) {
		 Execution ex = em1.find(Execution.class, id);
	   	return ex;
	 }

}
