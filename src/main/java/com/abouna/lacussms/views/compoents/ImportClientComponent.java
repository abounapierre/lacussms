package com.abouna.lacussms.views.compoents;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.dto.ClientCSV;
import com.abouna.lacussms.dto.ResultImportDataModelDTO;
import com.abouna.lacussms.service.LacusImportDataService;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.MainFrame;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static com.abouna.lacussms.views.tools.ConstantUtils.HEADER;

public class ImportClientComponent extends JFileChooser {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ImportClientComponent.class);

    public ImportClientComponent(JPanel parent) {
        super();
        LacusSmsService service = LacusSmsService.getInstance();
        MainFrame mainFrame = ApplicationConfig.getApplicationContext().getBean(MainFrame.class);
        setFileSelectionMode(JFileChooser.FILES_ONLY);
        setAcceptAllFileFilterUsed(false);
        setMultiSelectionEnabled(false);
        setDialogTitle("Sélectionner le fichier à importer");
        setFileFilter(new FileNameExtensionFilter("Fichiers CSV", "csv"));
        int userSelection = showOpenDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                List<ClientCSV> result = readFile(this.getSelectedFile().getAbsoluteFile());
                new ShowImportClientDialog(mainFrame, result);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'importation : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveClients(File absoluteFile) throws RuntimeException {
        try {
            LacusImportDataService service = LacusImportDataService.getInstance();
            List<ClientCSV> models = readFile(absoluteFile);
            ResultImportDataModelDTO resultImportDataModelDTO = service.importAccountData(models);
            log.info("Importation réussie : {} lignes importées avec succès.", resultImportDataModelDTO.getErrors().size());
            JOptionPane.showMessageDialog(this, "Importation réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'importation : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<ClientCSV> readFile(File absoluteFile) {
        List<ClientCSV> clients = new ArrayList<>();
        try(Reader reader = new InputStreamReader(Files.newInputStream(absoluteFile.toPath()));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.newFormat(';').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            int row = 1;
            for(CSVRecord csvRecord : csvParser) {
                ClientCSV client = new ClientCSV(
                        row,
                        csvRecord.get(HEADER[0]),
                        csvRecord.get(HEADER[1]),
                        csvRecord.get(HEADER[2]),
                        csvRecord.get(HEADER[3]),
                        csvRecord.get(HEADER[4]),
                        csvRecord.get(HEADER[5]),
                        csvRecord.get(HEADER[6])
                );
                clients.add(client);
                row++;
            }
            return clients;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
