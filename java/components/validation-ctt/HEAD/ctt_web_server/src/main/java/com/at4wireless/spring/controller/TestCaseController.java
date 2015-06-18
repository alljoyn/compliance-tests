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
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.TestCase;
import com.at4wireless.spring.service.ProjectService;
import com.at4wireless.spring.service.TestCaseService;

/**
 * This class manages all actions related to Test Cases view
 */
@Controller
@RequestMapping(value="/testcase")
public class TestCaseController {
	
	@Autowired
	private TestCaseService tcService;
	
	@Autowired
	private ProjectService projectService;
	
	/**
	 * Loads the view and creates the necessary model objects
	 * 
     * @param 	model 		model to add objects needed by the view
     * @param 	newProject 	ID of the project whose parameters have to be loaded
     * @return 				target view
     */
	@RequestMapping(method = RequestMethod.GET)
	public String testcase(Model model) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			model.addAttribute("tcList", new ArrayList<TestCase>());
			model.addAttribute("newProject", new Project());
		}
		
		return "testcase";
	}
	
	/**
	 * Loads all applicable testcases with the received ICS configuration
	 * 
     * @param 	request		servlet request with the ICS configuration
     * @return 				target view
     */
	@RequestMapping(value="load", method=RequestMethod.GET)
	public @ResponseBody List<TestCase> load(HttpServletRequest request) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			Project p = projectService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("data[idProject]")));
			
			return tcService.load(projectService.getServicesData(p.getIdProject()),
					request.getParameterMap(), p.getType(), p.getIdCertrel());
		} else {
			return new ArrayList<TestCase>();
		}
	}
	
	/**
	 * Loads all disabled testcases on selected TCCL
	 * 
     * @param 	request		servlet request with the ID of the project being configured
     * @return 				target view
     */
	@RequestMapping(value="disabled", method=RequestMethod.GET)
	public @ResponseBody List<Integer> disabled(HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			Project p = projectService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("idProject")));
			
			return tcService.getDisabled(p.getIdTccl());
		} else {
			return new ArrayList<Integer>();
		}
	}
}
