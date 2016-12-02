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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.at4wireless.security.FileEncryption;
import com.at4wireless.spring.common.XMLManager;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.TestCaseResult;
import com.at4wireless.spring.model.dto.SendResultsDT;
import com.at4wireless.spring.service.ProjectService;
import com.at4wireless.spring.service.ResultService;
import com.at4wireless.spring.service.TestCaseService;
import com.at4wireless.spring.service.UserService;

/**
 * This class manages all actions related with results view
 * 
 */
@Controller
@RequestMapping(value="/results")
public class ResultsController
{
	@Autowired
	private ProjectService projectService;
	@Autowired
	private TestCaseService tcService;
	@Autowired
	private ResultService resultService;
	@Autowired
	private UserService userService;
	
	@Value("${cawt.url}")
	private String cawtUrl;
	
	@Value("${cawt.secret}")
	private String cawtSecret;
	
	/**
	 * Loads data to be displayed if logged, redirects to login
	 * otherwise.
	 * 
     * @param 	model 		model to add objects needed by the view
     * @param 	newProject 	project whose results are going to be shown
     * @return 				target view
     */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView results(Model model, @ModelAttribute("newProject") Project newProject)
	{
		List<TestCaseResult> listTCResult = new ArrayList<TestCaseResult>();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			String username = auth.getName();
			Project p = projectService.getFormData(username, newProject.getIdProject());
			
			listTCResult = resultService.getResults(p);
		}
		
		model.addAttribute("listTCResult", listTCResult);
		return new ModelAndView("dynamic-results");
	}
	
	/**
	 * Shows full log of selected row in other tab
	 * 
	 * @param	request		servlet request with the name of the log to be shown
	 * @return				full log
	 */
	@RequestMapping(value="/fullLog", method=RequestMethod.GET, produces="text/plain; charset=UTF-8")
	public @ResponseBody String showLog(HttpServletRequest request)
	{	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String log = "";
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			String username = auth.getName();
			String path = File.separator+"Allseen"+File.separator+"Users"+File.separator
					+username+File.separator+request.getParameter("id")
					+File.separator+request.getParameter("file");
			try
			{
				FileEncryption fE = new FileEncryption();
				fE.setAesSecretKey(userService.getAesSecretKey(username));
				log = fE.decrypt(RestController.readFile(path, StandardCharsets.UTF_8));
			}
			catch (GeneralSecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return log;
	}
	
	/**
	 * Creates Test Report
	 * 
	 * @param 	request		servlet request that contains the project whose test report is going to be created
	 * @return				true if test report creation succeeded, false otherwise. 
	 */
	@RequestMapping(value="/tr/create", method=RequestMethod.POST)
	public @ResponseBody boolean createTestReport(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			String username = auth.getName();
			Project p = projectService.getFormData(username,
					Integer.parseInt(request.getParameter("idProject")));
			
			if(tcService.ranAll(p.getConfiguration(), p.getResults()))
			{
				String imgPath = request.getSession().getServletContext().getRealPath("/")+"resources"
						+File.separator+"img";
				return resultService.createTestReport(username, p, imgPath);
			}
		}
		return false;
	}
	
	/**
	 * Allows to download the test report
	 * 
	 * @param request
	 * 				servlet request with the ID of the project whose test report is going to be sent
	 * @param response
	 * 				servlet response with the test report
	 * 
	 * @throws IOException if file does not exist
	 */
	@RequestMapping(value="/tr/view", method = RequestMethod.GET)
	public void getTestReport(HttpServletRequest request, HttpServletResponse response)
			throws IOException
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			Project p = projectService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("idProject")));
			
			resultService.downloadPdfFile(response, p.getTestReport());
		}
	}
	
	/**
	 * Allows to download a package with the test report and all related logs
	 * 
	 * @param request
	 * 				servlet request with the ID of the project whose zip is going to be sent and downloaded
	 * @param response
	 * 				servlet response with the zip
	 * 
	 * @throws Exception in something wrong occurred during zip creation
	 */
	@RequestMapping(value="tr/send", method = RequestMethod.GET)
	public @ResponseBody SendResultsDT send(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		SendResultsDT sendResultsDt = new SendResultsDT(2, new ArrayList<String>());
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			Project p = projectService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("idProject")));
			
			List<String> logsNotFound = resultService.createZipFile(p.getUser(), p.getIdProject(), userService.getAesSecretKey(auth.getName()));
			
			if (!p.getType().equals("Development"))
			{	
				if (logsNotFound.isEmpty())
				{
					String xmlResponse = resultService.uploadZipFileToCawt(p.getUser(), p.getCarId(), p.getIdProject(), cawtUrl, cawtSecret);
					XMLManager xmlManager = new XMLManager();
					
					sendResultsDt.setResultCode(Integer.parseInt(xmlManager.retrieveNodeValuesFromXMLString(xmlResponse, "/Output/result_code").get(0)));
					
					List<String> errorMessages = xmlManager.retrieveNodeValuesFromXMLString(xmlResponse, "/Output/Error");
					
					if (!errorMessages.isEmpty())
					{
						sendResultsDt.getResultMessages().add(errorMessages.get(0));
					}	
				}
				else
				{
					sendResultsDt.getResultMessages().addAll(logsNotFound);
				}
			}
			else
			{
				sendResultsDt.setResultCode(3);
			}
		}

		return sendResultsDt;
	}
	
	@RequestMapping(value="tr/download", method = RequestMethod.GET)
	public void download(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			Project p = projectService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("idProject")));

			resultService.downloadZipFile(response, p.getUser(), p.getIdProject());
		}
	}
	
	
	/**
	 * Checks if all applicable test cases have been executed
	 * 
	 * @param request
	 * 				servlet request with the ID of the project to be checked
	 * 
	 * @return true if executed all, false otherwise
	 */
	@RequestMapping(value="tr/allRun", method=RequestMethod.GET)
	public @ResponseBody boolean allRun(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			return resultService.allTestCasesRun(auth.getName(), Integer.parseInt(request.getParameter("idProject")));
		}
		else
		{
			return false;
		}
	}
}