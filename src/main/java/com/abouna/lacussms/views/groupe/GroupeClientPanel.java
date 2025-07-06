package com.abouna.lacussms.views.groupe;

import javax.swing.*;
import java.awt.*;

public class GroupeClientPanel extends JPanel{
    private final TitlePanel titlePanel;
    private final ContentPanel contentPanel;

    public GroupeClientPanel() {
        setLayout(new BorderLayout());
        titlePanel = new TitlePanel("Groupe de clients");
        contentPanel = new ContentPanel(new Object[]{"Code client", "Noms et prénoms", "N° compte", "Action"});
        add(BorderLayout.BEFORE_FIRST_LINE, titlePanel);
        add(BorderLayout.CENTER, contentPanel);
    }


    public TitlePanel getTitlePanel() {
        return titlePanel;
    }

    public ContentPanel getContentPanel() {
        return contentPanel;
    }
}
