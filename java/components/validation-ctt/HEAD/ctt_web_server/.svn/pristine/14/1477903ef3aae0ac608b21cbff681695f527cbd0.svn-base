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
package com.at4wireless.security;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * This class extends AuthenticationSuccessHandler in order to redirect to
 * the correct site depending on user role.
 * 
 */
public class MySimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler 
{
	protected Log logger = LogFactory.getLog(this.getClass());
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
 
	/**
     * @param 	request			servlet authentication request
     * @param 	response		servlet response to the authentication
     * @param 	authentication	authentication request token to be managed
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
    		HttpServletResponse response, Authentication authentication) throws IOException
    {
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }
 
	/**
     * @param 	request			servlet authentication request
     * @param 	response		servlet response to the authentication
     * @param 	authentication	authentication request token to be managed
     */
    protected void handle(HttpServletRequest request, 
    		HttpServletResponse response, Authentication authentication) throws IOException 
    {
        String targetUrl = determineTargetUrl(authentication);
 
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
 
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }
 
	/**
     * @param 	authentication 	authentication request token to be managed
     * @return 					target url depending on user role
     */
    protected String determineTargetUrl(Authentication authentication)
    {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
            	return "/project";
            } else if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
            	return "/admin";
            }
        }
        
        throw new IllegalStateException();
    }
 
	/**
     * @param 	request		request with the session whose authentication attributes are going
     *            			to be cleared
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
 
	/**
     * @param 	redirectStrategy 	a new redirect strategy to override the existent
     */
    public void setRedirectStrategy(RedirectStrategy redirectStrategy)
    {
        this.redirectStrategy = redirectStrategy;
    }
    
	/**
     * @return	the defined redirect strategy
     */
    protected RedirectStrategy getRedirectStrategy()
    {
        return redirectStrategy;
    }
}
