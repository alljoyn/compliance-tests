package com.at4wireless.alljoyn.core.commons;


import org.alljoyn.ns.commons.GenericLogger;
import org.alljoyn.ns.commons.NativePlatform;

public class NativePlatformWindows implements  NativePlatform {

	/**
	 * Reference to logger
	 */
	protected GenericLogger logger; 
	
	/**
	 * Constructor
	 */
	public NativePlatformWindows() {
		createLogger();
	}

	/**
	 * Creates and set logger object
	 */
	protected  void createLogger() {
		logger=new GenericLoggerImp(); 
	}
	
	/**
	 * @see org.alljoyn.ns.commons.NativePlatform#getNativeLogger()
	 */
	@Override
	public GenericLogger getNativeLogger() {
		return logger;
	}

	/**
	 * @see org.alljoyn.ns.commons.NativePlatform#setNativeLogger(org.alljoyn.ns.commons.GenericLogger)
	 */
	@Override
	public void setNativeLogger(GenericLogger logger) {
		this.logger = logger;
	}
}