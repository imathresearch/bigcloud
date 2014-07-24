package com.core.data;


import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.core.model.BC_User;
import com.core.model.Execution;


@RequestScoped
public class BC_UserDB {
	
	
	
	@Inject private EntityManager em1;
	 
	 /**
	 * Returns a {@link BC_User} from the given id
	 * @param id
	 * 		The id of the {@link BC_User}  
	 * @author ammartinez
	 */
	 public BC_User findById(String id) {
		BC_User user = em1.find(BC_User.class, id);
	   	return user;
	 }
	 
	 
	 public List<BC_User> findByMail(String email) throws Exception{
		 
		 CriteriaBuilder cb = em1.getCriteriaBuilder();
		 CriteriaQuery<BC_User> criteria = cb.createQuery(BC_User.class);
		 Root<BC_User> user = criteria.from(BC_User.class);
		 Predicate p1 = cb.equal(user.get("eMail"), email);
		 criteria.select(user).where(p1);
		 List<BC_User> out = em1.createQuery(criteria).getResultList();
		 if (out.size() > 1){
			 throw new Exception ("Critical: Several users have the same password" );
		 }
		 return out;
	 }

}
