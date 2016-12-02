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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class RowRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 6703872492730589499L;
	
	public RowRenderer()
	{
		
	}

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {	
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
        if (isSelected)
        {
        	cellComponent.setBackground(new Color(165, 218, 237));
        }
        else
        {
	        if (row % 2 != 0)
	        {
	        	cellComponent.setBackground(new Color(232, 249, 255));
	        }
	        else
	        {
	        	cellComponent.setBackground(new Color(235, 237, 234));
	        }
        }
        
        cellComponent.setForeground(new Color(86, 86, 86));
        
        if (table.getColumnName(column).equals("Final Verdict") || table.getColumnName(column).equals("Last Verdict"))
        {
        	cellComponent.setFont(table.getFont().deriveFont(Font.BOLD));
        	
        	if ("INCONC".equals(table.getModel().getValueAt(table.convertRowIndexToModel(row), column)))
    		{
    			cellComponent.setForeground(new Color(168, 161, 0));
    		}
    		else if ("PASS".equals(table.getModel().getValueAt(table.convertRowIndexToModel(row), column)))
    		{
    			cellComponent.setForeground(new Color(103, 154, 0));
    		}
    		else if ("FAIL".equals(table.getModel().getValueAt(table.convertRowIndexToModel(row), column)))
    		{
    			cellComponent.setForeground(new Color(217, 61, 26));
    		}
    		else
    		{
    			cellComponent.setForeground(new Color(0, 0, 0));
    		}
        }
        
		return cellComponent;
    }
}