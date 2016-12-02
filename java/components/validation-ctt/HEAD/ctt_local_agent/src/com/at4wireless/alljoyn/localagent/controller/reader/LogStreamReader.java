/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package com.at4wireless.alljoyn.localagent.controller.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LogStreamReader implements Runnable
{
	private BufferedReader reader;

	public LogStreamReader(InputStream is)
	{
		this.reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
	}

	public void run()
	{
		try
		{
			String line;
			
			while ((line = reader.readLine()) != null)
			{					
				System.out.println(line);
			}
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}