package com.abouna.lacussms.service;

import com.abouna.lacussms.config.ApplicationConfig;

public interface PdfReportService {
    byte[] generatePdfReportAsByteArray(int month, int year) throws Exception;
    void buildPdfReport() throws Exception;
    static PdfReportService getInstance() {
        return ApplicationConfig.getApplicationContext().getBean(PdfReportService.class);
    }
}
