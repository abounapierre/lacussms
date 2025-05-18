package com.abouna.lacussms.views.main;

import org.jdesktop.swingx.JXLabel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 *
 * @author Abouna Pierre
 */
public class EmptyPanel extends JPanel{
    private final BufferedImage bgImage;
    
    public EmptyPanel(String logo){
        try {
            this.bgImage = ImageIO.read(Objects.requireNonNull(getClass().getResource(logo)));
            JXLabel label = new JXLabel("LACUS SMS");
            label.setFont(new Font("Bernard MT Condensed",Font.BOLD,65));
            label.setForeground(Color.BLACK);
            add(label);
        } catch (IOException ex) {
            throw  new RuntimeException("Error when building " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0,getWidth(),getHeight(),null);
    }
}