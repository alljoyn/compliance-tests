/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *     Project (AJOSP) Contributors and others.
 *     
 *     SPDX-License-Identifier: Apache-2.0
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *     
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *     
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
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