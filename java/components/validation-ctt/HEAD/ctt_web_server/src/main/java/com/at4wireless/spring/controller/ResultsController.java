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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.servlet.ModelAndView;

import com.at4wireless.security.FileEncryption;
import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.TestCaseResult;
import com.at4wireless.spring.service.ProjectService;
import com.at4wireless.spring.service.ResultService;
import com.at4wireless.spring.service.TestCaseService;
import com.at4wireless.spring.service.UserService;

/**
 * This class manages all actions related with results view
 * 
 */
@Controller
@RequestMapping(value="/results")
public class ResultsController
{
	@Autowired
	private ProjectService projectService;
	@Autowired
	private TestCaseService tcService;
	@Autowired
	private ResultService resultService;
	@Autowired
	private UserService userService;
	
	private static final int BUFFER_SIZE = 4096;
	
	/**
	 * Loads data to be displayed if logged, redirects to login
	 * otherwise.
	 * 
     * @param 	model 		model to add objects needed by the view
     * @param 	newProject 	project whose results are going to be shown
     * @return 				target view
     */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView results(Model model, @ModelAttribute("newProject") Project newProject)
	{
		String projectName = "";
		List<TestCaseResult> listTCResult = new ArrayList<TestCaseResult>();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			String username = auth.getName();
			Project p = projectService.getFormData(username, newProject.getIdProject());
			
			projectName = p.getName();
			listTCResult = resultService.getResults(p);
		}
		
		model.addAttribute("listTCResult", listTCResult);
		return new ModelAndView("dynamic-results");
	}
	
	/**
	 * Shows full log of selected row in other tab
	 * 
	 * @param	request		servlet request with the name of the log to be shown
	 * @return				full log
	 */
	@RequestMapping(value="/fullLog", method=RequestMethod.GET, produces="text/plain; charset=UTF-8")
	public @ResponseBody String showLog(HttpServletRequest request)
	{	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String log = "";
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			String username = auth.getName();
			String path = File.separator+"Allseen"+File.separator+"Users"+File.separator
					+username+File.separator+request.getParameter("id")
					+File.separator+request.getParameter("file");
			try
			{
				FileEncryption fE = new FileEncryption();
				fE.setAesSecretKey(userService.getAesSecretKey(username));
				log = fE.decrypt(RestController.readFile(path, StandardCharsets.UTF_8));
			}
			catch (GeneralSecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return log;
	}
	
	/**
	 * Creates Test Report
	 * 
	 * @param 	request		servlet request that contains the project whose test report is going to be created
	 * @return				true if test report creation succeeded, false otherwise. 
	 */
	@RequestMapping(value="/tr/create", method=RequestMethod.POST)
	public @ResponseBody boolean createTestReport(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			String username = auth.getName();
			Project p = projectService.getFormData(username,
					Integer.parseInt(request.getParameter("idProject")));
			String outputFileName = File.separator+"Allseen"
					+File.separator+"Users"+File.separator+username+File.separator
					+p.getIdProject()
					+File.separator+"TestReport.pdf";
			
			System.out.println(outputFileName);
			
			if(tcService.ranAll(p.getConfiguration(), p.getResults()))
			{
				String imgPath = request.getSession().getServletContext().getRealPath("/")+"resources"
						+File.separator+"img";
				return resultService.createTestReport(username, p, imgPath);
			}
		}
		return false;
	}
	
	/**
	 * Allows to download the test report
	 * 
	 * @param 	request			servlet request with the ID of the project whose test report is going to be sent
	 * @param 	response		servlet response with the test report
	 * @throws 	IOException		if file does not exist
	 */
	@RequestMapping(value="/tr/view", method = RequestMethod.GET)
	public void getTestReport(HttpServletRequest request, HttpServletResponse response)
			throws IOException
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			Project p = projectService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("idProject")));
			
			File downloadFile = new File(p.getTestReport());
	        FileInputStream inputStream = new FileInputStream(downloadFile);
	         
	        String mimeType = "application/pdf";
	 
	        // set content attributes for the response
	        response.setContentType(mimeType);
	        response.setContentLength((int) downloadFile.length());
	 
	        // set headers for the response
	        String headerKey = "Content-Disposition";
	        String headerValue = String.format("attachment; filename=\"%s\"",
	                downloadFile.getName());
	        response.setHeader(headerKey, headerValue);
	 
	        // get output stream of the response
	        OutputStream outStream = response.getOutputStream();
	 
	        byte[] buffer = new byte[BUFFER_SIZE];
	        int bytesRead = -1;
	 
	        // write bytes read from the input stream into the output stream
	        while ((bytesRead = inputStream.read(buffer)) != -1)
	        {
	            outStream.write(buffer, 0, bytesRead);
	        }
	 
	        inputStream.close();
	        outStream.close();
		}
	}
	
	/**
	 * Allows to download a package with the test report and all related logs
	 * 
	 * @param 	request		servlet request with the ID of the project whose zip is going to be sent
	 * @param 	response	servlet response with the zip
	 * @throws 	Exception	in something wrong occurred during zip creation
	 */
	@RequestMapping(value="tr/send", method = RequestMethod.GET)
	public void send(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			Project p = projectService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("idProject")));
			FileInputStream in = new FileInputStream(p.getTestReport());
			
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(File.separator+"Allseen"+File.separator
					+"Users"+File.separator+p.getUser()+File.separator+
					p.getIdProject()+File.separator+"TestReport.zip"));
			out.putNextEntry(new ZipEntry("TestReport.pdf"));
			
			byte[] b = new byte[1024];
			int count;
			
			while ((count = in.read(b))>0)
			{
				out.write(b,0,count);
			}
			
			for (String str : tcService.zipData(p.getConfiguration(), p.getResults()))
			{
				out.putNextEntry(new ZipEntry(str));
				ByteArrayInputStream bais = null;
				/*in = new FileInputStream(File.separator+"Allseen"+File.separator+"Users"
						+File.separator+p.getUser()
						+File.separator+p.getIdProject()+File.separator+str);*/
				
				String path = File.separator+"Allseen"+File.separator+"Users"+File.separator
						+p.getUser()+File.separator+p.getIdProject()
						+File.separator+str;
				try
				{
					FileEncryption fE = new FileEncryption();
					fE.setAesSecretKey(userService.getAesSecretKey(p.getUser()));
					String log = fE.decrypt(RestController.readFile(path, StandardCharsets.UTF_8));
					bais = new ByteArrayInputStream(log.getBytes());
				}
				catch (GeneralSecurityException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				while ((count = bais.read(b))>0)
				{
					out.write(b,0,count);
				}
			}
			out.close();
			in.close();
			
			//DOWNLOAD AFTER CREATING
			
			File downloadFile = new File(File.separator+"Allseen"+File.separator
					+"Users"+File.separator+p.getUser()+File.separator+p.getIdProject()+File.separator+"TestReport.zip");
	        FileInputStream inputStream = new FileInputStream(downloadFile);
	         
	        String mimeType = "application/octet-stream";
	 
	        // set content attributes for the response
	        response.setContentType(mimeType);
	        response.setContentLength((int) downloadFile.length());
	 
	        // set headers for the response
	        String headerKey = "Content-Disposition";
	        String headerValue = String.format("attachment; filename=\"%s\"",
	                downloadFile.getName());
	        response.setHeader(headerKey, headerValue);
	 
	        // get output stream of the response
	        OutputStream outStream = response.getOutputStream();
	 
	        byte[] buffer = new byte[BUFFER_SIZE];
	        int bytesRead = -1;
	 
	        // write bytes read from the input stream into the output stream
	        while ((bytesRead = inputStream.read(buffer)) != -1)
	        {
	            outStream.write(buffer, 0, bytesRead);
	        }
	 
	        inputStream.close();
	        outStream.close();
		}
	}
	
	/**
	 * Checks if all applicable testcases have been executed
	 * 
	 * @param 	request		servlet request with the ID of the project to be checked
	 * @return				true if executed all, false otherwise
	 */
	@RequestMapping(value="tr/ranAll", method=RequestMethod.GET)
	public @ResponseBody boolean ranAll(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			Project p = projectService.getFormData(auth.getName(), Integer.parseInt(request.getParameter("idProject")));
			
			if (p.isIsConfigured())
			{
				return tcService.ranAll(p.getConfiguration(), p.getResults());
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
}
