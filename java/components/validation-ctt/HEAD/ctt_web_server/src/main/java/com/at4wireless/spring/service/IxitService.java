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

import com.at4wireless.spring.model.Ixit;

/**
 * Interface with methods that allow ixit controller to communicate with database
 *
 */
public interface IxitService {
	
	/**
	 * Returns IXIT values of an certain project
	 * 
	 * @param 	services		project services
	 * @param 	isConfigured	project configuration condition
	 * @param 	configuration	location of the configuration XML
	 * @return					list of IXIT with default or previously configured values 
	 */
	public List<Ixit> load(List<BigInteger> services, boolean isConfigured, String configuration);
	
	/**
	 * Gets IXIT of a certain service
	 * 
	 * @param 	idService	ID of the service whose IXIT are going to be returned
	 * @return				service IXIT
	 */
	public List<Ixit> getService(int idService);
	
	/**
	 * Returns data to be included in Test Report
	 * 
	 * @param 	configuration	location of the configuration XML
	 * @return					data to include in Test Report
	 */
	public List<String> pdfData(String configuration);
	
	public String add(Ixit ixit);
}