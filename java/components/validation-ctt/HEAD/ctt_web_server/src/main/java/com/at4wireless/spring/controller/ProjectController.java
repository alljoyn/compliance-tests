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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.at4wireless.spring.model.CertificationRelease;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.service.CertificationReleaseService;
import com.at4wireless.spring.service.DutService;
import com.at4wireless.spring.service.GoldenUnitService;
import com.at4wireless.spring.service.ProjectService;
import com.at4wireless.spring.service.ServiceFrameworkService;
import com.at4wireless.spring.service.TcclService;
import com.at4wireless.spring.service.UserService;

/**
 * This class manages all actions related with project view
 * 
 */
@Controller
@RequestMapping(value = "/project")
public class ProjectController
{
	@Autowired
	private ProjectService projectService;
	@Autowired
	private DutService dutService;
	@Autowired
	private CertificationReleaseService crService;
	@Autowired
	private ServiceFrameworkService sfService;
	@Autowired
	private GoldenUnitService guService;
	@Autowired
	private TcclService tcclService;	
	@Autowired
	private UserService userService;

	/**
	 * Loads data to be displayed if logged, redirects to login
	 * otherwise.
	 * 
     * @param 	model 	model to add objects needed by the view
     * @param 	error 	error type, if exists
     * @return 			target view
     */
	@RequestMapping(method = RequestMethod.GET)
	public String project(Model model,
			@RequestParam(value = "error", required = false) String error)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			String username = auth.getName();

			model.addAttribute("projectList", projectService.getTableData(username));
			model.addAttribute("newProject", new Project());
			model.addAttribute("certrelList", crService.list());
			model.addAttribute("tcclList",tcclService.list());
			model.addAttribute("serviceList", sfService.list());
			model.addAttribute("dutList", dutService.getTableData(username));
			
			if (error != null)
			{
				if (error.equals("empty"))
				{
					model.addAttribute("error", "You have to select a project");
				}
				else if (error.equals("exists"))
				{
					model.addAttribute("error", "A project with that name already exists");
				}
				else if (error.equals("name"))
				{
					model.addAttribute("error", "Project name cannot be empty");
				}
			}
			return "project";
		}
		else
		{
			return "redirect:/login";
		}
	}

	/**
	 * Manages the creation of a new project if authenticated, redirects
	 * to login otherwise.
	 * 
     * @param 	newProject 	project data to be validated and stored
     * @param 	result 		validation result
     * @return 				target view
     */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(@Valid @ModelAttribute("newProject") Project newProject, BindingResult result)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			if (result.hasErrors())
			{
				return "redirect:/project?error=name";
			}
			else
			{
				if(projectService.create(newProject))
				{
					return "redirect:/project";
				}
				else
				{
					return "redirect:/project?error=exists";
				}
			}
		}
		else
		{
			return "redirect:/login";
		}
	}

	/**
	 * Loads project data to be edited
	 * 
     * @param 	request 	servlet request with projectId to be loaded
     * @return 				project data
     */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public @ResponseBody Project edit(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			return projectService.getFormData(auth.getName(), 
					Integer.parseInt(request.getParameter("idProject")));
		}

		return new Project();
	}

	/**
	 * Manages the modification of a project if authenticated, redirects
	 * to login otherwise.
	 * 
     * @param 	newProject 	project data to be validated and modified
     * @param 	result 		validation result
     * @return 				target view
     */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@Valid @ModelAttribute("newProject") Project newProject, BindingResult result)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			if (result.hasErrors())
			{
				return "redirect:/project?error=name";
			}
			else
			{
				projectService.update(newProject);
				return "redirect:/project";
			}
		}
		else
		{
			return "redirect:/login";
		}
	}

	/**
	 * Manages the erasing of a project if authenticated, redirects to login
	 * otherwise.
	 * 
     * @param 	request 	servlet request with the ID of the project to be deleted
     * @return 				target view
     */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String delete(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			projectService.delete(auth.getName(), 
					Integer.parseInt(request.getParameter("idProject")));
		}

		return "redirect:/project";
	}
	
	/**
	 * Async load of TCCLs of a given Certification Release
	 * 
     * @param 	request 	servlet request with the ID of the CR
     * @return 				list of TCCL related to the CR version
     */
	@RequestMapping(value="/loadTccl", method = RequestMethod.GET)
	public @ResponseBody List<Tccl> loadTccl(HttpServletRequest request)
	{
		return tcclService.listByCR(Integer.parseInt(request.getParameter("idCertRel")));
	}
	
	/**
	 * Checks if a project name already exists 
	 * 
	 * @param 	request	servlet request with the project name to be checked
	 * @return			false if exists, true otherwise
	 */
	@RequestMapping(value="/validateName", method = RequestMethod.GET)
	public @ResponseBody boolean validateName(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			return (!(projectService.exists(auth.getName(), 
					request.getParameter("name"), Integer.parseInt(request.getParameter("id")))));
		}
		return true;
	}
	
	/**
	 * Generates CTT Local Agent API-Key
	 * 
	 * @return	generated key
	 */
	@RequestMapping(value="/generateKey", method = RequestMethod.POST)
	public @ResponseBody int generateKey()
	{
		int pass = -1;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			pass = userService.setKey(auth.getName());
		}
		return pass;
	}
	
	/**
	 * Async load of Certification Releases of a given project type
	 * 
     * @param 	request 	servlet request with the type of the project
     * @return 				list of Certification Releases related to the project type
     */
	@RequestMapping(value="/loadCertRel", method = RequestMethod.GET)
	public @ResponseBody List<CertificationRelease> loadCertRel(HttpServletRequest request)
	{
		if (request.getParameter("pjType").equals("Development"))
		{
			return crService.list();
		}
		else
		{
			return crService.listReleaseVersions();
		}
	}
}
