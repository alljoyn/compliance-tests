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
package com.at4wireless.spring.dao;

import java.util.List;

import com.at4wireless.spring.model.Sample;

/**
 * Interface that manages database sample-related access
 *
 */
public interface SampleDAO {
	
	/**
	 * Retrieves the list of samples of a certain DUT
	 * @param 	dut		DUT ID
	 * @return			list of samples
	 */
	public List<Sample> list(int dut);
	
	/**
	 * Adds a new sample
	 * @param 	sample	sample to be added
	 */
	public void addSample(Sample sample);
	
	/**
	 * Removes a sample
	 * @param 	idSample	ID of the sample to be removed
	 */
	public void delSample(int idSample);
	
	/**
	 * Updates a sample
	 * @param 	sample	sample information to be updated
	 */
	public void saveChanges(Sample sample);
	
	/**
	 * Retrieves a certain sample
	 * @param 	idSample	ID of the sample to be returned
	 * @return				sample
	 */
	public Sample getSample(int idSample);
}
