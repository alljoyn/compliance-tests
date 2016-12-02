/*******************************************************************************
 *  * 
 *      Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *      Source Project Contributors and others.
 *      
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/

package com.at4wireless.spring.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value={"", "/", "/home"})
public class UserController
{	
	@RequestMapping(method=RequestMethod.GET)
	public String login(Model model,
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,
			@RequestParam(value = "session_expired", required = false) String session_expired,
			@RequestParam(value = "field", required = false) String field,
			HttpServletRequest request) {
		
		if (!(SecurityContextHolder.getContext().getAuthentication()
				instanceof AnonymousAuthenticationToken)) {
			if (!request.isUserInRole("ROLE_ADMIN")) {
				return "forward:/common";
			} else {
				return "forward:/admin";
			}
		} else {
			if (logout != null) {
				model.addAttribute("msg", "You have logged out of this application, but may still have an active single-sign on session with CAS");
			}
			
			if (session_expired != null) {
				model.addAttribute("session_expired", "Your session has expired. Please login again");
			}
			
			return "login";
		}
	}
}