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

package com.at4wireless.spring.dao;

import java.util.List;

import com.at4wireless.spring.model.Parameter;

public interface ParameterDAO
{
	/**
	 * Retrieves the list of General Parameters
	 * 
	 * @return list of General Parameters
	 */
	public List<Parameter> list();
}