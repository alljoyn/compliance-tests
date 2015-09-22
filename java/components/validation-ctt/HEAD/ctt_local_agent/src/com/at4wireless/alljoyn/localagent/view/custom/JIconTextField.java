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

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class JIconTextField extends JTextField
{
    private Icon icon;
    private Insets jTextFieldInsets;
 
    public JIconTextField()
    {
        super();
        this.icon = null;
 
        Border border = UIManager.getBorder("TextField.border");
        JTextField jTextField = new JTextField();
        this.jTextFieldInsets = border.getBorderInsets(jTextField);
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
            int x = jTextFieldInsets.left + 5;//this is our icon's x
            textX = x + iconWidth + 2; //this is the x where text should start
            int y = (this.getHeight() - iconHeight)/2;
            icon.paintIcon(this, g, x, y);
        }
 
        setMargin(new Insets(2, textX, 2, 2));
    }
}
