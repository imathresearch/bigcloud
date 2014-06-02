package com.core.model;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Service
 *
 */
@Entity
@SequenceGenerator(name="seqService", initialValue=5, allocationSize=1)
public class Service implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seqService")
	private Long id;
	
	private String name;
	
	private String description;
	
	private static final long serialVersionUID = 1L;

	public Service() {
		super();
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
   
}
