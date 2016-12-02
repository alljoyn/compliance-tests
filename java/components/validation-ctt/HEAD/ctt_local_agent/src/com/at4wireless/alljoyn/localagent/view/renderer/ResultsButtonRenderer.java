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
package com.at4wireless.alljoyn.localagent.view.renderer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.view.common.ViewCommons;

@SuppressWarnings("serial")
public class ResultsButtonRenderer extends JButton implements TableCellRenderer
{
	private static final String RESULTS_BUTTON = "results.jpg";

	public ResultsButtonRenderer()
	{
		setOpaque(true);
		setBorderPainted(false); 
		setContentAreaFilled(false); 
		setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, RESULTS_BUTTON)));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		if ((Boolean) value)
		{
			setEnabled(true);
		}
		else
		{
			setEnabled(false);
		}
		
		return this;
	}
}