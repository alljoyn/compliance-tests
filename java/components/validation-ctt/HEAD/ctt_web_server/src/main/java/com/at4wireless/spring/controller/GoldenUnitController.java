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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.at4wireless.spring.common.DataTablesData;
import com.at4wireless.spring.model.GoldenUnit;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.dto.GoldenUnitDT;
import com.at4wireless.spring.service.GoldenUnitService;
import com.at4wireless.spring.service.ProjectService;


/**
 * This class manages all actions related to Golden Unit view
 */
@Controller
@RequestMapping(value="/gu")
public class GoldenUnitController
{	
	static final Logger log = LogManager.getLogger(GoldenUnitController.class);
	
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
	public ModelAndView gu(Model model, @RequestParam(value = "error", required = false) String error)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			//String username = auth.getName();
			
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
			return new ModelAndView("dynamic-gu");
		}
		else
		{
			return new ModelAndView("redirect:/login");
		}
	}
	
	@RequestMapping(value = "/dataTable", method = RequestMethod.GET)
	public @ResponseBody DataTablesData dataTable()
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		DataTablesData dtData = new DataTablesData();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			List<GoldenUnitDT> listOfDataTablesGoldenUnits = new ArrayList<GoldenUnitDT>();

			for (GoldenUnit gu : guService.getTableData(auth.getName()))
			{
				listOfDataTablesGoldenUnits.add(new GoldenUnitDT(gu, guService.getCategoryById(gu.getCategory()).getName()));
			}
			
			dtData.data = listOfDataTablesGoldenUnits;
		}
		
		
		return dtData;
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
	public @ResponseBody GoldenUnitDT addGu(@Valid @ModelAttribute("newGu") GoldenUnit newGu,
			BindingResult result)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{		
			if (result.hasErrors())
			{
				//return "redirect:/gu?error=name";
				return null;
			}
			else
			{
				/*if(guService.create(newGu))
				{
					return "redirect:/gu";
				}
				else
				{
					return "redirect:/gu?error=exists";
				}*/
				GoldenUnit savedGu = guService.create(newGu);
				return new GoldenUnitDT(savedGu, guService.getCategoryById(savedGu.getCategory()).getName());
			}
		}
		else
		{
			//return "redirect:/login";
			return null;
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
			return guService.getFormData(auth.getName(), new BigInteger(request.getParameter("idGu")));
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
	public @ResponseBody GoldenUnitDT saveChanges(@Valid @ModelAttribute("newGu") GoldenUnit newGu, BindingResult result)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			if (result.hasErrors())
			{
				//return "redirect:/gu?error=name";
				return null;
			}
			else
			{
				GoldenUnit savedGu = guService.update(newGu);
				return new GoldenUnitDT(savedGu, guService.getCategoryById(savedGu.getCategory()).getName());
				//return "redirect:/gu";
			}
		}
		else
		{
			//return "redirect:/login";
			return null;
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
			guService.delete(username, new BigInteger(request.getParameter("idGu")));
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
	public @ResponseBody ModelAndView save(Model model, @ModelAttribute("newProject") Project newProject)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			log.trace("User authenticated");
			
			projectService.setGu(auth.getName(), newProject);
			
			log.trace(String.format("%d golden units stored", guService.getGuList(newProject.getIdProject()).size()));
			return new ModelAndView("redirect:/ics?idProject=" + newProject.getIdProject());
		}
		else
		{
			return new ModelAndView("redirect:/login");
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
					request.getParameter("name"), new BigInteger(request.getParameter("id")))));
		}
		return true;
	}
}
