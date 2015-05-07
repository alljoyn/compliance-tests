package com.at4wireless.spring.controller;

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

import com.at4wireless.spring.model.Parameter;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.service.ParameterService;
import com.at4wireless.spring.service.ProjectService;

@Controller
@RequestMapping(value="/parameter")
public class ParameterController {

	@Autowired
	private ParameterService parameterService;
	
	@Autowired
	private ProjectService projectService;
	
	/*@Autowired
	private ServiceFrameworkService sfService;*/
	
	//View pre-charging method
	@RequestMapping(method = RequestMethod.GET)
	public String ixit(Model model, @ModelAttribute("newProject") Project newProject) {		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			List<Parameter> listParameter = new ArrayList<Parameter>();
			Project p = projectService.getFormData(auth.getName(), newProject.getIdProject());
			
			listParameter = parameterService.load(p.isIsConfigured(),
					p.getConfiguration());
			
			model.addAttribute("listParameter", listParameter);
			return "parameter";
		} else {
			return "redirect:/login";
		}
	}
}
