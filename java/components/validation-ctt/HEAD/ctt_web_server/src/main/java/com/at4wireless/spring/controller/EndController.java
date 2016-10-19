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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.at4wireless.spring.service.EndService;
import com.at4wireless.spring.service.ProjectService;

/**
 * This class manages all actions related to the end of the configuration
 */
@Controller
@RequestMapping(value="/end")
public class EndController
{
	@Autowired
	private ProjectService projectService;
	@Autowired
	private EndService endService;
	
	static final Logger log = LogManager.getLogger(EndController.class);
	
	private static final int BUFFER_SIZE = 4096;
	
	/**
	 * Loads the view
	 * 
     * @return 	target view
     */
	@RequestMapping(method=RequestMethod.GET)
	public String end()
	{
		return "end";
	}
	
	/**
	 * Allows to download the last version of the Local Agent
	 * @param 	response		servlet response with the file to be downloaded
	 * @throws 	IOException		if file does not exist
	 */
	@RequestMapping(value="/download", method=RequestMethod.GET)
    public void download(HttpServletResponse response) throws IOException
    {
		String fullPath = File.separator+"Allseen"+File.separator
				+"localAgent"+File.separator+endService.lastUpload();
        File downloadFile = new File(fullPath);
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
	
	/**
	 * Saves project configuration 
	 * 
	 * @param request
	 * 			request containing the project information to be saved
	 * 
	 * @return target view
	 */
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public ModelAndView save(HttpServletRequest request)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		// Checks if user is authenticated
		if (!(auth instanceof AnonymousAuthenticationToken))
		{
			boolean isProjectConfigured = Boolean.parseBoolean(request.getParameter("data[isConfigured]"));
			
			if (isProjectConfigured)
			{
				log.debug("Project is already configured. Modifying configuration file...");
				endService.modifyXML(auth.getName(), request.getParameterMap());
			}
			else
			{
				String url = endService.createXML(auth.getName(), request.getParameterMap());
				projectService.configProject(Integer.parseInt(request.getParameter("data[idProject]")), url);
			}
		}
		else
		{
			// An status code should be returned to front-end
		}

		return new ModelAndView("redirect:/project");
	}
}
