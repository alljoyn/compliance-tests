package com.at4wireless.spring.service;

import java.util.Map;

/**
 * Interface with methods that allow end controller to communicate with database
 *
 */
public interface EndService {
	
	/**
	 * Creates XML with project configuration data
	 * 
	 * @param 	username	user whose project is going to be saved
	 * @param 	map			map with all information to be included in XML
	 * @return				path where file is allocated
	 */
	public String createXML(String username, Map<String,String[]> map);
	
	/**
	 * Returns the newest version of the Local Agent stored in server
	 * 
	 * @return	Local Agent's last version
	 */
	public String lastUpload();
}
