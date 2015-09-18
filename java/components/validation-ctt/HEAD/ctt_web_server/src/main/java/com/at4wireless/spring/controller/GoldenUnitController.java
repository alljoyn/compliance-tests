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

import com.at4wireless.spring.model.GoldenUnit;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.service.GoldenUnitService;
import com.at4wireless.spring.service.ProjectService;


/**
 * This class manages all actions related to Golden Unit view
 */
@Controller
@RequestMapping(value="/gu")
public class GoldenUnitController
{
	@Autowired
	private ProjectService projectService;
	@Autowired
	private GoldenUnitService guService;
	
	/**
	 * Loads data to be displayed if logged, redirects to login
	 * otherwise.
	 * 
     * @param 	model 	model to add objects needed by the view
     * @param 	error 	error type, if exists
     * @return 			target view
     */
	@RequestMapping(method = RequestMethod.GET)
	public String gu(Model model, @RequestParam(value = "error", required = false) String error)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			String username = auth.getName();
			
			model.addAttribute("guList", guService.getTableData(username));
			model.addAttribute("categoryList", guService.getCategories());
			model.addAttribute("newProject", new Project());
			model.addAttribute("newGu", new GoldenUnit());

			if (error != null)
			{
				if(error.equals("empty"))
				{
					model.addAttribute("error", "You have to select a GU");
				}
				else if(error.equals("exists"))
				{
					model.addAttribute("error", "A GU with that name already exists");
				}
				else if(error.equals("name"))
				{
					model.addAttribute("error", "GU name cannot be empty");
				}
			}
			return "gu";
		}
		else
		{
			return "redirect:/login";
		}
	}
	
	/**
	 * Manages the creation of a new golden unit if authenticated, redirects
	 * to login otherwise.
	 * 
     * @param 	newGu 		Golden Unit data to be validated and stored
     * @param 	result 		validation result
     * @return 				target view
     */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String addGu(@Valid @ModelAttribute("newGu") GoldenUnit newGu,
			BindingResult result)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{		
			if (result.hasErrors())
			{
				return "redirect:/gu?error=name";
			}
			else
			{
				if(guService.create(newGu))
				{
					return "redirect:/gu";
				}
				else
				{
					return "redirect:/gu?error=exists";
				}
			}
		}
		else
		{
			return "redirect:/login";
		}
	}
	
	/**
	 * Loads Golden Unit data to be edited
	 * 
     * @param 	request 	servlet request with ID of the Golden Unit to be loaded
     * @return 				Golden Unit data
     */
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public @ResponseBody GoldenUnit editDut(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			return guService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("idGu")));
		}
		return new GoldenUnit();
	}
	
	/**
	 * Manages the modification of a Golden Unit if authenticated, redirects
	 * to login otherwise.
	 * 
     * @param 	newGu 		Golden Unit data to be validated and modified
     * @param 	result 		validation result
     * @return 				target view
     */
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String saveChanges(@Valid @ModelAttribute("newGu") GoldenUnit newGu, BindingResult result)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			if (result.hasErrors())
			{
				return "redirect:/gu?error=name";
			}
			else
			{
				guService.update(newGu);
				return "redirect:/gu";
			}
		}
		else
		{
			return "redirect:/login";
		}
	}
	
	/**
	 * Manages the removal of a Golden Unit if authenticated, redirects to login
	 * otherwise.
	 * 
     * @param 	request 	servlet request with the ID of the Golden Unit to be deleted
     * @return 				target view
     */
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public String delGu(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			String username = auth.getName();
			guService.delete(username, Integer.parseInt(request.getParameter("idGu")));
			return "redirect:/gu";
		}
		else
		{
			return "redirect:/login";
		}
	}
	
	/**
	 * Stores the assigned Golden Units and redirects to the next view
	 * 
	 * @param 	model		model to add objects needed by the view
	 * @param 	newProject	project object with assigned Golden Unit info
	 * @return				target view
	 */
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public String save(Model model, @ModelAttribute("newProject") Project newProject)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			projectService.setGu(auth.getName(),newProject);
			return "redirect:/ics?idProject=" + newProject.getIdProject();
		}
		else
		{
			return "redirect:/login";
		}
	}
	
	/**
	 * Checks if a golden unit name already exists 
	 * 
	 * @param 	request	servlet request with the golden unit name to be checked
	 * @return			false if exists, true otherwise
	 */
	@RequestMapping(value="/validateName", method = RequestMethod.GET)
	public @ResponseBody boolean validateName(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			return (!(guService.exists(auth.getName(), 
					request.getParameter("name"), Integer.parseInt(request.getParameter("id")))));
		}
		return true;
	}
}
