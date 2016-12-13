/*******************************************************************************
 *  Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright 2016 Open Connectivity Foundation and Contributors to
 *     AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.localagent.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.at4wireless.alljoyn.localagent.model.UpdateTestCasesModel;
import com.at4wireless.alljoyn.localagent.model.common.ModelCommons;

public class UpdateTestCasesController
{
	private static final Logger logger = LogManager.getLogger(UpdateTestCasesController.class.getName());
	
	private static final String downloadPath = (new File("")).getAbsolutePath() + File.separator
			+ ModelCommons.getConfigValue("TestCasesPackagePath");
	private static final String jarPath = "jar:file:/" + new File("").getAbsolutePath() + "/"
			+ ModelCommons.getConfigValue("TestCasesPackagePath");
	
	private static final String exeName = "c_test.exe";
	private static final String dllName = "alljoyn_java.dll";
	private static final String xmlsPath = "introspection-xml";
	
	private UpdateTestCasesModel updateTestCasesModel;
	
	public UpdateTestCasesController()
	{
		updateTestCasesModel = new UpdateTestCasesModel();
	}
	
	public void downloadUpdate(String sessionToken, String fullPackageVersion) throws URISyntaxException, IOException
	{
		HttpResponse testToolUpdateResponse = updateTestCasesModel.getUpdate(sessionToken, fullPackageVersion);
		String fileName = "TestCases_Package_" + fullPackageVersion + ".jar";	
		BufferedHttpEntity bufferedUpdate = new BufferedHttpEntity(testToolUpdateResponse.getEntity());
		FileOutputStream updateLocation = null;
		
		logger.debug(String.format("Download path: %s. File saved: %s", downloadPath, fileName));
		
		try
		{
			updateLocation = new FileOutputStream(downloadPath + fileName);
		}
		catch (FileNotFoundException e)
		{
			logger.warn(String.format("Directory %s does not exist. Creating...", downloadPath));
			
			File directoryToBeCreated = new File(downloadPath);
			if (directoryToBeCreated.mkdirs())
			{	
				updateLocation = new FileOutputStream(downloadPath + fileName);
			}
		}
		
		bufferedUpdate.writeTo(updateLocation);
		
		extractDllFromJar(fullPackageVersion);
		extractXmlsFromJar(fullPackageVersion);
		extractExeFromJar(fullPackageVersion);
	}
	
	private void extractDllFromJar(String fullPackageVersion) throws MalformedURLException, IOException
	{
		String packageName = "TestCases_Package_" + fullPackageVersion + ".jar";
		String inputFile = "jar:file:/" + new File("").getAbsolutePath() + "/" + ModelCommons.getConfigValue("TestCasesPackagePath")
				+ packageName + "!/" + dllName;
		String pathToSaveDll = new File("").getAbsolutePath() + File.separator + "lib" + File.separator + fullPackageVersion.split("_")[0] + File.separator;
		
		extractFileFromJarToPath(inputFile, pathToSaveDll, dllName);
	}
	
	private void extractXmlsFromJar(String fullPackageVersion)
	{
		String packageName = "TestCases_Package_" + fullPackageVersion + ".jar";
		String inputPath = "jar:file:/" + new File("").getAbsolutePath() + "/" + ModelCommons.getConfigValue("TestCasesPackagePath")
				+ packageName + "!/" + xmlsPath;
		String pathToSaveXmls = new File("").getAbsolutePath() + File.separator + "lib"
				+ File.separator + fullPackageVersion.split("_")[0] + File.separator + xmlsPath;
		
		copyJarResourceToFolder(inputPath, pathToSaveXmls);
	}
	
	private void extractExeFromJar(String fullPackageVersion) throws MalformedURLException, IOException
	{
		String packageName = "TestCases_Package_" + fullPackageVersion;
		String inputFile = jarPath + packageName + ".jar!/" + exeName;
		
		if (fileExistsInsideJar(downloadPath + packageName + ".jar", exeName))
		{
			extractFileFromJarToPath(inputFile, downloadPath, packageName + ".exe");
		}
	}
	
	private boolean fileExistsInsideJar(String jarFileName, String jarEntryName)
	{
		boolean jarEntryExists = false;
		JarFile jarFile;
		JarEntry jarEntry;
		
		try
		{
			jarFile = new JarFile(jarFileName);
			jarEntry = jarFile.getJarEntry(jarEntryName);
			jarEntryExists = jarEntry != null;
			
			jarFile.close();
		}
		catch (IOException e)
		{
			logger.error(String.format("%s does not exist", jarFileName));
		}
		
		return jarEntryExists;
	}
	
	private void extractFileFromJarToPath(String pathOfFileToExtract, String outputPath, String outputFileName)
			throws MalformedURLException, IOException
	{
		File dir = new File(outputPath);
		
		logger.debug(String.format("Path to save file: %s", outputPath));
		
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		
		InputStream inputStream = ((JarURLConnection) new URL(pathOfFileToExtract).openConnection()).getInputStream(); 
		FileOutputStream outputStream = new FileOutputStream(new File(outputPath + outputFileName));
 
		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = inputStream.read(bytes)) != -1)
		{
			outputStream.write(bytes, 0, read);
		}
		
		inputStream.close();
		outputStream.close();
	}
	
	 /**
	 * This method will copy resources from the jar file of the current thread and extract it to the destination folder.
	 * 
	 * @param jarConnection
	 * @param destDir
	 * @throws IOException
	 */
	public void copyJarResourceToFolder(String inputPath, String destDir)
	{
	    try
	    {
	    	JarURLConnection jarConnection = ((JarURLConnection) new URL(inputPath).openConnection());
	        JarFile jarFile = jarConnection.getJarFile();

	        /**
	         * Iterate all entries in the jar file.
	         */
	        for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();)
	        {
	            JarEntry jarEntry = e.nextElement();
	            String jarEntryName = jarEntry.getName();
	            String jarConnectionEntryName = jarConnection.getEntryName();

	            /**
	             * Extract files only if they match the path.
	             */
	            if (jarEntryName.startsWith(jarConnectionEntryName))
	            {
	                String filename = jarEntryName.startsWith(jarConnectionEntryName)
	                		? jarEntryName.substring(jarConnectionEntryName.length()) : jarEntryName;
	                File currentFile = new File(destDir, filename);

	                if (jarEntry.isDirectory())
	                {
	                    currentFile.mkdirs();
	                }
	                else
	                {
	                    InputStream is = jarFile.getInputStream(jarEntry);
	                    OutputStream out = new FileOutputStream(currentFile);
	                    
	                    int read = 0;
	            		byte[] bytes = new byte[1024];
	            		try
	            		{
	            			while ((read = is.read(bytes)) != -1)
	            			{
	            				out.write(bytes, 0, read);
	            			}
	            		}
	            		catch (IOException e1)
	            		{
	            			// TODO Auto-generated catch block
	            			e1.printStackTrace();
	            		}
	            		finally
	            		{
	            			try
	            			{
	            				is.close();
	            				out.close();
	            			}
	            			catch (IOException e1)
	            			{
	            				// TODO Auto-generated catch block
	            				e1.printStackTrace();
	            			}
	            		}
	                }
	            }
	        }
	    }
	    catch (IOException e)
	    {
	        // TODO add logger
	        e.printStackTrace();
	    }

	}
}