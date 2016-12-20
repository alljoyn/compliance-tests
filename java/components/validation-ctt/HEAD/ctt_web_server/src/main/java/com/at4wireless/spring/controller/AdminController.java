/*******************************************************************************
 *      Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *      Source Project (AJOSP) Contributors and others.
 *
 *      SPDX-License-Identifier: Apache-2.0
 *
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *      Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for
 *      any purpose with or without fee is hereby granted, provided that the
 *      above copyright notice and this permission notice appear in all
 *      copies.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.spring.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.at4wireless.spring.common.ConfigParam;
import com.at4wireless.spring.model.CertificationRelease;
import com.at4wireless.spring.model.Ics;
import com.at4wireless.spring.model.Ixit;
import com.at4wireless.spring.model.ServiceFramework;
import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.model.TestCase;
import com.at4wireless.spring.model.TestCaseTccl;
import com.at4wireless.spring.service.CertificationReleaseService;
import com.at4wireless.spring.service.IcsService;
import com.at4wireless.spring.service.IxitService;
import com.at4wireless.spring.service.ServiceFrameworkService;
import com.at4wireless.spring.service.TcclService;
import com.at4wireless.spring.service.TestCaseService;

@Controller
@RequestMapping(value = "/admin")
public class AdminController
{
	private static final String TEST_CASES_REGEX = "TestCases_Package_v[0-9]{2}\\.[0-9]{2}\\.[0-0]{2}[a-z]?_[DR][0-9]+\\.jar";
	private static final String LOCAL_AGENTS_REGEX = "CTT_Local_Agent_v[0-9]+\\.[0-9]+\\.[0-9]+_Installer\\.exe";
	private static final String CR_PARAMETER_NAME = "idCertRel";
	private static final String TCCL_PARAMETER_NAME = "idTccl";
	private static final String DESCRIPTION_PARAMETER_NAME = "description";
	
	@Autowired
	private ServiceFrameworkService sfService;
	@Autowired
	private TcclService tcclService;
	@Autowired
	private CertificationReleaseService crService;
	@Autowired
	private IcsService icsService;
	@Autowired
	private IxitService ixitService;
	@Autowired
	private TestCaseService tcService;
	
	//---------------------------------------------------------------------------------
	// ON PAGE LOAD
	//---------------------------------------------------------------------------------
	@RequestMapping(method = RequestMethod.GET)
	public String tccl(Model model)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{	
			model.addAttribute("certrelList", crService.list());
			model.addAttribute("tcclList", tcclService.list());
			model.addAttribute("tcList", tcService.list());
			model.addAttribute("newTccl", new Tccl());
			model.addAttribute("newIcs", new Ics());
			model.addAttribute("newIxit", new Ixit());
			model.addAttribute("newTestCase", new TestCase());
			
			return "admin";
		}
		else
		{
			return "redirect:/login";
		}
	}
	
	//---------------------------------------------------------------------------------
	// TCCL CREATION
	//---------------------------------------------------------------------------------
	@RequestMapping(value = "/crVersions", method = RequestMethod.GET)
	public @ResponseBody List<CertificationRelease> crVersions()
	{
		return crService.list();
	}
	
	@RequestMapping(value = "/testcases", method = RequestMethod.GET)
	public @ResponseBody List<TestCase> testcases(HttpServletRequest request)
	{
		return tcService.list(Integer.parseInt(request.getParameter(CR_PARAMETER_NAME)));
	}
	
	@RequestMapping(value = "/tccl/add", method = RequestMethod.POST)
	public @ResponseBody Tccl add(HttpServletRequest request)
	{
		return tcclService.create(request.getParameterMap());
	}
	
	//---------------------------------------------------------------------------------
	// TCCL EDITION
	//---------------------------------------------------------------------------------
	@RequestMapping(value = "/tccl/edit", method = RequestMethod.GET)
	public @ResponseBody List<TestCaseTccl> editTccl(HttpServletRequest request)
	{
		return tcclService.getTccl(Integer.parseInt(request.getParameter(TCCL_PARAMETER_NAME)));
	}
	
	@RequestMapping(value = "/tccl/edit", method = RequestMethod.POST)
	public @ResponseBody Tccl saveChanges(HttpServletRequest request)
	{
		return tcclService.update(request.getParameterMap());
	}
	
	//---------------------------------------------------------------------------------
	// TCCL DELETION
	//---------------------------------------------------------------------------------
	@RequestMapping(value = "/tccl/delete", method = RequestMethod.POST)
	public @ResponseBody void delete(HttpServletRequest request)
	{
		tcclService.delete(Integer.parseInt(request.getParameter(TCCL_PARAMETER_NAME)));
	}
	
	//---------------------------------------------------------------------------------
	// CTT LOCAL AGENT INSTALLER
	//---------------------------------------------------------------------------------
	@RequestMapping(value = "/availableInstallers", method = RequestMethod.GET)
	public @ResponseBody Map<String, List<String>> availableInstallers()
	{
		Map<String, List<String>> localAgentInstallersMap = new HashMap<String, List<String>>();
		
		for (File localAgentInstaller : new File(ConfigParam.INSTALLERS_PATH).listFiles())
		{
			if (localAgentInstaller.isFile())
			{
				String mainVersion = getVersionFromInstaller(localAgentInstaller);
				
				if (localAgentInstallersMap.containsKey(mainVersion))
				{
					localAgentInstallersMap.get(mainVersion).add(localAgentInstaller.getName());
				}
				else
				{
					List<String> newLaList = new ArrayList<String>();
					newLaList.add(localAgentInstaller.getName());
					localAgentInstallersMap.put(mainVersion, newLaList);
				}
			}
			
			for (String key : localAgentInstallersMap.keySet())
			{
				Collections.sort(localAgentInstallersMap.get(key), new Comparator<String>()
				{
				    public int compare(String str1, String str2)
				    {
				        String substr1 = str1.split("_")[3].split("\\.")[1];
				        String substr2 = str2.split("_")[3].split("\\.")[1];

				        return Integer.valueOf(substr1).compareTo(Integer.valueOf(substr2));
				    }
				});
			}
		}
		
		return localAgentInstallersMap;
	}
	
	private String getVersionFromInstaller(File localAgentInstaller)
	{
		return localAgentInstaller.getName().split("_")[3].split("\\.")[0];
	}
	
	@RequestMapping(value = "/uploadLA", method = RequestMethod.POST)
	public @ResponseBody UploadMessage uploadLA(MultipartHttpServletRequest request, HttpServletResponse response)
	{
		MultipartFile mpf = request.getFile(request.getFileNames().next());
		UploadMessage uMessage = null;
		
		if (mpf.getOriginalFilename().matches(LOCAL_AGENTS_REGEX))
		{
			try
			{
				mpf.transferTo(new File(ConfigParam.INSTALLERS_PATH + mpf.getOriginalFilename()));
				uMessage = new UploadMessage("Success", String.format("%s uploaded!", mpf.getOriginalFilename()));
			}
			catch (IllegalStateException | IOException e)
			{
				uMessage = new UploadMessage("Fail", String.format("upload returned an error: ", e.getMessage()));
			}
		}
		else
		{
			uMessage = new UploadMessage("Fail", "The installer to be uploaded has a wrong name format!");
		}

		return uMessage;
	}
	
	@RequestMapping(value = "/deleteLA", method = RequestMethod.POST)
	public @ResponseBody UploadMessage deleteLocalAgentInstaller(HttpServletRequest request)
	{	
		UploadMessage uMessage = null;
		
		String fileToBeDeleted = request.getParameter("fileToDelete").replaceAll(" ", "");
		
		if (fileToBeDeleted.matches(LOCAL_AGENTS_REGEX))
		{
			File file = new File(ConfigParam.INSTALLERS_PATH + fileToBeDeleted);
			
			if (file.exists())
			{
				file.delete();
				uMessage = new UploadMessage("Success", String.format("%s deleted!", fileToBeDeleted));
			}
			else
			{
				uMessage = new UploadMessage("Fail", "The installer does not exist");
			}
		}
		else
		{
			uMessage = new UploadMessage("Fail", "The installer to be deleted has a wrong name format!");
		}
		
		return uMessage;
	}
	
	//---------------------------------------------------------------------------------
	// CTT TEST CASES PACKAGE
	//---------------------------------------------------------------------------------
	@RequestMapping(value = "/availablePackages", method = RequestMethod.GET)
	public @ResponseBody Map<String, List<String>> availablePackages()
	{
		Map<String, List<String>> testCasesPackagesMap = new HashMap<String, List<String>>();
		
		for (File testCasesPackage : new File(ConfigParam.PACKAGES_PATH).listFiles())
		{
			if (testCasesPackage.isFile())
			{
				String certificationRelease = getCrFromPackage(testCasesPackage);
				
				if (testCasesPackagesMap.containsKey(certificationRelease))
				{
					testCasesPackagesMap.get(certificationRelease).add(testCasesPackage.getName());
				}
				else
				{
					List<String> newCrList = new ArrayList<String>();
					newCrList.add(testCasesPackage.getName());
					testCasesPackagesMap.put(certificationRelease, newCrList);
				}
			}
			
			for (String key : testCasesPackagesMap.keySet())
			{
				Collections.sort(testCasesPackagesMap.get(key), new Comparator<String>()
				{
				    public int compare(String str1, String str2)
				    {
				        String substr1 = str1.split("_")[3].split("\\.")[0].substring(1);
				        String substr2 = str2.split("_")[3].split("\\.")[0].substring(1);

				        return Integer.valueOf(substr1).compareTo(Integer.valueOf(substr2));
				    }
				});
			}
		}
		
		for (String certificationRelease : testCasesPackagesMap.keySet())
		{
			testCasesPackagesMap.get(certificationRelease).add(crService.getCertificationReleaseDescription(certificationRelease));
		}
		
		return testCasesPackagesMap;
	}
	
	private String getCrFromPackage(File testCasesPackage)
	{
		return testCasesPackage.getName().split("_")[2];
	}
	
	@RequestMapping(value = "/uploadTCP", method = RequestMethod.POST)
	public @ResponseBody UploadMessage upload(MultipartHttpServletRequest request, HttpServletResponse response)
	{	
		MultipartFile mpf = request.getFile(request.getFileNames().next());
		UploadMessage uMessage = null;
		
		if (mpf.getOriginalFilename().matches(TEST_CASES_REGEX))
		{
			try
			{
				CrAndVersion crAndVersion = new CrAndVersion(mpf);

				tcclService.addCertificationReleaseIfNotExists(crAndVersion.getPackageCr(), crAndVersion.getPackageVersion(),
						request.getParameter(DESCRIPTION_PARAMETER_NAME));
				mpf.transferTo(new File(ConfigParam.PACKAGES_PATH + mpf.getOriginalFilename()));
				uMessage = new UploadMessage("Success", mpf.getOriginalFilename() + " uploaded!");
			}
			catch (Exception e)
			{
				uMessage = new UploadMessage("Fail", String.format("upload returned an error: %s", e.getMessage()));
			}
		}
		else
		{
			uMessage = new UploadMessage("Fail", "The package to be uploaded has a wrong name format!");
		}
		
		return uMessage;
	}
	
	@RequestMapping(value = "/deleteTCP", method = RequestMethod.POST)
	public @ResponseBody UploadMessage deleteTestCasesPackage(HttpServletRequest request)
	{	
		UploadMessage uMessage = null;
		
		String fileToBeDeleted = request.getParameter("fileToDelete").replaceAll(" ", "");
		
		if (fileToBeDeleted.matches(TEST_CASES_REGEX))
		{
			File file = new File(ConfigParam.PACKAGES_PATH + fileToBeDeleted);
			
			if (file.exists())
			{
				file.delete();
				uMessage = new UploadMessage("Success", String.format("%s deleted!", fileToBeDeleted));
			}
			else
			{
				uMessage = new UploadMessage("Fail", "The package does not exist");
			}
		}
		else
		{
			uMessage = new UploadMessage("Fail", "The package to be deleted has a wrong name format!");
		}
		
		return uMessage;
	}
	
	//---------------------------------------------------------------------------------
	// COMMON
	//---------------------------------------------------------------------------------
	public class UploadMessage
	{
		String result;
		String message;
		
		public UploadMessage(String result, String message)
		{
			this.result = result;
			this.message = message;
		}
		
		public String getResult()
		{
			return result;
		}
		
		public void setResult(String result)
		{
			this.result = result;
		}
		
		public String getMessage()
		{
			return message;
		}
		
		public void setMessage(String message)
		{
			this.message = message;
		}
	}
	
	public class CrAndVersion
	{
		private String packageCr;
		private String packageVersion;
		
		public CrAndVersion(MultipartFile multipartFile)
		{
			this.packageCr = multipartFile.getOriginalFilename().split("_")[2];
			this.packageVersion = multipartFile.getOriginalFilename().split("_")[3].split("\\.")[0];
		}

		public String getPackageCr()
		{
			return packageCr;
		}

		public void setPackageCr(String packageCr)
		{
			this.packageCr = packageCr;
		}

		public String getPackageVersion()
		{
			return packageVersion;
		}

		public void setPackageVersion(String packageVersion)
		{
			this.packageVersion = packageVersion;
		}
	}
	
	@RequestMapping(value="/loadServiceFrameworks", method = RequestMethod.GET)
	public @ResponseBody List<ServiceFramework> loadServiceFrameworks(HttpServletRequest request)
	{
		return sfService.list();
	}
	
	@RequestMapping(value="/loadCertificationReleases", method = RequestMethod.GET)
	public @ResponseBody List<CertificationRelease> loadCertificationReleases(HttpServletRequest request)
	{
		return crService.list();
	}
	
	@RequestMapping(value="/saveIcs", method = RequestMethod.POST)
	public @ResponseBody String saveNewIcs(@ModelAttribute("newIcs") Ics newIcs)
	{
		return icsService.add(newIcs);
	}
	
	@RequestMapping(value="/saveIxit", method = RequestMethod.POST)
	public @ResponseBody String saveNewIxit(@ModelAttribute("newIxit") Ixit newIxit)
	{
		return ixitService.add(newIxit);
	}
	
	@RequestMapping(value="/saveTc", method = RequestMethod.POST)
	public @ResponseBody String saveNewTestCase(@ModelAttribute("newTestCase") TestCase newTestCase)
	{
		return tcService.add(newTestCase);
	}
}