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

import com.at4wireless.spring.model.Ixit;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.ServiceFramework;
import com.at4wireless.spring.service.DutService;
import com.at4wireless.spring.service.IxitService;
import com.at4wireless.spring.service.ProjectService;
import com.at4wireless.spring.service.ServiceFrameworkService;

@Controller
@RequestMapping(value="/ixit")
public class IxitController {

	@Autowired
	private IxitService ixitService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private DutService dutService;
	
	@Autowired
	private ServiceFrameworkService sfService;
	
	//View pre-charging method
	@RequestMapping(method = RequestMethod.GET)
	public String ixit(Model model, @ModelAttribute("newProject") Project newProject) {		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			List<Ixit> listIxit = new ArrayList<Ixit>();
			List<ServiceFramework> listService = new ArrayList<ServiceFramework>();
			Project p = projectService.getFormData(auth.getName(), newProject.getIdProject());
			
			listIxit = ixitService.load(projectService.getServicesData(p.getIdProject()), p.isIsConfigured(),
					p.getConfiguration());
			dutService.setValues(auth.getName(),p.getIdDut(),listIxit);
			for (BigInteger bi : projectService.getServicesData(newProject.getIdProject())) {
				listService.add(sfService.list().get(bi.intValue()-1));
			}
			model.addAttribute("ixitList", listIxit);
			model.addAttribute("serviceList", listService);
			return "ixit";
		} else {
			return "redirect:/login";
		}
	}
}
