package com.core.model;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Configuration
 *
 */

@Entity
@SequenceGenerator(name="seqExecution", initialValue=5, allocationSize=1)
public class Execution implements Serializable {

	public static enum States {CREATED, RUNNING, PAUSED, CANCELLED, FINISHED_OK, FINISHED_ERROR};
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seqExecution")
	private Long id;
	
	@ManyToOne(optional=false) 
    @JoinColumn(name="idServiceInstance", nullable=false, updatable=false)
	private Service_Instance serviceInstance;
	
	@OneToOne(cascade = CascadeType.ALL)
	private BC_Job job;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Result_Data result;
	
	@Column(nullable=false)
	private States state;
	
	private boolean lastExecution;
	
	private String configuration;
	
	private static final long serialVersionUID = 1L;

	public Execution() {
		super();
	}
	
	public Long getId(){
		return this.id;
	}
   
	public void setId(Long id){
		this.id = id;
	}
	
	public Service_Instance getServiceInstance(){
		return this.serviceInstance;
	}
	
	public void setServiceInstance(Service_Instance se){
		this.serviceInstance = se;
		
	}
	
	public States getState(){
		return this.state;
	}
	
	public void setState(States state){
		this.state = state;
	}
	
	public String getConfiguration(){
		return this.configuration;
	}
	
	public void setConfiguration(String config){
		this.configuration = config;
	}
	
	public BC_Job getJob(){
		return this.job;
	}
	
	public void setJob(BC_Job job){
		this.job = job;
	}
	
	public Result_Data getResult(){
		return this.result;
	}
	
	public void setResult(Result_Data result){
		this.result = result;
	}
	
	public boolean getLastExecution(){
		return this.lastExecution;
	}
	
	public void setLastExecution(boolean lastExecution){
		this.lastExecution = lastExecution;
	}
}
