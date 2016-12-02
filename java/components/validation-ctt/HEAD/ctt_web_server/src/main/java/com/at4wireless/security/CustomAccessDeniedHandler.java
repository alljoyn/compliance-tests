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
package com.at4wireless.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

/**
 * This class extends AccessDeniedHandler in order to redirect to login
 * when session expires.
 * 
 */
public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl
{
	/**
     * @param requestservlet
     * 				request that produces the exception
     * @param responseservlet
     * 				response to the exception
     * @param accessDeniedException
     * 				exception to be managed
     */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException
	{	
		if (accessDeniedException instanceof MissingCsrfTokenException
				|| accessDeniedException instanceof InvalidCsrfTokenException)
		{
			response.sendRedirect(request.getContextPath() + "/home?session_expired");
			// [AT4] This has to be changed since session is atm set not to expire
		}
	}
}