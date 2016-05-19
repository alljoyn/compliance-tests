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

import java.math.BigInteger;
import java.util.List;

import com.at4wireless.spring.model.ServiceFramework;

/**
 * 
 * Interface that manages database service-related access
 *
 */
public interface ServiceFrameworkDAO {
	
	/**
	 * @return		all available service frameworks
	 */
	public List<ServiceFramework> list();
	
	/**
	 * Searches services associated to a project and returns their IDs
	 * 
	 * @param 	idProject	id of the project whose services have to be returned
	 * @return				services IDs list
	 */
	public List<BigInteger> getServices(int idProject);
	
	/**
	 * Searches services associated to a project and returns their names
	 * 
	 * @param 	idProject	id of the project whose services have to be returned
	 * @return				services names list
	 */
	public List<String> getServicesByName(int idProject);
}