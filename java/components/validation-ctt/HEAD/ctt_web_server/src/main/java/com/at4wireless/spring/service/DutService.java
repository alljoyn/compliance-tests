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

import com.at4wireless.spring.model.Dut;
import com.at4wireless.spring.model.Ixit;
import com.at4wireless.spring.model.Sample;

/**
 * Interface with methods that allow dut controller to communicate with database
 *
 */
public interface DutService
{
	/**
	 * Stores a new DUT
	 * 
	 * @param newDut
	 * 			DUT information to be stored
	 * 
	 * @return true if DUT is successfully stored, false if DUT exists
	 */
	public boolean create(Dut newDut);
	
	/**
	 * Returns data of a given user formatted to be displayed in table
	 * 
	 * @param username
	 * 			user whose DUT have to be loaded
	 * 
	 * @return DUT list of this user
	 */
	public List<Dut> getTableData(String username);
	
	/**
	 * Returns a certain DUT of a given user formatted to be displayed in form
	 * 
	 * @param username
	 * 			user whose DUTs have to be loaded
	 * @param idDut	
	 * 			id of the requested DUT
	 * 
	 * @return requested DUT, if exists
	 */
	public Dut getFormData(String username, int idDut);
	
	/**
	 * Updates a DUT
	 * 
	 * @param dutUpdateInfo
	 * 			DUT information to be updated
	 * 
	 * @return true if DUT is successfully updated, false otherwise
	 */
	public Dut update(Dut dutUpdateInfo);
	
	/**
	 * Deletes a DUT
	 * 
	 * @param username
	 * 			user whose DUT is going to be deleted
	 * @param idDut	
	 * 			id of the DUT to be deleted
	 * 
	 * @return true if DUT is successfully removed, false otherwise
	 */
	public boolean delete(String username, int idDut);
	
	/**
	 * Stores a sample
	 * 
	 * @param s
	 * 			sample information to be stored
	 * 
	 * @return true if sample is successfully stored, false if sample exists
	 */
	public void createSample(Sample s);
	
	/**
	 * Returns data of a given user formatted to be displayed in table
	 * 
	 * @param idDut
	 * 			DUT whose sample have to be loaded
	 * 
	 * @return sample list of this user
	 */
	public List<Sample> getSampleData(int idDut);
	
	/**
	 * Returns a certain sample of a given user formatted to be displayed in form
	 * 
	 * @param idSample
	 * 			id of the requested sample
	 * 
	 * @return requested sample, if exists
	 */
	public Sample getSampleFormData(int idSample);
	
	/**
	 * Updates a sample
	 * 
	 * @param sampleUpdateInfo
	 * 			sample information to be updated
	 * @param username
	 * 			user that created the DUT whose sample is going to be updated
	 * 			[This is necessary in order to update the Configuration.xml if the project already exists]
	 */
	public void updateSample(Sample sampleUpdateInfo, String username);
	
	/**
	 * Deletes a sample
	 * 
	 * @param idSample
	 * 			id of the sample to be deleted
	 * 
	 * @return true if sample is successfully removed, false otherwise
	 */
	public void deleteSample(int idSample);
	
	/**
	 * Set IXIT that can be loaded from DUT and samples info
	 * 
	 * @param username
	 * 			user whose project is being configured
	 * @param dutID
	 * 			ID of the selected DUT
	 * @param listOfIxit
	 * 			list of IXIT that contain values to be modified
	 */
	public void loadIxitValues(String username, int dutID, List<Ixit> listOfIxit);
	
	public boolean exists(String username, String name, int id);
	
	public int getDutId(String username, String dutName);
}