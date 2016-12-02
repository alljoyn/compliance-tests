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

import com.at4wireless.spring.model.Sample;

public interface SampleDAO
{
	/**
	 * Retrieves the list of samples of a certain DUT
	 * 
	 * @param dutID
	 * 			ID of the DUT whose samples are going to be retrieved
	 * 
	 * @return list of samples
	 */
	public List<Sample> list(int dutID);
	
	/**
	 * Retrieves information of a certain sample using its ID in query
	 * 
	 * @param sampleID
	 * 			ID of the sample to be retrieved
	 * 
	 * @return sample object
	 */
	public Sample get(int sampleID);
	
	/**
	 * Creates a new sample
	 * 
	 * @param sample
	 * 			data of the new sample to be stored
	 */
	public void add(Sample newSample);
	
	/**
	 * Removes a sample using its ID in query
	 * 
	 * @param sampleID
	 * 			ID of the sample to be removed
	 * 
	 * @return the number of entities deleted
	 */
	public int delete(int sampleID);
}