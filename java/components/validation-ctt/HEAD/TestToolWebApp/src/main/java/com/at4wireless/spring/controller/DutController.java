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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.at4wireless.spring.model.Dut;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.Sample;
import com.at4wireless.spring.service.DutService;
import com.at4wireless.spring.service.ProjectService;

@Controller
@RequestMapping(value="/dut")
public class DutController {
		
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private DutService dutService;
	
	//View pre-charging method
	@RequestMapping(method = RequestMethod.GET)
	public String dut(Model model, @RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "field", required = false) String field) {
	  
		//Check login before accessing database
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String username = auth.getName();
			
			model.addAttribute("dutList",dutService.getTableData(username));
			model.addAttribute("newProject", new Project());
			model.addAttribute("newDut", new Dut());
			model.addAttribute("newSample", new Sample());
			
			if (error != null) {
				if(error.equals("empty")) {
				model.addAttribute("error", "You have to select a DUT");
				} else if(error.equals("exists")) {
					model.addAttribute("error", "A DUT with that name already exists");
				} else if(error.equals("NotEmpty")) {
					model.addAttribute("error", "DUT "+field+" cannot be empty");
				} else if(error.equals("Length")) {
					model.addAttribute("error", "Invalid "+field+" field length");
				}
			}
			return "dut";
		  } else {
			  return "redirect:/login";
		  }
	}
	
	//AddDUT button action
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String addDut(@Valid @ModelAttribute("newDut") Dut newDut,
			BindingResult result) {
		
		//Check login before accessing database
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {		
			if (result.hasErrors()) {
				List<ObjectError> errorList = result.getAllErrors();
				/*for(ObjectError oe : errorList) {
					System.out.println(oe.toString());
					System.out.println(oe.getObjectName()+":"+oe.getDefaultMessage());
					for(String str : oe.getCodes()) {
						System.out.println(str);
					}
				}*/
				/*String[] str = errorList.get(0).getCodes();
				String[] str2 = str[1].split("[\\.]+");*/
				String[] str2 = errorList.get(0).getCodes()[1].split("[\\.]+");
				return "redirect:/dut?error="+str2[0]+"&field="+str2[1];
			} else {
				if(dutService.create(newDut)) {
					return "redirect:/dut";
				} else {
					return "redirect:/dut?error=exists";
				}
			}
		} else {
			return "redirect:/login";
		}
	}
	
	//EditDUT button actions
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public @ResponseBody Dut editDut(HttpServletRequest request) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return dutService.getFormData(auth.getName(),Integer.parseInt(request.getParameter("data")));
		}
		
		return new Dut();
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String saveChanges(@Valid @ModelAttribute("newDut") Dut newDut,
			BindingResult result) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			if (result.hasErrors()) {
				List<ObjectError> errorList = result.getAllErrors();
				String[] str2 = errorList.get(0).getCodes()[1].split("[\\.]+");
				return "redirect:/dut?error="+str2[0]+"&field="+str2[1];
			} else {
				dutService.update(newDut);
				return "redirect:/dut";
			}
		} else {
			return "redirect:/login";
		}
	}
	
	//DeleteDUT button action
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public String delDut(HttpServletRequest request) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			String username = auth.getName();
			projectService.clearConfigByDut(username, Integer.parseInt(request.getParameter("data")));
			dutService.delete(username, Integer.parseInt(request.getParameter("data")));
		}
		
		return "redirect:/dut";
	}
	
	@RequestMapping(value="/save", method=RequestMethod.POST)
	/*public String save(Model model, @ModelAttribute("newProject") Project newProject, 
			RedirectAttributes redirectAttributes) {*/
	public String save(Model model, @ModelAttribute("newProject") Project newProject) {
		
		//Check login before accessing database
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			if(!projectService.setDut(auth.getName(),newProject).equals("Conformance")) {
				return "redirect:/gu";
			} else {
				//redirectAttributes.addFlashAttribute("newProject", newProject);
				return "redirect:/ics?idProject="+newProject.getIdProject();
			}
		} else {
			return "redirect:/login";
		}
	}
	
	@RequestMapping(value="/samples", method=RequestMethod.GET)
	public @ResponseBody List<Sample> samples(HttpServletRequest request) {
		return dutService.getSampleData(Integer.parseInt(request.getParameter("idDut")));
	}
	
	@RequestMapping(value="/samples/add", method=RequestMethod.POST)
	public String addSample(Model model, @ModelAttribute("newSample") Sample newSample) {
		
		dutService.createSample(newSample);
		return "redirect:/dut";
	}
	
	@RequestMapping(value="/samples/delete", method=RequestMethod.POST)
	public String delSample(HttpServletRequest request) {
		
		dutService.deleteSample(Integer.parseInt(request.getParameter("data")));
		
		return "redirect:/dut";
	}
	
	//EditDUT button actions
	@RequestMapping(value="/samples/edit", method=RequestMethod.GET)
	public @ResponseBody Sample editSample(HttpServletRequest request) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {

			return dutService.getSampleFormData(Integer.parseInt(request.getParameter("data")));
		}
		
		return new Sample();
	}
	
	@RequestMapping(value="/samples/edit", method=RequestMethod.POST)
	public String saveSampleChanges(@Valid @ModelAttribute("newSample") Sample newSample,
			BindingResult result) {
		
		//Check login before accessing database
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			if(result.hasErrors()) {
				return "redirect:/dut?error=name";
			} else {
				dutService.updateSample(newSample);
				return "redirect:/dut";
			}
		} else {
			return "redirect:/login";
		}
	}
}
