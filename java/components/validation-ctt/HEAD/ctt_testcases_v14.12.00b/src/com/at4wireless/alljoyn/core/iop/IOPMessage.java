/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.iop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;


/**
 * The Class IOPMessage.
 */
public class IOPMessage {

	/** The logger. */
	WindowsLoggerImpl logger=null;

	/** The response code 0->Yes, 1->No and -1->No answer selected . */
	int response=-1;

	/** The dialog used to show information. */
	JDialog dialog;

	/**
	 * Instantiates a new IOP message.
	 *
	 * @param logger the logger
	 */
	public IOPMessage(WindowsLoggerImpl logger) {
		this.logger=logger;
	}


	/**
	 * Show message.
	 *
	 * @param title the title
	 * @param msg the message
	 */
	public  void showMessage(String title,String msg) {
		if(!title.equals("Verdict")){
		logger.info(msg);
		}


		JTextPane textPane = new JTextPane();
		textPane.setText(msg);

		textPane.setSize(new Dimension(480, 150));
		textPane.setPreferredSize(new Dimension(480, 150));

		textPane.setEditable(false);
		JScrollPane scroll=new JScrollPane(textPane);


		dialog =new JDialog();
		Rectangle bounds = null ;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width=500;
		int height=200;
		bounds = new Rectangle((int) (dim.width/2)-width/2, 
				(int) (dim.height/2)-height/2,
				width, 
				height);
		dialog.setBounds(bounds);
		dialog.setTitle(title);
		dialog.add(scroll,BorderLayout.CENTER);
		dialog.setResizable(false);
		JButton buttonNext=new JButton("Next");
		buttonNext.setForeground(new Color(255, 255, 255));
		buttonNext.setBackground(new Color(68, 140, 178));
		buttonNext.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
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
	}


	/**
	 * Show question message.
	 *
	 * @param title the title
	 * @param msg the message
	 * @return the response: 0->Yes, 1->No, -1-> Not answer selected
	 */
	public  int showQuestion(String title, String msg) {
		if(!title.equals("Verdict")){
			logger.info(msg);
			}

		JTextPane textPane = new JTextPane();
		textPane.setText(msg);
		textPane.setEditable(false);
		textPane.setSize(new Dimension(480, 150));
		textPane.setPreferredSize(new Dimension(480, 150));
		JScrollPane scroll=new JScrollPane(textPane);
		dialog =new JDialog();
		Rectangle bounds = null ;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width=500;
		int height=200;
		bounds = new Rectangle((int) (dim.width/2)-width/2, 
				(int) (dim.height/2)-height/2,
				width, 
				height);
		dialog.setBounds(bounds);
		dialog.setTitle(title);
		dialog.add(scroll,BorderLayout.CENTER);
		dialog.setResizable(false);
		JButton buttonYes=new JButton("Yes");
		buttonYes.setForeground(new Color(255, 255, 255));
		buttonYes.setBackground(new Color(68, 140, 178));
		buttonYes.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				logger.info("response:Yes");
				response=0;
				dialog.dispose();
			}});


		JButton buttonNo=new JButton("No");

		buttonNo.setForeground(new Color(255, 255, 255));
		buttonNo.setBackground(new Color(68, 140, 178));
		buttonNo.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				logger.info("response:No");
				response=1;
				dialog.dispose();
			}});

		JPanel buttonPanel=new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.5, 0.5};
		gridBagLayout.rowWeights = new double[]{1.0};

		buttonPanel.setLayout(gridBagLayout);

		GridBagConstraints gbc_yes = new GridBagConstraints();
		gbc_yes.gridx = 0;
		gbc_yes.gridy = 0;
		gbc_yes.anchor=GridBagConstraints.EAST;
		buttonPanel.add(buttonYes,gbc_yes);		
		gbc_yes.insets=new Insets(20,0,20,0);
		GridBagConstraints gbc_no = new GridBagConstraints();

		gbc_no.gridx = 1;
		gbc_no.gridy = 0;
		gbc_no.anchor=GridBagConstraints.WEST;
		buttonPanel.add(buttonNo,gbc_no);
		dialog.add(buttonPanel,BorderLayout.SOUTH);
		dialog.setAlwaysOnTop(true); 
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		int resp=response;

		return resp;
	}

}
