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

@Controller
@RequestMapping(value="/testcase")
public class TestCaseController {
	
	@Autowired
	private TestCaseService tcService;
	
	@Autowired
	private ProjectService projectService;
	
	//View pre-charging method
	@RequestMapping(method = RequestMethod.GET)
	public String testcase(Model model) {
		
		//Check login before accessing database
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			model.addAttribute("tcList", new ArrayList<TestCase>());
			model.addAttribute("newProject", new Project());
		}
		
		return "testcase";
	}
	
	@RequestMapping(value="load", method=RequestMethod.GET)
	public @ResponseBody List<TestCase> load(HttpServletRequest request) {
		//Check login before accessing database
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			Project p = projectService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("data[idProject]")));
			
			return tcService.load(projectService.getServicesData(p.getIdProject()),
					request.getParameterMap(), p.getType(), p.getIdCertrel());
		} else {
			return new ArrayList<TestCase>();
		}
	}
	
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
