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

import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.service.CertificationReleaseService;
import com.at4wireless.spring.service.DutService;
import com.at4wireless.spring.service.GoldenUnitService;
import com.at4wireless.spring.service.ProjectService;
import com.at4wireless.spring.service.ServiceFrameworkService;
import com.at4wireless.spring.service.TcclService;

@Controller
@RequestMapping(value = "/project")
public class ProjectController {

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

	// View pre-charging method
	@RequestMapping(method = RequestMethod.GET)
	public String project(Model model,
			@RequestParam(value = "error", required = false) String error) {

		// Check login before accessing database
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String username = auth.getName();

			model.addAttribute("projectList", projectService.getTableData(username));
			model.addAttribute("newProject", new Project());
			model.addAttribute("certrelList", crService.list());
			//model.addAttribute("tcclList",tcclService.listByCR(crService.list().get(crService.list().size()-1).getName()));
			//model.addAttribute("tcclList",tcclService.listByCR(3));
			model.addAttribute("tcclList",tcclService.list());
			model.addAttribute("serviceList", sfService.list());
			model.addAttribute("dutList", dutService.getTableData(username));
			//model.addAttribute("goldenList", guService.getTableData(username));
			
			if (error != null) {
				if (error.equals("empty")) {
					model.addAttribute("error", "You have to select a project");
				} else if (error.equals("exists")) {
					model.addAttribute("error",
							"A project with that name already exists");
				} else if (error.equals("name")) {
					model.addAttribute("error", "Project name cannot be empty");
				}
			}
			return "project";
		} else {
			return "redirect:/login";
		}
	}

	// AddProject button action
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(@Valid @ModelAttribute("newProject") Project newProject,
			BindingResult result) {

		// Check login before accessing database
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			if (result.hasErrors()) {
				return "redirect:/project?error=name";
			} else {
				if(projectService.create(newProject)) {
					return "redirect:/project";
				} else {
					return "redirect:/project?error=exists";
				}
			}
		} else {
			return "redirect:/login";
		}
	}

	// EditProject button actions
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public @ResponseBody Project edit(HttpServletRequest request) {

		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return projectService.getFormData(auth.getName(), 
					Integer.parseInt(request.getParameter("data")));
		}

		return new Project();
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@Valid @ModelAttribute("newProject") Project newProject,
			BindingResult result) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			if (result.hasErrors()) {
				return "redirect:/project?error=name";
			} else {
				projectService.update(newProject);
				return "redirect:/project";
			}
		} else {
			return "redirect:/login";
		}
	}

	// DeleteProject button action
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String delete(HttpServletRequest request) {

		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String username = auth.getName();
			projectService.delete(username, Integer.parseInt(request.getParameter("data")));
		}

		return "redirect:/project";
	}
	
	@RequestMapping(value="/loadTccl", method = RequestMethod.GET)
	public @ResponseBody List<Tccl> loadTccl(HttpServletRequest request) {
		return tcclService.listByCR(Integer.parseInt(request.getParameter("idCertRel")));
	}
}
