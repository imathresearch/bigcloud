package com.core.model;

import java.io.Serializable;

import javax.persistence.*;


/**
 * Entity implementation class for Entity: Job
 *
 */
@Entity
@SequenceGenerator(name="seqJob", initialValue=5, allocationSize=1)
public class BC_Job implements Serializable {

	
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seqJob")
	private Long id;
	
	private Long id_iMathCloud;
	
	@Column(nullable=false)
	private String name;
	
	private String outputfiles;
	
	private static final long serialVersionUID = 1L;

	public BC_Job() {
		super();
	}
	
	public Long getId(){
		return this.id;
	}
	
	public void setId(Long id){
		this.id = id;
	}
	
	public Long getIdiMathCloud(){
		return this.id_iMathCloud;
	}
	
	public void setIdiMathCloud(Long id){
		this.id_iMathCloud = id;
	}
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getOutputfiles(){
		return this.outputfiles;
	}
	
	public void setOutputfiles(String outputfiles){
		this.outputfiles = outputfiles;
	}
	
   
}
