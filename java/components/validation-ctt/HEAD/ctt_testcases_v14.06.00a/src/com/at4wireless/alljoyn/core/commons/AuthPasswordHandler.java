/*Author: AT4 wireless
 *Date: 2014/11/28
 *Version: 1
 *Description: Definition of the interface org.alljoyn.services.android.security.AuthPasswordHandler;
 *	for Windows 7
 */	
package com.at4wireless.alljoyn.core.commons;

public interface AuthPasswordHandler 
{
	/**
	 * When a client tries to access one of the board's secure interfaces, AllJoyn Bus will ask the board
	 * for a password and match it with the one it receives from the client.
	 * @param peerName the AllJoyn bus name of the peer
	 * @return the password
	 */
	public char[] getPassword(String peerName);
	
	/**
	 * AllJoyn Bus communicates the result of a security check, and lets the board handle it.
	 * On the board's side this is pretty much FYI, not as important as on the client's side where failures mean
	 * denial of service and require user's attention.
	 * @param mechanism the Authentication mechanism
	 * @param authPeer the Bus name of the peer
	 * @param authenticated the result: failure/success
	 */
	public void completed(String mechanism, String authPeer, boolean authenticated);
}
