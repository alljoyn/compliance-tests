/**
 *Author: AT4 wireless
 *Date: 2015/02/12
 *Version: 1
 *Description: It's use to load only once the library, because if it's load twice 
 *it throws an Error
 */
package com.at4wireless.alljoyn;

import java.io.File;

public class Loader {
	
	private static final String LIB_BIN = "lib14.12.00/";
	private final static String ALLJOIN_JAVA="alljoyn_java";
	static {
	
			loadLib(LIB_BIN, ALLJOIN_JAVA);
			
			
		

	}
 
	
	
	private static void loadLib(String path, String name) {
		name = name + ".dll";
		System.out.println("Loading dll: "+LIB_BIN + name);
		
			// have to use a stream
			File dll = new File(LIB_BIN + name);
			
			
			
			
			try {
			
			
			System.load(dll.getAbsolutePath());
			System.out.println("Loaded correctly");

			} catch (UnsatisfiedLinkError e) {
				System.out.println("alljoyn_java.dll was not loaded correctly.");
				System.out.println("You need Visual Studio 2013 redistributable.");
				
				
			
			}
	}
	
	
	 @Override
	    public void finalize() {
		 
	 }
} 
