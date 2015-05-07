package com.at4wireless.spring.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.at4wireless.spring.model.User;
import com.at4wireless.spring.service.UserService;

@Controller
@RequestMapping(value={"/", "/login"})
public class UserController {
	@Autowired
	private UserService userService;
	
	//@RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView login(
		@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout,
		@RequestParam(value = "session_expired", required = false) String session_expired,
		@RequestParam(value = "field", required = false) String field) {
 
		ModelAndView model = new ModelAndView();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			if(!auth.getName().equals("Administrator")) {
				return new ModelAndView("forward:/project");
			} else {
				return new ModelAndView("forward:/admin");
			}
		} else {
		
			model.addObject("newUser", new User()); //Add object to be used with addUser method
			
			if (error != null) {
				if(error.equals("exists")) {
					model.addObject("error", "User already exists");
				} else if (error.equals("Size")) {
					model.addObject("error", "Invalid "+field+" size. It must be between 5 and 13 characters");
				} else if (error.equals("AssertTrue")) {
					model.addObject("error", "Passwords mismatch");
				} else {
					model.addObject("error", "Invalid username and password!"); //if bad login
				}
			}
	 
			if (logout != null) {
				model.addObject("msg", "You've been logged out successfully."); //if successful logout
			}
			
			if (session_expired!=null) {
				model.addObject("error", "Your session has expired. Please, sign in again.");
			}
			model.setViewName("login");
	 
			return model;
		}
 
	}
	
	@RequestMapping(value="/addUser", method=RequestMethod.POST)
	public String addUser(@Valid @ModelAttribute("newUser") User newUser,
			BindingResult result) {
	
		if (result.hasErrors()) {
			List<ObjectError> errorList = result.getAllErrors();
			String[] str2 = errorList.get(0).getCodes()[1].split("[\\.]+");
			System.out.println(str2[0]+":"+str2[1]);
			return "redirect:/login?error="+str2[0]+"&field="+str2[1];
		} else {
			if(userService.addUser(newUser)) {
				return "redirect:/login";
			} else {
				return "redirect:/login?error=exists";
			}
		}
	}
	
	@RequestMapping(value="/requestPassword", method=RequestMethod.POST)
	public String requestPassword(@ModelAttribute("newUser") User newUser) {
		
		//JTF: NOT IMPLEMENTED YET
		return "redirect:/login";
	}
}
