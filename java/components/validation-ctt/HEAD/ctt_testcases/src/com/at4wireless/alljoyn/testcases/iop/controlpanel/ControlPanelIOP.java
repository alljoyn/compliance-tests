/*
 *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.testcases.iop.controlpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.iop.IOPMessage;


/**
 * The Class ControlPanelIOP.
 */
public class ControlPanelIOP {

	
	 
 	/** The pass. */
 	Boolean pass=true;
 	
 	Boolean inconc=false;
	
	/** The tag. */
	protected  final String TAG = "ControlPIOPTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The message. */
	IOPMessage message=new IOPMessage(logger);
	
	 Map<String, List<String>> goldenUnits;

	
	 String name=null;
	 
	 Boolean ICSON_OnboardingServiceFramework=false;

	
	/**
	 * Instantiates a new control panel iop.
	 *
	 * @param testCase the test case
	 */
	public ControlPanelIOP(String testCase, Map<String, List<String>> goldenUnits, boolean iCSON_OnboardingServiceFramework) {
		ICSON_OnboardingServiceFramework=iCSON_OnboardingServiceFramework;
		this.goldenUnits=goldenUnits;
		try{
			runTestCase(testCase);
					}catch(Exception e){
			
			
				fail("Exception: "+e.toString());
			
		}
	}
	
	
	
	
	


	/**
	 * Run test case.
	 *
	 * @param testCase the test case
	 * @throws Exception the exception
	 */
	public  void runTestCase(String testCase) throws Exception{
		
		showPreconditions();		
		if(testCase.equals("IOP_ControlPanel-v1-01")){
			IOP_ControlPanel_v1_01();
		}else if(testCase.equals("IOP_ControlPanel-v1-02")){
			IOP_ControlPanel_v1_02();
		}else if(testCase.equals("IOP_ControlPanel-v1-03")){
			IOP_ControlPanel_v1_03(); 

		}else {
			fail("TestCase not valid");
		}
		//tearDown();
	}



	/**
	 * IOP control panel_v1_01.
	 */
	private  void IOP_ControlPanel_v1_01() {
		
		String testBed="";
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		message.showMessage("Test Procedure","Step 2) Switch on all Golden Units of the Test Bed.");
		
		
		String category="Category 3 AllJoyn Device (Onboarding)";
		String getGoldenUnitOnboarding = null;
		if(ICSON_OnboardingServiceFramework){
		 getGoldenUnitOnboarding=getGoldenUnitName(category);
		 if(getGoldenUnitOnboarding==null){

				fail("No "+category+" Golden Unit but "
						+ "ICSON_OnboardingServiceFramework is equals to true.");
				inconc=true;
				return;
				
			}
		}		
		
		if(ICSON_OnboardingServiceFramework){
			message.showMessage("Test Procedure","Step 3) Connect the DUT and "+testBed+" to the AP network if"
					+ " they are not connected yet, use "
					+ getGoldenUnitOnboarding+" to onboard the DUT to the personal AP.");
		}else{
		message.showMessage("Test Procedure","Step 3) Connect the DUT and "+testBed+" to the AP network if"
				+ " they are not connected yet.");
		}
		
		int step=4;
		for(int i=1;i<=2;i++){
				testBed="TBAD"+i;
				
				category="Category 4.1 AllJoyn Device (Control Panel Controller)";
				testBed=getGoldenUnitName(category);
				if(testBed==null){

					fail("No "+category+" Golden Unit.");
					inconc=true;
					return;
					
				}
				
				
				message.showMessage("Test Procedure","Step "+step+") Establish an AllJoyn "
						+ "connection between the DUT and "+testBed+".");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" (using its "
						+ "virtual control panel Controller functionality) to get DUT "
						+ "Control Panel elements on a graphical interface.");
				step++;
				message.showMessage("Test Procedure","Step "+step+") Using "+testBed+" navigate through "
						+ "the different menus available.");
				step++;
				
				int included=message.showQuestion("Pass/Fail Criteria","Verify that using "+testBed+" "
						+ "virtual control panel it is possible to navigate through all "
						+ "menus specified by DUT manufacturer.");

				if(included!=0){//1==NO

					fail(testBed+" virtual control panel it is not possible to navigate "
							+ "through all menus sepciefied by DUT manufacturer.");						
					return;}
		}
		
	}

	


	/**
	 * IOP control panel_v1_02.
	 */
	private  void IOP_ControlPanel_v1_02() {
		
		String testBed="TBAD1";
		String getGoldenUnitOnboarding=null;
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		
		if(ICSON_OnboardingServiceFramework){
			
			 String category = "Category 3 AllJoyn Device (Onboarding)";
				
			 getGoldenUnitOnboarding=getGoldenUnitName(category);
			 if(getGoldenUnitOnboarding==null){

					fail("No "+category+" Golden Unit but "
							+ "ICSON_OnboardingServiceFramework is equals to true.");
					inconc=true;
					return;
					
				}
			}	
		
		
		
		String category = "Category 4.1 AllJoyn Device (Control Panel Controller)";
		testBed=getGoldenUnitName(category);
		if(testBed==null){
			fail("No "+category+" Golden Unit.");
			inconc=true;
			return;
			
		}
		String TBAD1=testBed;
		
		message.showMessage("Test Procedure","Step 2) Switch on "+testBed+".");
		
		
		
		int step=3;
		for(int i=1;i<3;i++){
			
				
				step++;			
				
				
				if(ICSON_OnboardingServiceFramework){
					message.showMessage("Test Procedure","Step "+step+") Connect the DUT and "+testBed+" to the AP network if"
							+ " they are not connected yet, use "
							+ getGoldenUnitOnboarding+" to onboard the DUT to the personal AP.");
				}else{
				message.showMessage("Test Procedure","Step "+step+") Connect the DUT and "+testBed+" to the AP network if"
						+ " they are not connected yet.");
				}
				step++;
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" (using its "
						+ "virtual Control Panel Controller functionality) to get DUT "
						+ "Control Panel elements on a graphical interface.");
				step++;
				
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to navigate "
						+ "across the different DUT menu windows available in the virtual "
						+ "control panel and note the values of the parameters displayed "
						+ "in the menu windows.");
				step++;
				
				int resp=message.showQuestion("Pass/Fail Criteria","Verify that the values of "
						+ "the different parameters shown in the virtual control panel "
						+ "menu windows are the same that the real DUT device parameter "
						+ "values.");

				if(resp!=0){//1==NO

					fail("The values of the different parameters shown in the "
							+ "virtual control panel menu windows are not the same that the "
							+ "real DUT device parameter values.");						
					return;}
				
				
				 category = "Category 4.1 AllJoyn Device (Control Panel Controller)";
					testBed=getGoldenUnitName(category);
					if(testBed==null){
						fail("No "+category+" Golden Unit.");
						inconc=true;
						return;
						
					}
				
		}//for
		
		int resp=message.showQuestion("Pass/Fail Criteria","Verify that the values of the parameters"
				+ " obtained with the rest of the Golden Units are the same"
				+ " than the results obtained with "+TBAD1+".");

		if(resp!=0){//1==NO

			fail("The values of the parameters"
				+ " obtained with the rest of the Golden Units are not the same"
				+ " than the results obtained with "+TBAD1+".");						
			return;}
		
		
	}
	
	



	/**
	 * IOP control panel_v1_03.
	 */
	private  void IOP_ControlPanel_v1_03() {
		String testBed="TBAD1";
		String getGoldenUnitOnboarding=null;
		
		message.showMessage("Initial Conditions","DUT and Golden Units are switched off.");
		message.showMessage("Test Procedure","Step 1) Switch on DUT.");
		
		if(ICSON_OnboardingServiceFramework){
			
			 String category = "Category 3 AllJoyn Device (Onboarding)";
				
			 getGoldenUnitOnboarding=getGoldenUnitName(category);
			 if(getGoldenUnitOnboarding==null){

					fail("No "+category+" Golden Unit but "
							+ "ICSON_OnboardingServiceFramework is equals to true.");
					inconc=true;
					return;
					
				}
			}	
		
		
		String category = "Category 4.1 AllJoyn Device (Control Panel Controller)";
		testBed=getGoldenUnitName(category);
		if(testBed==null){
			fail("No "+category+" Golden Unit.");
			inconc=true;
			return;
			
		}
		String TBAD1=testBed;
		message.showMessage("Test Procedure","Step 2) Switch on "+testBed+".");
		
		
		
		int step=3;
		for(int i=1;i<3;i++){
				
				
				step++;
				if(ICSON_OnboardingServiceFramework){
					message.showMessage("Test Procedure","Step "+step+") Connect the DUT and "+testBed+" to the AP network if"
							+ " they are not connected yet, use "
							+ getGoldenUnitOnboarding+" to onboard the DUT to the personal AP.");
				}else{
				message.showMessage("Test Procedure","Step "+step+") Connect the DUT and "+testBed+" to the AP network if"
						+ " they are not connected yet.");
				}
				step++;
				
				message.showMessage("Test Procedure","Step "+step+") Establish an AllJoyn "
						+ "connection between the DUT and "+testBed+".");
				step++;
				
				message.showMessage("Test Procedure","Step "+step+") Use "+testBed+" control "
						+ "application to display DUT Control Panel elements.");
				step++;
				
				message.showMessage("Test Procedure","Step "+step+") Command "+testBed+" to navigate "
						+ "through the menu windows in the virtual DUT control panel and "
						+ "perform following steps for every element:");
				message.showMessage("Test Procedure","Step "+step+" a) Command "+testBed+" to modify the "
						+ "element value.");
				message.showMessage("Test Procedure","Step "+step+" b) Verify that the element "
						+ "value is modified in the virtual control panel.");
				message.showMessage("Test Procedure","Step "+step+" c) Verify at DUT that the "
						+ "value of the element is modified (by means provided by DUT "
						+ "user interface).");
				step++;
				int included=message.showQuestion("Pass/Fail Criteria","For every Control Panel "
						+ "DUT element, are the parameters modified in DUT using virtual "
						+ "Control Panel interface at "+testBed+" updated in DUT and in "
						+ "the Control Panel Interface at "+testBed+".");

				if(included!=0){//1==NO

					fail("For every Control Panel "
						+ "DUT element, the parameters modified in DUT using virtual "
						+ "Control Panel interface at "+testBed+" are not updated in "
						+ "DUT and in the Control Panel Interface at "+testBed+".");						
					return;}
				
				
				 category = "Category 4.1 AllJoyn Device (Control Panel Controller)";
				testBed=getGoldenUnitName(category);
				if(testBed==null){
					fail("No "+category+" Golden Unit.");
					inconc=true;
					return;
					
				}
		}
		
		
		int included=message.showQuestion("Pass/Fail Criteria","Verify that the results obtained with the rest of the Golden Units"
				+ " are the same than the results obtained with "+TBAD1+".");

		if(included!=0){//1==NO

			fail("The results obtained with the rest of the Golden Units"
				+ " are not the same than the results obtained with "+TBAD1+".");						
			return;}
	}



	/**
	 * Show preconditions.
	 */
	private  void showPreconditions() {
		
		String msg="Step 1) The passcode for the DUT is set to the default passcode \"000000\""
				+ "\nStep 2) The AllJoyn devices of the Test Bed used will register an AuthListener with the"
				+ " AllJoyn framework that provides the default passcode (“000000”)\n when "
				+ "authentication is requested (unless anything else is defined in a test case)."
				+ "\nStep 3) The SSID of the soft access point (Soft AP) advertised by the DUT follows the"
				+ " proper format such that it ends with the first seven digits of the deviceId."
				+ "\nStep 4) All devices are configured with their AllJoyn functionality enabled.";
		//If Audio service=> Add audio preconditions
		message.showMessage("Preconditions",msg);
		
		
		
		
	}
	
	

	
/**
 * Fail.
 *
 * @param msg the msg
 */
private  void fail(String msg) {

	message.showMessage("Verdict",msg);
	logger.error(msg);
	pass=false;


}
	
	
	
	/**
	 * Gets the verdict.
	 *
	 * @return the verdict
	 */
	public String getVerdict() {

		String verdict=null;
		if(inconc){
			verdict="INCONC";
		}else if(pass){
			verdict="PASS";
		}else if(!pass){
			verdict="FAIL";
		}


		return verdict;
	}
	
	
	
	private String getGoldenUnitName(final String Category) {
		name=null;

		final List<String> gu = goldenUnits.get(Category);
		if(gu!=null){
			if(gu!=null&&gu.size()>1){
				Object col[] = {"Golden Unit Name","Category"};

				TableModel model = new DefaultTableModel(col,gu.size());

				final JTable tableSample = new JTable(model){

					private static final long serialVersionUID = -5114222498322422255L;

					public boolean isCellEditable(int row, int column)
					{					
							return false;
								}
				};


				tableSample.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				tableSample.getTableHeader().setBackground(new Color(25, 78, 97));
				tableSample.getTableHeader().setForeground(new Color(255, 255, 255));
				tableSample.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 13));

				for (int i = 0; i < gu.size(); i++) {

					tableSample.setValueAt(gu.get(i),i,0);
					tableSample.setValueAt(Category,i,1);

				}




				JScrollPane scroll = new JScrollPane(tableSample);





				final JDialog dialog = new JDialog();
				Rectangle bounds = null ;
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				int width=500;
				int height=200;
				bounds = new Rectangle((int) (dim.width/2)-width/2, 
						(int) (dim.height/2)-height/2,
						width, 
						height);
				dialog.setBounds(bounds);
				dialog.setTitle("Select a Golden Unit");
				dialog.add(scroll,BorderLayout.CENTER);
				dialog.setResizable(false);
				JButton buttonNext=new JButton("Next");
				buttonNext.setForeground(new Color(255, 255, 255));
				buttonNext.setBackground(new Color(68, 140, 178));
				buttonNext.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						int selectedGU = tableSample.getSelectedRow();
						if(selectedGU!=-1){
							dialog.dispose();
							name="GU: "+gu.remove(selectedGU);
							//goldenUnits.put(Category, gu);
						}		
					}});

				JPanel buttonPanel=new JPanel();
				GridBagLayout gridBagLayout = new GridBagLayout();
				gridBagLayout.columnWeights = new double[]{1.0};
				gridBagLayout.rowWeights = new double[]{1.0};
				buttonPanel.setLayout(gridBagLayout);
				GridBagConstraints gbc_next = new GridBagConstraints();
				gbc_next.gridx = 0;
				gbc_next.gridy = 0;
				gbc_next.anchor=GridBagConstraints.CENTER;
				buttonPanel.add(buttonNext,gbc_next);	
				dialog.add(buttonPanel,BorderLayout.SOUTH);
				dialog.setAlwaysOnTop(true); //<-- this line
				dialog.setModal(true);
				dialog.setResizable(false);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);

			}else if(gu.size()==1){
				name="GU: "+gu.remove(0);

			}
		}

		return name;
	}
	

}