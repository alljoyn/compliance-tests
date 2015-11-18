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

package com.at4wireless.spring.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

import com.at4wireless.spring.model.Ixit;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.ServiceFramework;
import com.at4wireless.spring.service.DutService;
import com.at4wireless.spring.service.IxitService;
import com.at4wireless.spring.service.ProjectService;
import com.at4wireless.spring.service.ServiceFrameworkService;

/**
 * This class manages all actions related to IXIT view
 */
@Controller
@RequestMapping(value="/ixit")
public class IxitController
{
	@Autowired
	private IxitService ixitService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private DutService dutService;
	@Autowired
	private ServiceFrameworkService sfService;
	
	/**
	 * Loads data to be displayed if logged, redirects to login
	 * otherwise.
	 * 
     * @param 	model 		model to add objects needed by the view
     * @param 	newProject 	ID of the project whose services have to be loaded
     * @return 				target view
     */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ModelAndView ixit(Model model, @ModelAttribute("newProject") Project newProject)
	{		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			List<Ixit> listIxit = new ArrayList<Ixit>();
			List<ServiceFramework> listService = new ArrayList<ServiceFramework>();
			Project p = projectService.getFormData(auth.getName(), newProject.getIdProject());
			
			listIxit = ixitService.load(projectService.getServicesData(p.getIdProject()), p.isIsConfigured(),
					p.getConfiguration());
			
			dutService.setValues(auth.getName(), p.getIdDut(), listIxit);
			
			for (BigInteger bi : projectService.getServicesData(newProject.getIdProject()))
			{
				listService.add(sfService.list().get(bi.intValue()-1));
			}
			model.addAttribute("ixitList", ControllerCommons.dataToHtml(listIxit));
			//model.addAttribute("ixitList", listIxit);
			model.addAttribute("serviceList", listService);
			return new ModelAndView("dynamic-ixit");
		}
		else
		{
			return new ModelAndView("redirect:/login");
		}
	}
}
