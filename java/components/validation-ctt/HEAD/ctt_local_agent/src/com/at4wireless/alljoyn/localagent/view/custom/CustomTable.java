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
