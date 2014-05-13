package com.core.model;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Configuration
 *
 */

@Entity
public class Configuration implements Serializable {

	@Id
	private int id;
	
	private static final long serialVersionUID = 1L;

	public Configuration() {
		super();
	}
   
}
