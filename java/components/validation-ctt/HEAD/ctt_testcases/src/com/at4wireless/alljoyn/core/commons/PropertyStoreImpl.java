/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package com.at4wireless.alljoyn.core.commons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.services.common.PropertyStore;
import org.alljoyn.services.common.PropertyStoreException;

public class PropertyStoreImpl  implements PropertyStore
{
	private Set < String > m_AnnounceKeys = new HashSet < String >();
	
	private Map < String, Map < String, Object > > m_DataMap = new HashMap < 
			String, Map < String, Object > >();

	public PropertyStoreImpl(Map < String, Map < String, Object > > defaultMap)
	{
		// Initialize set of Announce keys
		m_AnnounceKeys.add("AppId");
		m_AnnounceKeys.add("DefaultLanguage");
		m_AnnounceKeys.add("DeviceName");
		m_AnnounceKeys.add("DeviceId");
		m_AnnounceKeys.add("AppName");
		m_AnnounceKeys.add("Manufacturer");
		m_AnnounceKeys.add("ModelNumber");

		m_DataMap.putAll(defaultMap);


	}

	public PropertyStoreImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void readAll(String languageTag, Filter filter, Map<String, 
			Object> dataMap) throws PropertyStoreException
	{
		for (Entry<String, Map<String, Object>> entry :m_DataMap.entrySet())
		{
			if (entry.getValue().containsKey(languageTag))
			{
				String lang = "";
				if (entry.getValue().containsKey(languageTag))
				{
					lang = languageTag;
				}
				switch (filter) 
				{
				case READ:
				{
					//System.out.println("Leyendo: "+entry.getValue());
					dataMap.put(entry.getKey(), 
							entry.getValue().get(lang));
				}
				break;
				case ANNOUNCE:
					//System.out.println("Leyendo Todo");
					if (m_AnnounceKeys.contains(entry.getKey()))
					{
						dataMap.put(entry.getKey(), 
								entry.getValue().get(lang));
					}
					break;
				}
			}
		}
	}
	
	@Override
	public void reset(String key, String languageTag) throws 
	PropertyStoreException {
	}

	@Override
	public void resetAll() throws PropertyStoreException {
	}

	@Override
	public void update(String key, String languageTag, Object newValue)
			throws PropertyStoreException {
	}

	public void setValue(String aboutkey, String value, String lang)
	{
		Map < String, Object > defaultMap = new HashMap <String, Object>();
		defaultMap.put(lang, value);
		
		m_DataMap.put(aboutkey, defaultMap);	
	}

	public void setValue(String aboutkeys, UUID appId, String lang)
	{
		UUID uuid = appId;
		Map < String, Object > defaultAppId = new HashMap <String, Object>();
		defaultAppId.put(lang, uuid);
		
		m_DataMap.put(AboutKeys.ABOUT_APP_ID, defaultAppId);
	}



}