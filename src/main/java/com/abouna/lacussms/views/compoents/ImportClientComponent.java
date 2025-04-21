package com.abouna.lacussms.views.compoents;

import com.abouna.lacussms.dto.AccountCSVModel;
import com.abouna.lacussms.service.LacusImportDataService;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.BkCliPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportClientComponent extends JFileChooser {
    public ImportClientComponent(JPanel parent) {
        super();
        LacusSmsService service = LacusSmsService.getInstance();
        setFileSelectionMode(JFileChooser.FILES_ONLY);
        setAcceptAllFileFilterUsed(false);
        setMultiSelectionEnabled(false);
        setDialogTitle("Sélectionner le fichier à importer");
        setFileFilter(new FileNameExtensionFilter("Fichiers CSV", "csv"));
        int userSelection = showOpenDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                saveClients(this.getSelectedFile().getAbsoluteFile());
            } catch (Exception ex) {
                Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void saveClients(File absoluteFile) throws RuntimeException {
        try {
            LacusImportDataService service = LacusImportDataService.getInstance();
            List<AccountCSVModel> models = readFile(absoluteFile);
            service.importAccountData(models);
            JOptionPane.showMessageDialog(this, "Importation réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'importation : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<AccountCSVModel> readFile(File absoluteFile) {
        return null;
    }
}
