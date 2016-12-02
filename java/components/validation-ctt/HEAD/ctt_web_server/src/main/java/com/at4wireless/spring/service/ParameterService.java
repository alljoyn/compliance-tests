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

import java.util.List;

import com.at4wireless.spring.model.Parameter;

/**
 * Interface with methods that allow general parameters controller to communicate with database
 *
 */
public interface ParameterService {
	
	/**
	 * Returns General Parameter values of an certain project
	 * 
	 * @param 	isConfigured	project configuration condition
	 * @param 	configuration	location of the configuration XML
	 * @return					list of General Parameters with default or previously configured values 
	 */
	public List<Parameter> load(boolean isConfigured, String configuration);
	
	/**
	 * Returns General Parameters with default values
	 * 
	 * @return	list of General Parameters
	 */
	public List<Parameter> list();
	
	/**
	 * Returns data to be included in Test Report
	 * 
	 * @param 	configuration	location of the configuration XML
	 * @return					data to include in Test Report
	 */
	public List<String> pdfData(String configuration);
}