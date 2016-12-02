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
package com.at4wireless.alljoyn.localagent.view.custom;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.at4wireless.alljoyn.localagent.view.renderer.RowRenderer;

@SuppressWarnings("serial")
public class CustomTable extends JTable
{
	public CustomTable(TableModel tableModel)
	{
		super(tableModel);
		
		JTableHeader tableHeader = getTableHeader();
		tableHeader.setReorderingAllowed(false);
		tableHeader.setFont(tableHeader.getFont().deriveFont(Font.BOLD));
		tableHeader.setForeground(new Color(25, 78, 97));
		tableHeader.setBorder(new LineBorder(new Color(228, 233, 236), 1));
		tableHeader.setPreferredSize(new Dimension(getWidth(), 25));
		((DefaultTableCellRenderer) tableHeader.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		
		RowRenderer rowRenderer = new RowRenderer();
		rowRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		rowRenderer.setVerticalAlignment(SwingConstants.CENTER);
		
		setDefaultRenderer(Object.class, rowRenderer);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setGridColor(Color.WHITE);
		setIntercellSpacing(new Dimension(2, 2));
		setRowHeight(23);
	}
}