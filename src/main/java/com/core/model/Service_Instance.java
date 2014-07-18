package com.core.model;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Service_Instance
 *
 */
@Entity
@SequenceGenerator(name="seqInstService", initialValue=20, allocationSize=1)
public class Service_Instance implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seqInstService")
	private Long id;

	@ManyToOne(optional=false) 
    @JoinColumn(name="idService", nullable=false, updatable=false)
	private Service bcService;
	
	@ManyToOne(optional=false) 
    @JoinColumn(name="idUser", nullable=false, updatable=false)
	private BC_User bcUser;
	
	private static final long serialVersionUID = 1L;

	public Service_Instance() {
		super();
	}
	
	public Long getId(){
		return this.id;
	}
	
	public void setId(Long id){
		this.id = id;
	}
	
	public Service getService(){
		return this.bcService;
	}
	
	public void setService(Service service){
		this.bcService = service;
	}
	
	public BC_User getUser(){
		return this.bcUser;
	}
	
	public void setUser(BC_User user){
		this.bcUser = user;
		
	}
   
	
}
