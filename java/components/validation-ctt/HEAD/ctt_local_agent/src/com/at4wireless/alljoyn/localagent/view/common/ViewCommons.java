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
