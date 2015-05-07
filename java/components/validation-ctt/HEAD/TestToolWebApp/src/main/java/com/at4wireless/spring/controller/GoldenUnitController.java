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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.at4wireless.spring.model.GoldenUnit;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.service.GoldenUnitService;
import com.at4wireless.spring.service.ProjectService;

@Controller
@RequestMapping(value="/gu")
public class GoldenUnitController {
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private GoldenUnitService guService;
	
	//View pre-charging method
	@RequestMapping(method = RequestMethod.GET)
	public String gu(Model model, @RequestParam(value = "error", required = false) String error) {
		  
		//Check login before accessing database
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String username = auth.getName();
			
			model.addAttribute("guList", guService.getTableData(username));
			model.addAttribute("categoryList", guService.getCategories());
			model.addAttribute("newProject", new Project());
			model.addAttribute("newGu", new GoldenUnit());

			
			if (error != null) {
				if(error.equals("empty")) {
				model.addAttribute("error", "You have to select a GU");
				} else if(error.equals("exists")) {
					model.addAttribute("error", "A GU with that name already exists");
				} else if(error.equals("name")) {
					model.addAttribute("error", "GU name cannot be empty");
				}
			}
			return "gu";
		} else {
			return "redirect:/login";
		}
	}
	
	//AddDUT button action
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String addGu(@Valid @ModelAttribute("newGu") GoldenUnit newGu,
			BindingResult result) {
		
		//Check login before accessing database
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (!(auth instanceof AnonymousAuthenticationToken)) {		
					if (result.hasErrors()) {
						return "redirect:/gu?error=name";
					} else {
						if(guService.create(newGu)) {
							return "redirect:/gu";
						} else {
							return "redirect:/gu?error=exists";
						}
					}
				} else {
					return "redirect:/login";
				}
	}
	
	//EditDUT button actions
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public @ResponseBody GoldenUnit editDut(HttpServletRequest request) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return guService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("data")));
		}
		
		return new GoldenUnit();
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String saveChanges(@Valid @ModelAttribute("newGu") GoldenUnit newGu,
			BindingResult result) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			if (result.hasErrors()) {
				return "redirect:/gu?error=name";
			} else {
				guService.update(newGu);
				return "redirect:/gu";
			}
		} else {
			return "redirect:/login";
		}
	}
	
	//DeleteDUT button action
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public String delGu(HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String username = auth.getName();
			//projectService.clearConfigByGu(username, Integer.parseInt(request.getParameter("data")));
			guService.delete(username, Integer.parseInt(request.getParameter("data")));
			return "redirect:/gu";
		} else {
			return "redirect:/login";
		}
	}
	
	@RequestMapping(value="/save", method=RequestMethod.POST)
	/*public String save(Model model, @ModelAttribute("newProject") Project newProject,
			RedirectAttributes redirectAttributes) {*/
	public String save(Model model, @ModelAttribute("newProject") Project newProject) {
		//Check login before accessing database
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			projectService.setGu(auth.getName(),newProject);
			//redirectAttributes.addFlashAttribute("newProject", newProject);
			return "redirect:/ics?idProject="+newProject.getIdProject();
		} else {
			return "redirect:/login";
		}
		
		
	}
}
