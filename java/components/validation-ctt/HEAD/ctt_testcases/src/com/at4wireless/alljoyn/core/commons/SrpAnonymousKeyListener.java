/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.core.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alljoyn.bus.AuthListener;

import com.at4wireless.alljoyn.core.commons.AuthPasswordHandler;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class SrpAnonymousKeyListener implements AuthListener
{
	public static String KEY_STORE_FINE_NAME;
	public static final char [] DEFAULT_PINCODE = new char[]{'0','0','0','0','0','0'};
	private static final Logger logger = new WindowsLoggerImpl(SrpAnonymousKeyListener.class.getSimpleName());
	AuthPasswordHandler m_passwordHandler;

	private List<String> authMechanisms;
	
	public SrpAnonymousKeyListener(AuthPasswordHandler passwordHandler, Logger genericLogger)
	{
		m_passwordHandler = passwordHandler;
		authMechanisms = new ArrayList<String>(2);
		authMechanisms.add("ALLJOYN_SRP_KEYX");
		authMechanisms.add("ALLJOYN_ECDHE_PSK");
	}
	
	public SrpAnonymousKeyListener(AuthPasswordHandler passwordHandler, Logger genericLogger, String[] authMechanisms)
	{
		this(passwordHandler, genericLogger);
		if ( authMechanisms == null ) {
			
			throw new IllegalArgumentException("authMechanisms is undefined");
		}
		
		this.authMechanisms = Arrays.asList(authMechanisms);
		logger.debug("Supported authentication mechanisms: '" + this.authMechanisms + "'");
	}
	
	@Override
	public boolean requested(String mechanism, String peer, int count, String userName,  AuthRequest[] requests) 
	{
		logger.info(" ** " + "requested, mechanism = " + mechanism + " peer = " + peer);
		if ( !this.authMechanisms.contains(mechanism) )
		{
			return false;
		}
		else
		{
			if (!(requests[0] instanceof PasswordRequest)) 
			{
				return false;
			}
			char [] pinCode = DEFAULT_PINCODE;
			// if pincode not set for this peer, the function will return null, at that case, use the default one.
			if (m_passwordHandler != null && m_passwordHandler.getPassword(peer)!= null)
			{
				pinCode = m_passwordHandler.getPassword(peer);
			}
			((PasswordRequest) requests[0]).setPassword(pinCode);
			return true;
		}
	}
   
	@Override
	public void completed(String mechanism, String authPeer, boolean authenticated) 
	{
		if (! authenticated)
		{
			logger.info(" ** " + authPeer + " failed to authenticate");
			return;
		}
		logger.info(" ** " + authPeer + " successfully authenticated");
		
		m_passwordHandler.completed(mechanism, authPeer, authenticated);
	}

	
	public String[] getAuthMechanisms()
	{
		return authMechanisms.toArray(new String[authMechanisms.size()]);
	}
	
	public String getAuthMechanismsAsString() 
	{	
		final String separator = " ";
		StringBuilder sb       = new StringBuilder();
		for (String mech : authMechanisms ) {
			
			sb.append(mech).append(separator);
		}
		
		int length = sb.length();
		if ( length >= 1 ) {
			sb.deleteCharAt(length - 1); //remove the last added separator
		}
		
		return sb.toString();
	}
}