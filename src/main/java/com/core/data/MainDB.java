package com.core.data;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;




@RequestScoped
public class MainDB {

	@Inject private BC_UserDB bc_userDB;
	
	public BC_UserDB getBC_UserDB() {
    	return this.bc_userDB;
    }
	
}
