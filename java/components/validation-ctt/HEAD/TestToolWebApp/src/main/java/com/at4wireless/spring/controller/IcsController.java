package com.at4wireless.spring.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import com.at4wireless.spring.model.Ics;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.Result;
import com.at4wireless.spring.model.ServiceFramework;
import com.at4wireless.spring.service.DutService;
import com.at4wireless.spring.service.IcsService;
import com.at4wireless.spring.service.ProjectService;
import com.at4wireless.spring.service.ServiceFrameworkService;

@Controller
@RequestMapping(value="/ics")
public class IcsController {
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private IcsService icsService;
	
	@Autowired
	private ServiceFrameworkService sfService;
	
	//View pre-charging method
	@RequestMapping(method = RequestMethod.GET)
	public String ics(Model model, @ModelAttribute("newProject") Project newProject) {
		
		//Check login before accessing database
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			List<Ics> listIcs = new ArrayList<Ics>();
			List<ServiceFramework> listService = new ArrayList<ServiceFramework>();
			Project p = projectService.getFormData(auth.getName(), newProject.getIdProject());

			listIcs = icsService.load(projectService.getServicesData(p.getIdProject()), p.isIsConfigured(),
					p.getConfiguration());
			for (BigInteger bi : projectService.getServicesData(newProject.getIdProject())) {
				listService.add(sfService.list().get(bi.intValue()-1));
			}
			
			model.addAttribute("icsList", listIcs);
			model.addAttribute("serviceList", listService);

			return "ics";
		} else {
			return "redirect:/login";
		}
		

	}
	
	//SCR button action
	@RequestMapping(value="/scr", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody List<Result> SCR(HttpServletRequest request) {
		List<Result> scrCheck = new ArrayList<Result>();
		Map<String, String[]> map = request.getParameterMap();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			scrCheck = icsService.check(projectService.getServicesData(Integer.parseInt(
					request.getParameter("data[idProject]"))),map);
		}		
		return scrCheck;
	}
	
	@RequestMapping(value="/decide", method=RequestMethod.GET)
	public String decide(@ModelAttribute("newProject") Project newProject) {
		//Check login before accessing database
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String type = projectService.getFormData(auth.getName(), newProject.getIdProject()).getType();
			if(type.equals("Conformance")) {
				return "redirect:/dut";
			} else {
				return "redirect:/gu";
			}
		} else {
			return "redirect:/login";
		}
	}
}
