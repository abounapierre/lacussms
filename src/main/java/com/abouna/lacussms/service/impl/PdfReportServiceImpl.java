package com.abouna.lacussms.service.impl;

import com.abouna.lacussms.config.PathConfigBean;
import com.abouna.lacussms.dto.RapportPdfModelDTO;
import com.abouna.lacussms.dto.RapportPdfModelGroupByAgenceDTO;
import com.abouna.lacussms.dto.ReportPDFAgenceInputDTO;
import com.abouna.lacussms.dto.ReportPDFInputDTO;
import com.abouna.lacussms.dto.ReportPDFPeriodeInputDTO;
import com.abouna.lacussms.pdf.PrintByAgenceReportPDF;
import com.abouna.lacussms.pdf.PrintPeriodeReportPDF;
import com.abouna.lacussms.pdf.PrintReportPDF;
import com.abouna.lacussms.service.PdfReportService;
import com.abouna.lacussms.service.RapportService;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.abouna.lacussms.pdf.PdfUtils.getPreviousMonth;
import static com.abouna.lacussms.pdf.PdfUtils.getPreviousYear;

@Service
public class PdfReportServiceImpl implements PdfReportService  {
    private final RapportService rapportService;

    public PdfReportServiceImpl(RapportService rapportService) {
        this.rapportService = rapportService;
    }

    @Override
    public byte[] generatePdfReportAsByteArray(int month, int year) throws Exception {
        try {
            List<RapportPdfModelDTO> dataModels = rapportService.getMessagesByMonthAndYear(month, year);
            if (dataModels.isEmpty()) {
                throw new Exception("No data found for the specified month and year.");
            }
            ReportPDFInputDTO reportPDFInputDTO = new ReportPDFInputDTO();
            reportPDFInputDTO.setMonth(String.valueOf(month));
            reportPDFInputDTO.setYear(String.valueOf(year));
            return new PrintReportPDF(reportPDFInputDTO).getBytes();
        } catch (Exception e) {
            throw new Exception("Error generating PDF report: " + e.getMessage(), e);
        }
    }

    @Override
    public String buildPdfReport() {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = getPreviousMonth(currentDate);
        int currentYear = getPreviousYear(currentDate);
        try {
            String path = PathConfigBean.getInstance().getRootPath() + "/reports" + "/rapport-" + currentMonth + "-" + currentYear + ".pdf";
            try (FileOutputStream outputStream = new FileOutputStream(path)) {
                outputStream.write(generatePdfReportAsByteArray(currentMonth, currentYear));
            }
            return path;
        } catch (Exception e) {
            throw new RuntimeException(" " + e.getMessage(), e);
        }
    }

    @Override
    public String buildPdfReport(Date dateDebut, Date dateFin, boolean masquer) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
            String path = PathConfigBean.getInstance().getRootPath() + "/reports" + "/rapport-" + simpleDateFormat.format(dateDebut) + "-" + simpleDateFormat.format(dateFin) + ".pdf";
            List<RapportPdfModelDTO> dataModels = rapportService.getMessagesByPeriode(dateDebut, dateFin);
            if (dataModels.isEmpty()) {
                throw new RuntimeException("No data found for the specified date  begin and date end.");
            }
            String formattedDate = "dd-MM-yyyy";
            ReportPDFPeriodeInputDTO reportPDFInputDTO = new ReportPDFPeriodeInputDTO();
            reportPDFInputDTO.setDateDebut(new SimpleDateFormat(formattedDate).format(dateDebut));
            reportPDFInputDTO.setDateFin(new SimpleDateFormat(formattedDate).format(dateFin));
            reportPDFInputDTO.setDataModels(dataModels);
            reportPDFInputDTO.setMask(masquer);
            reportPDFInputDTO.setEntreprise("LACUS");
            try (FileOutputStream outputStream = new FileOutputStream(path)) {
                outputStream.write(new PrintPeriodeReportPDF(reportPDFInputDTO).getBytes());
            }
            return  path;
        } catch (Exception e) {
            throw new RuntimeException("Erreur de generation du rapport " + e.getMessage(), e);
        }
    }

    @Override
    public String buildPdfReportByAgency(Date dateDebut, Date dateFin, boolean masquer) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
            String path = PathConfigBean.getInstance().getRootPath() + "/reports" + "/rapport-groupe-" + simpleDateFormat.format(dateDebut) + "-" + simpleDateFormat.format(dateFin) + ".pdf";
            List<RapportPdfModelGroupByAgenceDTO> dataModels = rapportService.getMessagesByPeriodeGroupByAgence(dateDebut, dateFin);
            if (dataModels.isEmpty()) {
                throw new RuntimeException("No data found for the specified date  begin and date end.");
            }
            String formattedDate = "dd-MM-yyyy";
            ReportPDFAgenceInputDTO reportPDFInputDTO = new ReportPDFAgenceInputDTO();
            reportPDFInputDTO.setDateDebut(new SimpleDateFormat(formattedDate).format(dateDebut));
            reportPDFInputDTO.setDateFin(new SimpleDateFormat(formattedDate).format(dateFin));
            reportPDFInputDTO.setMessages(dataModels);
            reportPDFInputDTO.setMask(masquer);
            reportPDFInputDTO.setEntreprise("LACUS");
            try (FileOutputStream outputStream = new FileOutputStream(path)) {
                outputStream.write(new PrintByAgenceReportPDF(reportPDFInputDTO).getBytes());
            }
            return  path;
        } catch (Exception e) {
            throw new RuntimeException("Erreur de generation du rapport " + e.getMessage(), e);
        }
    }
}
