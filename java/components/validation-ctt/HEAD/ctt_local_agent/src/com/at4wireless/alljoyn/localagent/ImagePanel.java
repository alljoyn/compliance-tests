package com.at4wireless.alljoyn.localagent;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

class ImagePanel extends JComponent {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6618211256550731363L;
	private Image image;
    public ImagePanel(String imagePath) {
    	Image myImage = null;
		try {
			myImage = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.image = myImage;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}
