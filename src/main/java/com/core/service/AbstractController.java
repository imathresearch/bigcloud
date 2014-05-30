package com.core.service;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.core.data.MainDB;


public class AbstractController {
	
	 	@Inject protected Logger LOG;
	    @Inject protected MainDB db;
	    @Inject protected EntityManager em;

	    

}
