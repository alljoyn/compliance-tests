/*******************************************************************************
 * Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *      Source Project (AJOSP) Contributors and others.
 *
 *      SPDX-License-Identifier: Apache-2.0
 *
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Copyright 2016 Open Connectivity Foundation and Contributors to
 *      AllSeen Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for
 *      any purpose with or without fee is hereby granted, provided that the
 *      above copyright notice and this permission notice appear in all
 *      copies.
 *
 *       THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *       WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *       WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *       AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *       DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *       PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *       TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *       PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/

package com.at4wireless.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import com.at4wireless.spring.common.XMLManager;
import com.at4wireless.spring.dao.DutDAO;
import com.at4wireless.spring.dao.ProjectDAO;
import com.at4wireless.spring.dao.SampleDAO;
import com.at4wireless.spring.model.Dut;
import com.at4wireless.spring.model.Ixit;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.Sample;

@Service
public class DutServiceImpl implements DutService
{	
	@Autowired
	private ProjectDAO projectDAO;
	
	@Autowired
	private DutDAO dutDao;
	
	@Autowired
	private SampleDAO sampleDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Dut> getTableData(String username)
	{
		return dutDao.list(username);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Dut getFormData(String username, int idDut)
	{
		return dutDao.get(idDut, username);
	}
	
	@Override
	@Transactional
	public boolean create(Dut newDut)
	{
		// Creation and modification date are the same when it is created
		java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
		newDut.setCreatedDate(date);
		newDut.setModifiedDate(date);

		if (dutDao.get(newDut.getName(), newDut.getUser()) != null)
		{
			return false; // DUT already exists
		}
		else
		{
			dutDao.add(newDut); // Add new DUT's information to database
			sampleDao.add(new Sample(newDut)); // Add new DUT's first sample to database
			
			return true;
		}	
	}

	@Override
	@Transactional
	public Dut update(Dut dutUpdateInfo)
	{
		// First of all check if the DUT exists
		Dut dutToUpdate = dutDao.get(dutUpdateInfo.getIdDut(), dutUpdateInfo.getUser());
		
		if (dutToUpdate != null)
		{
			dutToUpdate.setName(dutUpdateInfo.getName());
			dutToUpdate.setManufacturer(dutUpdateInfo.getManufacturer());
			dutToUpdate.setModel(dutUpdateInfo.getModel());
			dutToUpdate.setDescription(dutUpdateInfo.getDescription());
			
			// Add modification datetime
			dutToUpdate.setModifiedDate(new java.sql.Timestamp(new java.util.Date().getTime()));
			
			for (Project parentProject : projectDAO.getByDUT(dutUpdateInfo.getUser(), dutUpdateInfo.getIdDut()))
			{
				if (!parentProject.isIsConfigured())
				{
					continue;
				}
				
				modifyDutIxit(parentProject.getConfiguration(), dutUpdateInfo.getName(), dutUpdateInfo.getManufacturer(),
						dutUpdateInfo.getModel(), dutUpdateInfo.getDescription());
			}
		}
		
		return dutToUpdate;
	}
	
	private void modifyDutIxit(String configurationPath, String deviceName, String manufacturer, String model, String description)
	{
		XMLManager xmlManager = new XMLManager();
		Document xmlDocument = xmlManager.fileToDocument(configurationPath);
		
		xmlManager.replaceNodeValueByName(xmlDocument, "Project/Ixit", "IXITCO_DeviceName", deviceName);
		xmlManager.replaceNodeValueByName(xmlDocument, "Project/Ixit", "IXITCO_Manufacturer", manufacturer);
		xmlManager.replaceNodeValueByName(xmlDocument, "Project/Ixit", "IXITCO_ModelNumber", model);
		xmlManager.replaceNodeValueByName(xmlDocument, "Project/Ixit", "IXITCO_Description", description);
		
		xmlManager.saveDocumentToFile(xmlDocument, configurationPath);
	}

	@Override
	@Transactional
	public boolean delete(String username, int idDut)
	{
		if (dutDao.get(idDut, username) != null)
		{
			// Delete all linked samples
			for (Sample sampleToDelete : sampleDao.list(idDut))
			{
				sampleDao.delete(sampleToDelete.getIdSample());
			}
			
			// Delete the DUT
			return dutDao.delete(idDut) == 1;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean exists(String username, String name, int id)
	{
		Dut existingDut = dutDao.get(name, username);
				
		return ((existingDut != null) && (existingDut.getIdDut() != id));
	}
	
	@Override
	@Transactional(readOnly = true)
	public int getDutId(String username, String dutName)
	{
		return dutDao.get(dutName, username).getIdDut();
	}
	
	// --------------------------------------------------------
	// SAMPLE
	// --------------------------------------------------------
	@Override
	@Transactional(readOnly = true)
	public List<Sample> getSampleData(int idDut)
	{
		return sampleDao.list(idDut);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Sample getSampleFormData(int idSample)
	{
		return sampleDao.get(idSample);
	}

	@Override
	@Transactional
	public void createSample(Sample s)
	{
		sampleDao.add(s);	
	}
	
	@Override
	@Transactional
	public void deleteSample(int sampleID)
	{
		// At least 1 sample is necessary
		if (sampleDao.list(sampleDao.get(sampleID).getAssociatedDut()).size() > 1)
		{
			sampleDao.delete(sampleID);
		}
	}

	@Override
	@Transactional
	public void updateSample(Sample sampleUpdateInfo, String username)
	{
		Sample sampleToUpdate = sampleDao.get(sampleUpdateInfo.getIdSample());
		
		if (sampleToUpdate != null)
		{
			sampleToUpdate.setAppId(sampleUpdateInfo.getAppId());
			sampleToUpdate.setDeviceId(sampleUpdateInfo.getDeviceId());
			sampleToUpdate.setHwVer(sampleUpdateInfo.getHwVer());
			sampleToUpdate.setSwVer(sampleUpdateInfo.getSwVer());
			
			if (sampleToUpdate.getIdSample() == sampleDao.list(sampleToUpdate.getAssociatedDut()).get(0).getIdSample())
			{
				for (Project parentProject : projectDAO.getByDUT(username, sampleToUpdate.getAssociatedDut()))
				{
					if (!parentProject.isIsConfigured())
					{
						continue;
					}
					
					modifySampleIxit(parentProject.getConfiguration(), sampleUpdateInfo.getAppId(), sampleUpdateInfo.getDeviceId(),
							sampleUpdateInfo.getHwVer(), sampleUpdateInfo.getSwVer());
				}
			}
		}
	}
	
	private void modifySampleIxit(String configurationPath, String appID, String deviceID, String hwVersion, String swVersion)
	{
		XMLManager xmlManager = new XMLManager();
		Document xmlDocument = xmlManager.fileToDocument(configurationPath);
		
		xmlManager.replaceNodeValueByName(xmlDocument, "Project/Ixit", "IXITCO_AppId", appID);
		xmlManager.replaceNodeValueByName(xmlDocument, "Project/Ixit", "IXITCO_DeviceId", deviceID);
		xmlManager.replaceNodeValueByName(xmlDocument, "Project/Ixit", "IXITCO_SoftwareVersion", swVersion);
		xmlManager.replaceNodeValueByName(xmlDocument, "Project/Ixit", "IXITCO_HardwareVersion", hwVersion);
		
		xmlManager.saveDocumentToFile(xmlDocument, configurationPath);
	}

	// --------------------------------------------------------
	// IXIT PRELOAD
	// --------------------------------------------------------
	@Override
	@Transactional(readOnly = true)
	public void loadIxitValues(String username, int dutID, List<Ixit> listOfIxit)
	{
		Dut dutInfoToLoad = dutDao.get(dutID, username);
		Sample sampleInfoToLoad = sampleDao.list(dutID).get(0);
		
		setIxitValue(listOfIxit, "IXITCO_DeviceName", dutInfoToLoad.getName());
		setIxitValue(listOfIxit, "IXITCO_Manufacturer", dutInfoToLoad.getManufacturer());
		setIxitValue(listOfIxit, "IXITCO_ModelNumber", dutInfoToLoad.getModel());
		setIxitValue(listOfIxit, "IXITCO_Description", dutInfoToLoad.getDescription());
		
		setIxitValue(listOfIxit, "IXITCO_AppId", sampleInfoToLoad.getAppId());
		setIxitValue(listOfIxit, "IXITCO_DeviceId", sampleInfoToLoad.getDeviceId());
		setIxitValue(listOfIxit, "IXITCO_SoftwareVersion", sampleInfoToLoad.getSwVer());
		setIxitValue(listOfIxit, "IXITCO_HardwareVersion", sampleInfoToLoad.getHwVer());
	}
	
	private void setIxitValue(List<Ixit> listOfIxit, String name, String value)
	{
		listOfIxit.stream()
			.filter(ixit -> ixit.getName().equals(name))
			.findFirst().get()
			.setValue(value);
	}
}