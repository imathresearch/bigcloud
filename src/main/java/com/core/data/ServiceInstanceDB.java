package com.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.core.model.Service_Instance;

public class ServiceInstanceDB {
	
	@Inject private EntityManager em;
	 
	 /**
	 * Returns a {@link Service_Instance} from the given id
	 * @param id
	 * 		The id of the {@link Service_Instance}  
	 * @author ammartinez
	 */
	 public Service_Instance findById(Long id) {
		Service_Instance service_instance = em.find(Service_Instance.class, id);
	   	return service_instance;
	 }
	 
	 /**
	     * Returns all the services instances from a user with the given name 
	     * @param userName name of the user
	     * @author iMath
	 */
	 public List<Service_Instance> findByUser(String userName){
		 
		 CriteriaBuilder cb = em.getCriteriaBuilder();
		 CriteriaQuery<Service_Instance> criteria = cb.createQuery(Service_Instance.class);
		 Root<Service_Instance> inst = criteria.from(Service_Instance.class);
		 Predicate p1 = cb.equal(inst.get("bcUser").get("userName"), userName);     
	     criteria.select(inst).where(p1);
	     List<Service_Instance> instances = em.createQuery(criteria).getResultList();
	       
		 return instances;		 
	 }
}
