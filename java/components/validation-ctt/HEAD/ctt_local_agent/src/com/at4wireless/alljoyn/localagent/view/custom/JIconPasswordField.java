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

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class JIconPasswordField extends JPasswordField
{
    private Icon icon;
    private Insets jPasswordFieldInsets;
 
    public JIconPasswordField()
    {
        super();
        this.icon = null;
 
        Border border = UIManager.getBorder("TextField.border");
        JPasswordField jPasswordField = new JPasswordField();
        this.jPasswordFieldInsets = border.getBorderInsets(jPasswordField);
    }
 
    public void setIcon(Icon icon)
    {
        this.icon = icon;
    }
 
    public Icon getIcon()
    {
        return this.icon;
    }
 
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
 
        int textX = 2;
 
        if (this.icon != null)
        {
            int iconWidth = icon.getIconWidth();
            int iconHeight = icon.getIconHeight();
            int x = jPasswordFieldInsets.left + 5;//this is our icon's x
            textX = x + iconWidth + 2; //this is the x where text should start
            int y = (this.getHeight() - iconHeight)/2;
            icon.paintIcon(this, g, x, y);
        }
 
        setMargin(new Insets(2, textX, 2, 2));
    }
}