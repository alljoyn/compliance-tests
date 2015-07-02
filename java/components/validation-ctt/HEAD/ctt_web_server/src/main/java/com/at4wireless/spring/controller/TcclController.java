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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.at4wireless.spring.model.CertificationRelease;
import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.model.TestCase;
import com.at4wireless.spring.model.TestCaseTccl;
import com.at4wireless.spring.service.CertificationReleaseService;
import com.at4wireless.spring.service.TcclService;
import com.at4wireless.spring.service.TestCaseService;

@Controller
@RequestMapping(value="/admin")
public class TcclController {
	
	@Autowired
	private TcclService tcclService;
	
	@Autowired
	private CertificationReleaseService crService;
	
	@Autowired
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
	
	public class UploadMessage {
		String result;
		String message;
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
	}
	
	@RequestMapping(value="/uploadTCP", method=RequestMethod.POST)
	public @ResponseBody UploadMessage upload(MultipartHttpServletRequest request, HttpServletResponse response) {
		
		Iterator<String> itr = request.getFileNames();
		
		MultipartFile mpf = request.getFile(itr.next());
		UploadMessage uMessage = new UploadMessage();
		uMessage.setResult("Success");
		uMessage.setMessage(mpf.getOriginalFilename() + " uploaded!");
		
		if(mpf.getOriginalFilename().matches("TestCases_Package_v[0-9]{2}\\.[0-9]{2}\\.[0-0]{2}[a-z]?_[DR][0-9]\\.jar")) {
			try {
				String packageCertificationRelease = mpf.getOriginalFilename().split("_")[2];
				String packageVersion = mpf.getOriginalFilename().split("_")[3].split("\\.")[0];

				tcclService.addCertificationReleaseIfNotExists(packageCertificationRelease, packageVersion,
						request.getParameter("description"));
				mpf.transferTo(new File(File.separator+"Allseen"+File.separator+"Technology"+
						File.separator+mpf.getOriginalFilename()));
			} catch (IllegalStateException e) {
				uMessage.setResult("Fail");
				uMessage.setMessage("upload returned an error: "+e.getMessage());
				//e.printStackTrace();
			} catch (IOException e) {
				uMessage.setResult("Fail");
				uMessage.setMessage("upload returned an error: "+e.getMessage());
				//e.printStackTrace();
			} catch (Exception e) {
				uMessage.setResult("Fail");
				uMessage.setMessage(e.getMessage());
			}
		} else {
			uMessage.setResult("Fail");
			uMessage.setMessage("The package to be uploaded has a wrong name format!");
		}
		
		return uMessage;
	}
	
	@RequestMapping(value="/availablePackages", method=RequestMethod.GET)
	public @ResponseBody List<String> availablePackages() {
		File folder = new File(File.separator+"Allseen"+File.separator+"Technology");
		File[] listOfFiles = folder.listFiles();
		List<String> listFilenames = new ArrayList<String>();
		
		for (File f : listOfFiles) {
			if(f.isFile()) {
				String description = crService.getCertificationReleaseDescription(f.getName().split("_")[2]);
				listFilenames.add(f.getName()+": "+description);
			}
		}
		
		return listFilenames;
	}
	
	@RequestMapping(value="/uploadLA", method=RequestMethod.POST)
	public @ResponseBody UploadMessage uploadLA(MultipartHttpServletRequest request, HttpServletResponse response) {
		Iterator<String> itr = request.getFileNames();
		
		MultipartFile mpf = request.getFile(itr.next());
		UploadMessage uMessage = new UploadMessage();
		uMessage.setResult("Success");
		uMessage.setMessage(mpf.getOriginalFilename() + " uploaded!");
		
		if(mpf.getOriginalFilename().matches("CTT_Local_Agent_v[0-9]+\\.[0-9]+\\.[0-9]+_Installer\\.exe")) {
			try {
				mpf.transferTo(new File(File.separator+"Allseen"+File.separator+"localAgent"+
						File.separator+mpf.getOriginalFilename()));
			} catch (IllegalStateException e) {
				uMessage.setResult("Fail");
				uMessage.setMessage("upload returned an error: "+e.getMessage());
				//e.printStackTrace();
			} catch (IOException e) {
				uMessage.setResult("Fail");
				uMessage.setMessage("upload returned an error: "+e.getMessage());
				//e.printStackTrace();
			}
		} else {
			uMessage.setResult("Fail");
			uMessage.setMessage("The installer to be uploaded has a wrong name format!");
		}

		return uMessage;
	}
	
	@RequestMapping(value="/availableInstallers", method=RequestMethod.GET)
	public @ResponseBody List<String> availableInstallers() {
		File folder = new File(File.separator+"Allseen"+File.separator+"localAgent");
		File[] listOfFiles = folder.listFiles();
		List<String> listFilenames = new ArrayList<String>();
		
		for (File f : listOfFiles) {
			if(f.isFile()) {
				listFilenames.add(f.getName());
			}
		}
		
		return listFilenames;
	}
	
	@RequestMapping(value="/crVersions", method=RequestMethod.GET)
	public @ResponseBody List<CertificationRelease> crVersions() {
		return crService.list();
	}
}
