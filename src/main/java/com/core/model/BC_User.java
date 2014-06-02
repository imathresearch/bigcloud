package com.core.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;


/**
 * Entity implementation class for Entity: User
 *
 */
@Entity

public class BC_User implements Serializable {
	
	
	@Id
	@NotNull
	@Size(min = 4, max = 25, message = "4 to 25 letters")
	@Pattern(regexp = "[A-Za-z]*", message = "Only letters")
	private String userName;
	
	private String lastName;
	private String firstName;
	private String organization;
	
	@NotNull
	@NotEmpty
	@Email(message = "Invalid format")
	private String eMail;
	
	@NotNull
	@NotEmpty
	@Size(min = 2, max = 10, message = "2-10")
	@Email(message = "Invalid format")
	private String password;
	

	private static final long serialVersionUID = 1L;

	public BC_User() {
		super();
	}
	
	public String getUserName() {
		return this.userName;
	}
 
	public void setUserName(String userName) {
		this.userName = userName;
	}   
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}   
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}   
	public String getOrganization() {
		return this.organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}   
	public String getEMail() {
		return this.eMail;
	}

	public void setEMail(String eMail) {
		this.eMail = eMail;
	}   
	
	public String getPassword(){
		return this.password;
	}
   
	public void setPassword(String password){
		this.password = password;
	}
}
