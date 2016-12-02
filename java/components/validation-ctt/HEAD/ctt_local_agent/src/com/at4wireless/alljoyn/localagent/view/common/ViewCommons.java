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
package com.at4wireless.alljoyn.localagent.view.common;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ViewCommons
{
	private static final Logger logger = LogManager.getLogger(ViewCommons.class.getName());
	
    public static Rectangle setCenteredRectangle(int rectangleWidth, int rectangleHeight)
    {
    	Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
		int xCenter = (screenDimensions.width - rectangleWidth)/2;
		int yCenter = (screenDimensions.height - rectangleHeight)/2;

		return new Rectangle(xCenter, yCenter, rectangleWidth, rectangleHeight);
    }
    
    public static Image readImageFromFile(String imagePath, String imageName)
	{
		Image image = null;
		
		try
		{
			image = ImageIO.read(new File(imagePath + File.separator + imageName));
		}
		catch (IOException e)
		{
			logger.warn(String.format("Resource %s not found", imageName));
		}
		
		return image;
	}
}