package com.abouna.lacussms.service.impl;

import com.abouna.lacussms.config.PathConfigBean;
import com.abouna.lacussms.dto.RapportPdfModelDTO;
import com.abouna.lacussms.dto.ReportPDFInputDTO;
import com.abouna.lacussms.pdf.PrintReportPDF;
import com.abouna.lacussms.service.PdfReportService;
import com.abouna.lacussms.service.RapportService;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

import static com.abouna.lacussms.pdf.DateUtils.getPreviousMonth;
import static com.abouna.lacussms.pdf.DateUtils.getPreviousYear;

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
    public void buildPdfReport() {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = getPreviousMonth(currentDate);
        int currentYear = getPreviousYear(currentDate);
        try {
            String root = PathConfigBean.getInstance().getRootPath() + "/reports" + "/rapport-" + currentMonth + "-" + currentYear + ".pdf";
            try (FileOutputStream outputStream = new FileOutputStream(root)) {
                outputStream.write(PdfReportService.getInstance().generatePdfReportAsByteArray(currentMonth, currentYear));
            }
        } catch (Exception e) {
            throw new RuntimeException(" " + e.getMessage(), e);
        }
    }
}
