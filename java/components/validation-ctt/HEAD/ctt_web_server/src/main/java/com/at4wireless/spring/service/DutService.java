/*******************************************************************************
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for any
 *      purpose with or without fee is hereby granted, provided that the above
 *      copyright notice and this permission notice appear in all copies.
 *      
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *      WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *      MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *      ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *      WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *      ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *      OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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
public interface DutService {
	
	/**
	 * Stores a DUT
	 * 
	 * @param 	d	DUT information to be stored
	 * @return		true if DUT is successfully stored, false if DUT exists
	 */
	public boolean create(Dut d);
	
	/**
	 * Returns data of a given user formatted to be displayed in table
	 * 
	 * @param 	username	user whose DUT have to be loaded
	 * @return				DUT list of this user
	 */
	public List<Dut> getTableData(String username);
	
	/**
	 * Returns a certain DUT of a given user formatted to be displayed in form
	 * 
	 * @param 	username	user whose DUTs have to be loaded
	 * @param 	idDut		id of the requested DUT
	 * @return				requested DUT, if exists
	 */
	public Dut getFormData(String username, int idDut);
	
	/**
	 * Updates a DUT
	 * 
	 * @param 	d	DUT information to be updated
	 * @return		true if DUT is successfully updated, false otherwise
	 */
	public Dut update(Dut d);
	
	/**
	 * Deletes a DUT
	 * 
	 * @param 	username	user whose DUT is going to be deleted
	 * @param 	idDut		id of the DUT to be deleted
	 * @return				true if DUT is successfully removed, false otherwise
	 */
	public boolean delete(String username, int idDut);
	
	/**
	 * Stores a sample
	 * 
	 * @param 	s	sample information to be stored
	 * @return		true if sample is successfully stored, false if sample exists
	 */
	public void createSample(Sample s);
	
	/**
	 * Returns data of a given user formatted to be displayed in table
	 * 
	 * @param 	idDut	DUT whose sample have to be loaded
	 * @return			sample list of this user
	 */
	public List<Sample> getSampleData(int idDut);
	
	/**
	 * Returns a certain sample of a given user formatted to be displayed in form
	 * 
	 * @param 	idSample	id of the requested sample
	 * @return				requested sample, if exists
	 */
	public Sample getSampleFormData(int idSample);
	
	/**
	 * Updates a sample
	 * 
	 * @param 	s	sample information to be updated
	 * @return		true if sample is successfully updated, false otherwise
	 */
	public void updateSample(Sample s);
	
	/**
	 * Deletes a sample
	 * 
	 * @param 	idSample	id of the sample to be deleted
	 * @return				true if sample is successfully removed, false otherwise
	 */
	public void deleteSample(int idSample);
	
	/**
	 * Set IXIT that can be loaded from DUT and samples info
	 * 
	 * @param 	username	user whose project is being configured
	 * @param 	idDut		ID of the selected DUT
	 * @param 	listIxit	list of IXIT that contain values to be modified
	 */
	public void setValues(String username, int idDut, List<Ixit> listIxit);
	
	public boolean exists(String username, String name, int id);
	
	public int getDutId(String username, String dutName);
}
