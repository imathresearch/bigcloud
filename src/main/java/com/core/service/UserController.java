package com.core.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.core.data.MainDB;
import com.core.model.BC_User;
import com.core.model.Service;
import com.core.model.Service_Instance;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class UserController extends AbstractController {
	
	@Inject private MainDB db;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createUser(String userName, String firstName, String lastName, String organization, String eMail, String password) throws Exception{
		
		System.out.println("Password " + password);
		
		BC_User user = new BC_User();		
		user.setUserName(userName);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setOrganization(organization);
		user.setEMail(eMail);
		System.out.println("Before set password");
		user.setPassword(password);
		System.out.println("After set password");

		db.makePersistent(user);
		System.out.println("After make persistent");
		
		// Now by default we assign a instance of the twitter sentiment analysis service to this new user
		Service service = db.getServiceDB().findById(1L); //1 is the id of the twitter service
		Service_Instance inst = new Service_Instance();
		inst.setUser(user);
		inst.setService(service);
		
		db.makePersistent(inst);
		
	}

}
