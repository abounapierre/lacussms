/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Administrateur
 */
public class ContactPanel extends JDialog{

    public ContactPanel() {
        JTextArea aireText = new JTextArea(10, 30);
     aireText.setFont(new Font("Comic Sans MS", 1, 14));
     aireText.setForeground(Color.BLUE);
     aireText.setText("Cette Application été développée sous"
             + "licence GNU elle ne peut dont faire l'objet de commerce"
             + "elle a été développée par l'ingénieur "
             + "ABOUNA Pierre Emmanuel copy right 2017."
             + "Tél:652445015/698984176"
             + "Email: abouna.emmanuel@yahoo.fr,"
             + "eabouna@gmail.com");
     aireText.setLineWrap(true);
     aireText.setWrapStyleWord(true);
     add(aireText);
    }
    
    
}
