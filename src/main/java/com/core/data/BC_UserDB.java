package com.core.data;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.core.model.BC_User;


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

}
