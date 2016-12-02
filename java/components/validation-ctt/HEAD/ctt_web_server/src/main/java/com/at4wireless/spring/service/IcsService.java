/*******************************************************************************
 *  * 
 *      Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *      Source Project Contributors and others.
 *      
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/

package com.at4wireless.spring.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.at4wireless.spring.model.Ics;
import com.at4wireless.spring.model.Result;

/**
 * Interface with methods that allow ics controller to communicate with database
 *
 */
public interface IcsService
{
	/**
	 * Returns ICS values of an certain project
	 * 
	 * @param services
	 * 			project services
	 * @param isConfigured
	 * 			project configuration condition
	 * @param configuration
	 * 			location of the configuration XML
	 * 
	 * @return list of ICS with default or previously configured values 
	 */
	public List<Ics> load(List<BigInteger> services, boolean isConfigured, String configuration);
	
	/**
	 * Performs SCR
	 * 
	 * @param services
	 * 			project services
	 * @param map
	 * 			map with ICS and modified values
	 * 
	 * @return list of result of SCR verification
	 */
	public List<Result> check(List<BigInteger> services, Map<String,String[]> map);
	
	/**
	 * Gets ICS of a certain service
	 * 
	 * @param idService
	 * 			ID of the service whose ICS are going to be returned
	 * 
	 * @return service ICS
	 */
	public List<Ics> getService(int idService);
	
	/**
	 * Returns data to be included in Test Report
	 * 
	 * @param configuration
	 * 			location of the configuration XML
	 * 
	 * @return data to include in Test Report
	 */
	public List<String> pdfData(String configuration);
	
	/**
	 * Stores a new ICS
	 * 
	 * @param ics
	 * 			ICS information to be stored
	 * 
	 * @return text with the result of the operation
	 */
	public String add(Ics ics);
}