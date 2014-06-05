package com.core.data;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.core.model.Execution;
import com.core.model.Service_Instance;
import com.core.model.Execution.States;

@RequestScoped
public class ExecutionDB {
	
	@Inject private EntityManager em;
	 
	 /**
	 * Returns a {@link Execution} from the given id
	 * @param id
	 * 		The id of the {@link Execution}  
	 * @author ammartinez
	 */
	 public Execution findById(Long id) {
		 Execution ex = em.find(Execution.class, id);
	   	return ex;
	 }
	 
	 /**
	     * Returns all executions associated to a service instance which have a specific state 
	     * @param idServiceInstance id of the service instance
	     * @param st state of the execution
	     * @author iMath
	 */
	 public List<Execution> findByServiceInstanceState(Long idServiceInstance, States st){
	 
		 CriteriaBuilder cb = em.getCriteriaBuilder();
		 CriteriaQuery<Execution> criteria = cb.createQuery(Execution.class);
		 Root<Execution> ex = criteria.from(Execution.class);
		 Predicate p1 = cb.equal(ex.get("serviceInstance").get("id"), idServiceInstance);
		 Predicate p2 = cb.equal(ex.get("state"), st);
		 Predicate pAND = cb.and(p1,p2);
		 criteria.select(ex).where(pAND);
		 List<Execution> out = em.createQuery(criteria).getResultList();
		 return out;
	 }
	 
	 public List<Execution> findLastExecutionByServiceInstance(Long idServiceInstance) throws Exception{
		 
		 CriteriaBuilder cb = em.getCriteriaBuilder();
		 CriteriaQuery<Execution> criteria = cb.createQuery(Execution.class);
		 Root<Execution> ex = criteria.from(Execution.class);
		 Predicate p1 = cb.equal(ex.get("serviceInstance").get("id"), idServiceInstance);
		 Predicate p2 = cb.equal(ex.get("lastExecution"), true);     
		 Predicate pAND = cb.and(p1,p2);
		 criteria.select(ex).where(pAND);
	     List<Execution> out = em.createQuery(criteria).getResultList();
	     
	     if (out.size()>1) {
	            throw new Exception ("Critical: More the one last execution found" );
	     }
	       
	     return out;     
	     	
	 }

}
