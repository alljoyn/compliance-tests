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
package com.at4wireless.allseen.localagent.view;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.xml.sax.SAXException;

import com.at4wireless.allseen.localagent.controller.TestCaseController;
import com.at4wireless.allseen.localagent.model.common.ConfigParameter;
import com.at4wireless.allseen.localagent.view.common.ViewCommons;
import com.at4wireless.allseen.localagent.view.custom.CustomTable;
import com.at4wireless.allseen.localagent.view.renderer.RunButtonRenderer;

public class TestCasesDetailsWindow
{
	private static final Logger logger = LogManager.getLogger(TestCasesDetailsWindow.class.getName());
	private static final Object testCasesTableColumns[] = {"Test Case", "Description", "Action",
		"Last Verdict", "Date & Time"};
	private static final String RUN_ALL_BUTTON = "run_all.jpg";
	private static final String BACK_BUTTON = "back.jpg";
	
	private ProjectsDetailsWindow projectWindow;
	private LoginWindow loginWindow;
	private TestCaseController testCaseController;
	private int projectId;
	private String certificationRelease;
	
	public TestCasesDetailsWindow(ProjectsDetailsWindow projectWindow, LoginWindow loginWindow)
	{
		this.projectWindow = projectWindow;
		this.loginWindow = loginWindow;
		this.testCaseController = new TestCaseController();
	}
	
	public void drawTestCasesTable(int projectId, String certificationRelease)
	{	
		this.projectId = projectId;
		this.certificationRelease = certificationRelease;
		
		Object[][] testCasesTableContent = null;
		
		logger.info("Retrieving selected project information from Web Server.");
		
		try
		{
			testCasesTableContent = testCaseController.getTestCasesListFromServer(loginWindow.getAuthenticatedUser(), 
					loginWindow.getSessionToken(), projectId);
		}
		catch (URISyntaxException e)
		{
			logger.warn("Malformed Url");
			JOptionPane.showMessageDialog(projectWindow, "Wrong Url. Please check that your configuration file is correct");
		}
		catch (IOException e)
		{
			logger.warn("Communication with server failed");
			JOptionPane.showMessageDialog(projectWindow, "Communication with server failed.");
		}
		catch (JSONException e)
		{
			logger.warn("No selected Test Cases");
			JOptionPane.showMessageDialog(projectWindow, "This project has no associated Test Cases, please check its configuration.");
		}
		
		JScrollPane testCasesScrollPane = (JScrollPane) projectWindow.getComponent(1);
		
		TableModel noEditableCellsExceptRunModel = new DefaultTableModel(testCasesTableColumns, testCasesTableContent.length)
		{
			private static final long serialVersionUID = -5114222498322422255L;

			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		
		CustomTable testCasesTable = new CustomTable(noEditableCellsExceptRunModel);
		
		for (int i = 0; i < testCasesTableContent.length; i++)
		{
			testCasesTable.setValueAt(testCasesTableContent[i][0], i, 0);
			testCasesTable.setValueAt(testCasesTableContent[i][1], i, 1);
			testCasesTable.setValueAt(testCasesTableContent[i][2], i, 3);
			testCasesTable.setValueAt(testCasesTableContent[i][3], i, 4);
		}
		
		testCasesTable.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent event)
			{
				JTable target = (JTable) event.getSource();
				int column = target.getSelectedColumn();
				
				if (column == 2)
				{	
					SampleSelectionWindow sampleSelectionWindow = drawSampleSelectionWindow();
					
					if (((sampleSelectionWindow != null) && (!sampleSelectionWindow.isCancelled())) || sampleSelectionWindow == null)
					{
						String selectedTestCase = (String) target.getValueAt(target.getSelectedRow(), 0);
						
						logger.info(String.format("Running testCase %s", selectedTestCase));
						
						ExecuteTestCaseWindow executeTestCaseWindow = new ExecuteTestCaseWindow((JFrame) SwingUtilities.getWindowAncestor(projectWindow), 
								TestCasesDetailsWindow.this, loginWindow, certificationRelease, projectId, selectedTestCase, false);
						executeTestCaseWindow.setVisible(true);
					}
				}
			}
		});
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(testCasesTable.getModel());
		sorter.setSortable(2, false);
		
		testCasesTable.setRowSorter(sorter);
		testCasesTable.setBorder(null);
		
		TableColumn actionColumn = testCasesTable.getColumn("Action");
		actionColumn.setCellRenderer(new RunButtonRenderer());
		actionColumn.setMaxWidth(85);
		actionColumn.setMinWidth(85);
		
		testCasesScrollPane.setViewportView(testCasesTable);
	}
	
	private SampleSelectionWindow drawSampleSelectionWindow()
	{
		Object[][] samplesTableContent = null;
		
		try
		{
			samplesTableContent = testCaseController.getSamplesFromFile();
		}
		catch (SAXException | IOException | ParserConfigurationException e)
		{
			logger.warn("Cannot open the project details file");
			JOptionPane.showMessageDialog(projectWindow, "A problem occurred getting samples information."
					+ "The Test Case will be executed with the first setted sample");
		}
		
		SampleSelectionWindow sampleSelectionWindow = null;
		if ((samplesTableContent != null) && (samplesTableContent.length > 1))
		{
			sampleSelectionWindow = new SampleSelectionWindow((JFrame) SwingUtilities.getWindowAncestor(projectWindow), 
					testCaseController, samplesTableContent);
			sampleSelectionWindow.setVisible(true);
		}
		
		return sampleSelectionWindow;
	}
	
	public void drawBackButton()
	{
		JButton backButton = (JButton) projectWindow.getComponent(2);
		backButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, BACK_BUTTON)));
	
		backButton.removeActionListener(backButton.getActionListeners()[0]);
    	backButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				projectWindow.drawProjectsTable();
				projectWindow.drawNextButton();
				projectWindow.drawRefreshButton();
			}
    	});
    	backButton.setBounds(new Rectangle(517, 411, 83, 23));
	}
	
	public void drawRunAllButton()
	{
		JButton runAllButton = (JButton) projectWindow.getComponent(3);
		runAllButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, RUN_ALL_BUTTON)));

		runAllButton.removeActionListener(runAllButton.getActionListeners()[0]);
    	runAllButton.setBounds(new Rectangle(424, 411, 83, 23));
    	runAllButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JTable target = (JTable) ((JScrollPane) projectWindow.getComponent(1)).getViewport().getView();		
				SampleSelectionWindow sampleSelectionWindow = drawSampleSelectionWindow();
				
				if (((sampleSelectionWindow != null) && (!sampleSelectionWindow.isCancelled())) || sampleSelectionWindow == null)
				{
					logger.info("Running all available Test Cases");
					
					for (int i = 0; i < target.getRowCount(); i++)
					{
						ExecuteTestCaseWindow executeTestCaseWindow = new ExecuteTestCaseWindow((JFrame) SwingUtilities.getWindowAncestor(projectWindow), 
								TestCasesDetailsWindow.this, loginWindow, certificationRelease, projectId, (String) target.getValueAt(i, 0), true);
						executeTestCaseWindow.setVisible(true);
						
						if (executeTestCaseWindow.isStopped())
						{
							break;
						}
					}
				}
			}
    	});
	}
	
	public void updateVerdictAndDatetime(String testCaseName, String verdict, String dateTime)
	{
		JTable tableToUpdate = (JTable) ((JScrollPane) projectWindow.getComponent(1)).getViewport().getView();
		
		for (int i = 0; i < tableToUpdate.getRowCount(); i++)
		{
			if (tableToUpdate.getValueAt(i, 0).equals(testCaseName))
			{
				tableToUpdate.setValueAt(verdict, i, 3);
				tableToUpdate.setValueAt(dateTime, i, 4);
			}
		}
	}
	
	public TestCaseController getTestCaseController()
	{
		return testCaseController;
	}
}