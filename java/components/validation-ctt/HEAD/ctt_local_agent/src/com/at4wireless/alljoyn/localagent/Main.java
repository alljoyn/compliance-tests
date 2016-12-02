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
package com.at4wireless.alljoyn.localagent;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import com.at4wireless.alljoyn.localagent.controller.common.ControllerCommons;
import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.view.LoginWindow;
import com.at4wireless.alljoyn.localagent.view.ProjectsDetailsWindow;
import com.at4wireless.alljoyn.localagent.view.common.ViewCommons;

public class Main
{
	private static final Logger logger = LogManager.getLogger(Main.class.getName());
	public static final String CTT_LOCAL_AGENT_VERSION = "v2.2.0";
	public static final String VERSION_LABEL = "AllSeen Test Tool Local Agent " + CTT_LOCAL_AGENT_VERSION;
	public static final String POWERED_BY_LABEL = "Powered by AT4 wireless";
	private static final int WINDOW_WIDTH = 1024;
	private static final int WINDOW_HEIGHT = 500;
	
	private static final String ICON = "ic_AllSeen.png"; 
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				logger.info(String.format("Initializing %s...", VERSION_LABEL));
				
				try
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
				catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (InstantiationException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (UnsupportedLookAndFeelException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				JFrame frame = new JFrame("CTT Local Agent");
				frame.setIconImage(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, ICON));
				
				setLoggerLevel();
				
				LoginWindow loginWindow = new LoginWindow(frame, true);
				
				frame.setBounds(ViewCommons.setCenteredRectangle(WINDOW_WIDTH, WINDOW_HEIGHT));
				loginWindow.setVisible(true);
				
				ProjectsDetailsWindow projectWindow = new ProjectsDetailsWindow(loginWindow);
				
				frame.add(projectWindow);
				frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				frame.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						ControllerCommons.clean();
						System.exit(0);
					}
				});
				frame.setResizable(false);
				projectWindow.draw();
			}
		});
	}
	
	private static void setLoggerLevel()
	{
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
		
		logger.info(String.format("Setting logger level to: %s", ConfigParameter.LOG_LEVEL));
		loggerConfig.setLevel(Level.toLevel(ConfigParameter.LOG_LEVEL));
		ctx.updateLoggers();
	}
}