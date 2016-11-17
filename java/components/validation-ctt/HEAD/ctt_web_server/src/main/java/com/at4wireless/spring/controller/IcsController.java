/*******************************************************************************
 * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
 *       THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *       WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *       WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *       AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *       DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *       PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *       TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *       PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.spring.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.web.servlet.ModelAndView;

import com.at4wireless.spring.model.Ics;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.Result;
import com.at4wireless.spring.model.ServiceFramework;
import com.at4wireless.spring.service.IcsService;
import com.at4wireless.spring.service.ProjectService;
import com.at4wireless.spring.service.ServiceFrameworkService;

/**
 * This class manages all actions related to ICS view
 */
@Controller
@RequestMapping(value="/ics")
public class IcsController
{
	@Autowired
	private ProjectService projectService;
	@Autowired
	private IcsService icsService;
	@Autowired
	private ServiceFrameworkService sfService;
	
	static final Logger log = LogManager.getLogger(IcsController.class);
	
	/**
	 * Loads data to be displayed if logged, redirects to login
	 * otherwise.
	 * 
     * @param 	model 		model to add objects needed by the view
     * @param 	newProject 	ID of the project whose services have to be loaded
     * @return 				target view
     */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView ics(Model model, @ModelAttribute("newProject") Project newProject)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			List<Ics> listIcs = new ArrayList<Ics>();
			List<ServiceFramework> listService = new ArrayList<ServiceFramework>();
			Project p = projectService.getFormData(auth.getName(), newProject.getIdProject());

			listIcs = icsService.load(projectService.getServicesData(p.getIdProject()), p.isIsConfigured(),
					p.getConfiguration());
			for (BigInteger bi : projectService.getServicesData(newProject.getIdProject()))
			{
				listService.add(sfService.list().get(bi.intValue()-1));
			}
			
			model.addAttribute("icsList", listIcs);
			model.addAttribute("serviceList", listService);

			return new ModelAndView("dynamic-ics");
		}
		else
		{
			return new ModelAndView("redirect:/login");
		}
	}
	
	/**
	 * Performs SCR with the received ICS configuration
	 * 
	 * @param 	request		servlet request with the ICS values
	 * @return				SCR result for each ICS
	 */
	@RequestMapping(value="/scr", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody List<Result> SCR(HttpServletRequest request)
	{
		List<Result> scrCheck = new ArrayList<Result>();
		Map<String, String[]> map = request.getParameterMap();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			scrCheck = icsService.check(projectService.getServicesData(Integer.parseInt(
					request.getParameter("data[idProject]"))),map);
		}		
		return scrCheck;
	}
	
	/**
	 * Gives functionality to Back Button, choosing a target view or another depending 
	 * on project type
	 * 
	 * @param 	newProject	contains ID of the project whose type is going to be checked
	 * @return				target view
	 */
	@RequestMapping(value="/decide", method=RequestMethod.GET)
	public String decide(@ModelAttribute("newProject") Project newProject)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			String type = projectService.getFormData(auth.getName(), newProject.getIdProject()).getType();
			
			if(type.equals("Conformance"))
			{
				return "redirect:/dut";
			}
			else
			{
				return "redirect:/gu";
			}
		}
		else
		{
			return "redirect:/login";
		}
	}
}