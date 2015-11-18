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

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.at4wireless.spring.model.Ics;
import com.at4wireless.spring.model.Result;

/**
 * Interface with methods that allow ics controller to communicate with database
 *
 */
public interface IcsService {
	
	/**
	 * Returns ICS values of an certain project
	 * 
	 * @param 	services		project services
	 * @param 	isConfigured	project configuration condition
	 * @param 	configuration	location of the configuration XML
	 * @return					list of ICS with default or previously configured values 
	 */
	public List<Ics> load(List<BigInteger> services, boolean isConfigured, String configuration);
	
	/**
	 * Performs SCR
	 * 
	 * @param 	services	project services
	 * @param 	map			map with ICS and modified values
	 * @return				list of result of SCR verification
	 */
	public List<Result> check(List<BigInteger> services, Map<String,String[]> map);
	
	/**
	 * Gets ICS of a certain service
	 * 
	 * @param 	idService	ID of the service whose ICS are going to be returned
	 * @return				service ICS
	 */
	public List<Ics> getService(int idService);
	
	/**
	 * Returns data to be included in Test Report
	 * 
	 * @param 	configuration	location of the configuration XML
	 * @return					data to include in Test Report
	 */
	public List<String> pdfData(String configuration);
	
	public String add(Ics ics);
}
