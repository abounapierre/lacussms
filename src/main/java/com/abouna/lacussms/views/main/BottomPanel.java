/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views.main;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Administrateur
 */
public class BottomPanel extends JPanel{
    private static JLabel info = getInfo();
    
    public BottomPanel(String title) {
        JLabel signatureLabel = new JLabel(title);//Ing. ABOUNA P.E
        signatureLabel.setFont(new Font("",Font.ITALIC,8));
        signatureLabel.setForeground(Color.red);
        setBackground(new Color(166,202,240));
        setLayout(new BorderLayout(10,10));
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,10,10));
        rightPanel.add(signatureLabel);
        rightPanel.setBackground(new Color(166,202,240));
        leftPanel.setBackground(new Color(166,202,240));
        leftPanel.add(info);
        add(leftPanel,BorderLayout.BEFORE_LINE_BEGINS);
        add(rightPanel,BorderLayout.AFTER_LINE_ENDS);
    }

    public static JLabel getInfo() {
        info = new JLabel();
        info.setSize(150, 20);
        return info;
    }

    public static void setInfo(JLabel info) {
        BottomPanel.info = info;
    }
    
    public static void settextLabel(String s){
        info.setText(s);
        info.setFont(new Font("",Font.BOLD,14));
    }
    
    public static void settextLabel(String s,Color c){
        info.setText(s);
        info.setForeground(c);
        info.setFont(new Font("",Font.BOLD,14));
    }
}
