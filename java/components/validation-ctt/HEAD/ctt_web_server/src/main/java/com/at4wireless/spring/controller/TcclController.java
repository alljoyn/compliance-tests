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

import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.model.TestCase;
import com.at4wireless.spring.model.TestCaseTccl;
import com.at4wireless.spring.service.CertificationReleaseService;
import com.at4wireless.spring.service.TcclService;
import com.at4wireless.spring.service.TestCaseService;

@Controller
@RequestMapping(value="/admin")
public class TcclController {
	
	/*@Autowired
	private TcclDAO tcclDao;*/
	
	@Autowired
	private TcclService tcclService;
	
	@Autowired
	private CertificationReleaseService crService;
	
	@Autowired
	//private TcDAO tcDao;
	private TestCaseService tcService;

	//View pre-charging method
	@RequestMapping(method = RequestMethod.GET)
	public String tccl(Model model) {
		  
		//Check login before accessing database
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			List<Tccl> tcclList = tcclService.list();
			List<TestCase> tcList = tcService.list();
			
			model.addAttribute("certrelList", crService.list());
			
			model.addAttribute("tcclList",tcclList);
			model.addAttribute("tcList",tcList);
			model.addAttribute("newTccl", new Tccl());
			
			return "admin";
		} else {
			return "redirect:/login";
		}
	}
	
	@RequestMapping(value="/testcases", method=RequestMethod.GET)
	public @ResponseBody List<TestCase> testcases(HttpServletRequest request) {
		return tcService.list(Integer.parseInt(request.getParameter("idCertRel")));
	}
	
	@RequestMapping(value="/tccl/add", method=RequestMethod.POST)
	public @ResponseBody Tccl add(HttpServletRequest request) {
		
		return tcclService.create(request.getParameterMap());
	}
	
	@RequestMapping(value="/tccl/delete", method=RequestMethod.POST)
	public @ResponseBody void delete(HttpServletRequest request) {
		tcclService.delete(Integer.parseInt(request.getParameter("idTccl")));
	}
	
	@RequestMapping(value="/tccl/edit", method=RequestMethod.GET)
	public @ResponseBody List<TestCaseTccl> editTccl(HttpServletRequest request) {
		return tcclService.getTccl(Integer.parseInt(request.getParameter("idTccl")));
	}
	
	@RequestMapping(value="/tccl/edit", method=RequestMethod.POST)
	public @ResponseBody Tccl saveChanges(HttpServletRequest request) {
		return tcclService.update(request.getParameterMap());
	}
}
