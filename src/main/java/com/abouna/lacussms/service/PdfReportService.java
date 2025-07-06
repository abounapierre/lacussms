package com.abouna.lacussms.service;

import com.abouna.lacussms.config.ApplicationConfig;

import java.util.Date;

public interface PdfReportService {
    byte[] generatePdfReportAsByteArray(int month, int year) throws Exception;
    String buildPdfReport() throws Exception;
    static PdfReportService getInstance() { return ApplicationConfig.getApplicationContext().getBean(PdfReportService.class); }
    String buildPdfReport(Date dateDebut, Date dateFin, boolean masquer);
    String buildPdfReportByAgency(Date date, Date date1, boolean masquer);
}
