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
package com.at4wireless.alljoyn.localagent.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.at4wireless.alljoyn.localagent.model.UpdateTestCasesModel;
import com.at4wireless.alljoyn.localagent.model.common.ModelCommons;

public class UpdateTestCasesController
{
	private static final Logger logger = LogManager.getLogger(UpdateTestCasesController.class.getName());
	
	private UpdateTestCasesModel updateTestCasesModel;
	
	public UpdateTestCasesController()
	{
		updateTestCasesModel = new UpdateTestCasesModel();
	}
	
	public void downloadUpdate(String sessionToken, String fullPackageVersion) throws URISyntaxException, IOException
	{
		HttpResponse testToolUpdateResponse = updateTestCasesModel.getUpdate(sessionToken, fullPackageVersion);
		String downloadPath = (new File("")).getAbsolutePath() + File.separator + ModelCommons.getConfigValue("TestCasesPackagePath");
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
	}
	
	private void extractDllFromJar(String fullPackageVersion)
	{
		String packageName = "TestCases_Package_" + fullPackageVersion + ".jar";
		String inputFile = "jar:file:/" + new File("").getAbsolutePath() + "/" + ModelCommons.getConfigValue("TestCasesPackagePath")
				+ packageName + "!/alljoyn_java.dll";
		String pathToSaveDll = new File("").getAbsolutePath() + File.separator + "lib" + File.separator + fullPackageVersion.split("_")[0] + File.separator;
		
		File dir = new File(pathToSaveDll);
		
		logger.debug(String.format("Path to save Dll: %s", pathToSaveDll));
		
		if (!dir.exists())
		{
			dir.mkdirs();
		}

		InputStream inputStream = null;
		try
		{
			inputStream = ((JarURLConnection) new URL(inputFile).openConnection()).getInputStream();
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		FileOutputStream outputStream = null;
		try
		{
			outputStream = new FileOutputStream(new File(pathToSaveDll + "alljoyn_java.dll"));
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		int read = 0;
		byte[] bytes = new byte[1024];
		try
		{
			while ((read = inputStream.read(bytes)) != -1)
			{
				outputStream.write(bytes, 0, read);
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				outputStream.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
