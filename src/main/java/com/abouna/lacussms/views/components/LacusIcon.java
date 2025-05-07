package com.abouna.lacussms.views.components;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class LacusIcon extends javax.swing.ImageIcon {
    public LacusIcon(String path) {
        super();
        java.awt.Image ajouImg;
        try {
            ajouImg = ImageIO.read(Objects.requireNonNull(getClass().getResource(path)));
            setImage(ajouImg);
        } catch (IOException e) {
            setImage(null);
        }
    }
}
