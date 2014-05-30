package com.core.service;

import com.core.util.*;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.api.iMathCloud;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TwitterController extends AbstractController {
	
	boolean run_SentimentAnalysis(Map<String, String> params){
		
		Constants C = new Constants();
		
		boolean success = true;
		
		//1. Change label in the file by the parameters
		
		//2. Upload file to iMathCloud. Get idFile
		success = iMathCloud.uploadFile("ammartinez", Constants.job_SA_template, "");
		
		//3. Submit job. Get idJob
		
		
		
		return success;
		
	}

}
