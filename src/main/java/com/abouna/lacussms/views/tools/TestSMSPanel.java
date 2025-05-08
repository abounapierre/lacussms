package com.abouna.lacussms.views.tools;

import com.abouna.lacussms.dto.SendResponseDTO;
import com.abouna.lacussms.sender.context.SenderContext;
import com.abouna.lacussms.views.utils.DialogUtils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;

public class TestSMSPanel extends JDialog {
    private final JTextField phoneText;
    private final JTextField messageText;
    public TestSMSPanel() {
        setTitle("TEST ENVOIE MESSAGE");
        setModal(true);
        setLayout(new BorderLayout(10, 10));
        JPanel haut = new JPanel();
        JLabel lbl;
        haut.add(lbl = new JLabel("<html><font color = #012345 > Formulaire d'envoie de SMS </font></html>"));
        lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
        add(BorderLayout.BEFORE_FIRST_LINE, haut);
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 110dlu:", ""));
        builder.setDefaultDialogBorder();
        builder.append("Téléphone", phoneText = new JTextField(50));
        builder.append("Message", messageText = new JTextField(50));

        JButton annulerBtn;
        JButton okBtn;
        JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Envoyer"), annulerBtn = new JButton("Fermer"));
        builder.append(buttonBar, builder.getColumnCount());
        add(BorderLayout.CENTER, builder.getPanel());

        okBtn.addActionListener(ae -> {
            if (phoneText.getText().isEmpty()) {
                JOptionPane.showMessageDialog(TestSMSPanel.this.getParent(), "Le numéro de telephone est obligatoire");
                return;
            }
            if (messageText.getText().isEmpty()) {
                JOptionPane.showMessageDialog(TestSMSPanel.this.getParent(), "Le message est obligatoire");
                return;
            }
            SendResponseDTO sendResponseDTO = SenderContext.getInstance().send(phoneText.getText(), messageText.getText());
            JOptionPane.showMessageDialog(TestSMSPanel.this, sendResponseDTO.getMessage());
        });

        annulerBtn.addActionListener(ae -> {
            TestSMSPanel.this.dispose();
        });
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void init() {;
        DialogUtils.initDialog(new TestSMSPanel(), null, 400, 200);
    }
}
