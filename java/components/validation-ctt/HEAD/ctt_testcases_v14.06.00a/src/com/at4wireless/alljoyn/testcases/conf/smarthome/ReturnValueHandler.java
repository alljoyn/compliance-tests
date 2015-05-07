package com.at4wireless.alljoyn.testcases.conf.smarthome;

import java.util.concurrent.CountDownLatch;

import org.alljoyn.bus.Variant;
import org.alljoyn.smarthome.centralizedmanagement.client.ReturnValueSignalHandler;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;


public class ReturnValueHandler extends ReturnValueSignalHandler{
	CountDownLatch countDownLatch;
	Boolean pass=true;
	private static final String TAG = "ReturnValueSignalHandler";
	private static final Logger logger = LoggerFactory.getLogger(TAG);

	
	
	
	
	public ReturnValueHandler(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
		}

	@Override
	public void ReturnValue(String methodName, String status, Variant value) {

		logger.debug("New owner: "  );
		
		countDownLatch.countDown();
		
	}

}
