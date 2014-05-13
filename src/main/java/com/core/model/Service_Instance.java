package com.core.model;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Service_Instance
 *
 */
@Entity

public class Service_Instance implements Serializable {

	@Id
	private int id;
	
	private static final long serialVersionUID = 1L;

	public Service_Instance() {
		super();
	}
   
}
