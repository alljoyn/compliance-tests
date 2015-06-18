/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.onboarding;

// TODO: Auto-generated Javadoc
/**
 * The Class WifiConfiguration.
 */
public class WifiConfiguration

{
	
	/**
	 * The Class KeyMgmt.
	 */
	public static class KeyMgmt
	{
		
		/**
		 * Instantiates a new key mgmt.
		 */
		KeyMgmt() { throw new RuntimeException("Stub!"); }
		
		/** The Constant NONE. */
		public static final int NONE = 0;
		
		/** The Constant WPA_PSK. */
		public static final int WPA_PSK = 1;
		
		/** The Constant WPA_EAP. */
		public static final int WPA_EAP = 2;
		
		/** The Constant IEEE8021X. */
		public static final int IEEE8021X = 3;
		
		/** The Constant varName. */
		public static final java.lang.String varName = "key_mgmt";
		
		/** The Constant strings. */
		public static final java.lang.String[] strings = null;
	}
	
	/**
	 * The Class Protocol.
	 */
	public static class Protocol
	{
		
		/**
		 * Instantiates a new protocol.
		 */
		Protocol() { throw new RuntimeException("Stub!"); }
		
		/** The Constant WPA. */
		public static final int WPA = 0;
		
		/** The Constant RSN. */
		public static final int RSN = 1;
		
		/** The Constant varName. */
		public static final java.lang.String varName = "proto";
		
		/** The Constant strings. */
		public static final java.lang.String[] strings = null;
	}
	
	/**
	 * The Class AuthAlgorithm.
	 */
	public static class AuthAlgorithm
	{
		
		/**
		 * Instantiates a new auth algorithm.
		 */
		AuthAlgorithm() { throw new RuntimeException("Stub!"); }
		
		/** The Constant OPEN. */
		public static final int OPEN = 0;
		
		/** The Constant SHARED. */
		public static final int SHARED = 1;
		
		/** The Constant LEAP. */
		public static final int LEAP = 2;
		
		/** The Constant varName. */
		public static final java.lang.String varName = "auth_alg";
		
		/** The Constant strings. */
		public static final java.lang.String[] strings = null;
	}
	
	/**
	 * The Class PairwiseCipher.
	 */
	public static class PairwiseCipher
	{
		
		/**
		 * Instantiates a new pairwise cipher.
		 */
		PairwiseCipher() { throw new RuntimeException("Stub!"); }
		
		/** The Constant NONE. */
		public static final int NONE = 0;
		
		/** The Constant TKIP. */
		public static final int TKIP = 1;
		
		/** The Constant CCMP. */
		public static final int CCMP = 2;
		
		/** The Constant varName. */
		public static final java.lang.String varName = "pairwise";
		
		/** The Constant strings. */
		public static final java.lang.String[] strings = null;
	}
	
	/**
	 * The Class GroupCipher.
	 */
	public static class GroupCipher
	{
		
		/**
		 * Instantiates a new group cipher.
		 */
		GroupCipher() { throw new RuntimeException("Stub!"); }
		
		/** The Constant WEP40. */
		public static final int WEP40 = 0;
		
		/** The Constant WEP104. */
		public static final int WEP104 = 1;
		
		/** The Constant TKIP. */
		public static final int TKIP = 2;
		
		/** The Constant CCMP. */
		public static final int CCMP = 3;
		
		/** The Constant varName. */
		public static final java.lang.String varName = "group";
		
		/** The Constant strings. */
		public static final java.lang.String[] strings = null;
	}
	
	/**
	 * The Class Status.
	 */
	public static class Status
	{
		
		/**
		 * Instantiates a new status.
		 */
		Status() { throw new RuntimeException("Stub!"); }
		
		/** The Constant CURRENT. */
		public static final int CURRENT = 0;
		
		/** The Constant DISABLED. */
		public static final int DISABLED = 1;
		
		/** The Constant ENABLED. */
		public static final int ENABLED = 2;
		
		/** The Constant strings. */
		public static final java.lang.String[] strings = null;
	}
	
	/**
	 * Instantiates a new wifi configuration.
	 */
	public  WifiConfiguration() { throw new RuntimeException("Stub!"); }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public  java.lang.String toString() { throw new RuntimeException("Stub!"); }
	
	/**
	 * Describe contents.
	 *
	 * @return the int
	 */
	public  int describeContents() { throw new RuntimeException("Stub!"); }

	/** The network id. */
	public int networkId;
	
	/** The status. */
	public int status;
	
	/** The ssid. */
	public java.lang.String SSID;
	
	/** The bssid. */
	public java.lang.String BSSID;
	
	/** The pre shared key. */
	public java.lang.String preSharedKey;
	
	/** The wep keys. */
	public java.lang.String[] wepKeys = null;
	
	/** The wep tx key index. */
	public int wepTxKeyIndex;
	
	/** The priority. */
	public int priority;
	
	/** The hidden ssid. */
	public boolean hiddenSSID;
	
	/** The allowed key management. */
	public java.util.BitSet allowedKeyManagement;
	
	/** The allowed protocols. */
	public java.util.BitSet allowedProtocols;
	
	/** The allowed auth algorithms. */
	public java.util.BitSet allowedAuthAlgorithms;
	
	/** The allowed pairwise ciphers. */
	public java.util.BitSet allowedPairwiseCiphers;
	
	/** The allowed group ciphers. */
	public java.util.BitSet allowedGroupCiphers;


}