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
package com.at4wireless.spring.controller;

import java.util.List;

import org.springframework.web.util.HtmlUtils;

import com.at4wireless.spring.model.Dut;
import com.at4wireless.spring.model.Ixit;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.Sample;

public class ControllerCommons
{
	public static <T> List<T> dataToHtml(List<T> dataList)
	{
		for (T obj : dataList)
		{
			if (obj instanceof Project)
			{
				((Project) obj).setName(HtmlUtils.htmlEscape(((Project) obj).getName()));
				((Project) obj).setgUnits(HtmlUtils.htmlEscape(((Project) obj).getgUnits()));
			}
			else if (obj instanceof Dut)
			{
				((Dut) obj).setName(HtmlUtils.htmlEscape(((Dut) obj).getName()));
				((Dut) obj).setDescription(HtmlUtils.htmlEscape(((Dut) obj).getDescription()));
				((Dut) obj).setDeviceId(HtmlUtils.htmlEscape(((Dut) obj).getDeviceId()));
				((Dut) obj).setHwVer(HtmlUtils.htmlEscape(((Dut) obj).getHwVer()));
				((Dut) obj).setManufacturer(HtmlUtils.htmlEscape(((Dut) obj).getManufacturer()));
				((Dut) obj).setModel(HtmlUtils.htmlEscape(((Dut) obj).getModel()));
				((Dut) obj).setSwVer(HtmlUtils.htmlEscape(((Dut) obj).getSwVer()));
			}
			else if (obj instanceof Sample)
			{
				((Sample) obj).setDescription(HtmlUtils.htmlEscape(((Sample) obj).getDescription()));
				((Sample) obj).setDeviceId(HtmlUtils.htmlEscape(((Sample) obj).getDeviceId()));
				((Sample) obj).setHwVer(HtmlUtils.htmlEscape(((Sample) obj).getHwVer()));
				((Sample) obj).setSwVer(HtmlUtils.htmlEscape(((Sample) obj).getSwVer()));
			}
			else if (obj instanceof Ixit)
			{
				((Ixit) obj).setValue(HtmlUtils.htmlEscape(((Ixit) obj).getValue()));
			}
		}
		
		return dataList;
	}
}