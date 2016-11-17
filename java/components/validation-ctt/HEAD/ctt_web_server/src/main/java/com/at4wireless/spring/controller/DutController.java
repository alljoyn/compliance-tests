/*******************************************************************************
 *  * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
import org.springframework.web.servlet.ModelAndView;

import com.at4wireless.spring.model.Dut;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.Sample;
import com.at4wireless.spring.service.DutService;
import com.at4wireless.spring.service.ProjectService;

/**
 * This class manages all actions related to DUT view
 */
@Controller
@RequestMapping(value="/dut")
public class DutController
{
	@Autowired
	private ProjectService projectService;
	@Autowired
	private DutService dutService;
	
	/**
	 * Loads data to be displayed if logged, redirects to login otherwise.
	 * 
     * @param model 
     * 			model to add objects needed by the view
     * @param error
     * 			error type, if exists
     * @param field
     * 			field that causes error during validation, if exists
     * 
     * @return target view
     */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView dut(Model model, @RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "field", required = false) String field)
	{  
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			model.addAttribute("dutList", ControllerCommons.dataToHtml(dutService.getTableData(auth.getName())));
			model.addAttribute("newProject", new Project());
			model.addAttribute("newDut", new Dut());
			model.addAttribute("newSample", new Sample());
			
			if (error != null)
			{
				if(error.equals("empty"))
				{
					model.addAttribute("error", "You have to select a DUT");
				}
				else if(error.equals("exists"))
				{
					model.addAttribute("error", "A DUT with that name already exists");
				}
				else if(error.equals("NotEmpty"))
				{
					model.addAttribute("error", "DUT " + field + " cannot be empty");
				}
				else if(error.equals("Length"))
				{
					model.addAttribute("error", "Invalid " + field + " field length");
				}
			}
			return new ModelAndView("dynamic-dut");
		}
		else
		{
			return new ModelAndView("redirect:/login");
		}
	}
	
	/**
	 * Manages the creation of a new DUT if authenticated, redirects to login otherwise.
	 * 
     * @param newDut
     * 			DUT data to be validated and stored
     * @param result 
     * 			validation result
     * 
     * @return target view
     */	
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public @ResponseBody Dut addDut(@Valid @ModelAttribute("newDut") Dut newDut, BindingResult result)
	{	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{		
			if (dutService.create(newDut))
			{
				return newDut;
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Loads DUT data to be edited
	 * 
     * @param request
     * 			servlet request with dutId to be loaded
     * 
     * @return DUT data
     */
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public @ResponseBody Dut editDut(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			return dutService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("idDut")));
		}
		
		return new Dut();
	}
	
	/**
	 * Manages the modification of a DUT if authenticated, redirects to login otherwise.
	 * 
     * @param newDut
     * 			DUT data to be validated and modified
     * @param result
     * 			validation result
     * 
     * @return target view
     */
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public @ResponseBody Dut saveChanges(@Valid @ModelAttribute("newDut") Dut newDut, BindingResult result)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			if (result.hasErrors())
			{
				return null;
			}
			else
			{
				return dutService.update(newDut);
			}
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Manages the removal of a DUT if authenticated, redirects to login otherwise.
	 * 
     * @param request
     * 			servlet request with the ID of the DUT to be deleted
     * 
     * @return target view
     */
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public String delDut(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			String username = auth.getName();
			int dutID = Integer.parseInt(request.getParameter("idDut"));
			
			projectService.clearConfigByDut(username, dutID);
			dutService.delete(username, dutID);
		}
		
		return "redirect:/dut";
	}
	
	/**
	 * Stores the assigned DUT and redirects to the next view depending on the
	 * project type
	 * 
	 * @param model
	 * 			model to add objects needed by the view
	 * @param newProject
	 * 			project object with assigned DUT info
	 * 
	 * @return target view
	 */
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public @ResponseBody ModelAndView save(Model model, @ModelAttribute("newProject") Project newProject)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			if(!projectService.setDut(auth.getName(), newProject).equals("Conformance"))
			{
				return new ModelAndView("redirect:/gu");
			}
			else
			{
				return new ModelAndView("redirect:/ics?idProject="+newProject.getIdProject());
			}
		}
		else
		{
			return new ModelAndView("redirect:/login");
		}
	}
	
	/**
	 * Checks if a dut name already exists 
	 * 
	 * @param request
	 * 			servlet request with the dut name to be checked
	 * 
	 * @return false if exists, true otherwise
	 */
	@RequestMapping(value="/validateName", method = RequestMethod.GET)
	public @ResponseBody boolean validateName(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			return (!(dutService.exists(auth.getName(), 
					request.getParameter("name"), Integer.parseInt(request.getParameter("id")))));
		}
		
		return true;
	}
	
	/**
	 * Loads DUT's stored samples data to be displayed if authenticated, redirects
	 * to login otherwise.
	 * 
	 * @param request
	 * 			servlet request with the ID of the DUT whose samples have to be loaded.
	 * 
	 * @return list of samples
	 */
	@RequestMapping(value="/samples", method=RequestMethod.GET)
	public @ResponseBody List<Sample> samples(HttpServletRequest request)
	{
		return ControllerCommons.dataToHtml(dutService.getSampleData(Integer.parseInt(request.getParameter("idDut"))));
	}
	
	/**
	 * Manages the creation of a new sample if authenticated.
	 * 
	 * @param newSample
	 * 			sample data to be stored
	 * 
	 * @return target view
	 */
	@RequestMapping(value="/samples/add", method=RequestMethod.POST)
	public @ResponseBody Sample addSample(@ModelAttribute("newSample") Sample newSample)
	{	
		dutService.createSample(newSample);
		
		return newSample;
	}
	
	/**
	 * Manages the removal of a sample if authenticated, returns to login otherwise.
	 * 
	 * @param request
	 * 			servlet request with the ID of the sample to be removed.
	 * 
	 * @return target view
	 */
	@RequestMapping(value="/samples/delete", method=RequestMethod.POST)
	public String delSample(HttpServletRequest request)
	{	
		dutService.deleteSample(Integer.parseInt(request.getParameter("idSample")));
		return "redirect:/dut";
	}
	
	/**
	 * Manages the modification of a sample if authenticated, redirects to login otherwise.
	 * 
     * @param request
     * 			servlet request with the ID of the sample to be modified
     * 
     * @return sample data
     */
	@RequestMapping(value="/samples/edit", method=RequestMethod.GET)
	public @ResponseBody Sample editSample(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			return dutService.getSampleFormData(Integer.parseInt(request.getParameter("idSample")));
		}
		
		return new Sample();
	}
	
	/**
	 * Manages the modification of a sample if authenticated, redirects to login otherwise.
	 * 
     * @param newSample 
     * 			Sample data to be validated and modified
     * @param result
     * 			validation result
     * 
     * @return target view
     */
	@RequestMapping(value="/samples/edit", method=RequestMethod.POST)
	public @ResponseBody Sample saveSampleChanges(@Valid @ModelAttribute("newSample") Sample newSample,
			BindingResult result)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			if (result.hasErrors())
			{
				return null;
			}
			else
			{
				dutService.updateSample(newSample, auth.getName());
				return newSample;
			}
		}
		else
		{
			return null;
		}
	}
	
	@RequestMapping(value="/getId", method=RequestMethod.GET)
	public @ResponseBody Integer getDutId(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			return dutService.getDutId(auth.getName(), request.getParameter("dutName"));
		}
		else
		{
			return null;
		}
	}
}