package com.abouna.lacussms.service;

import com.abouna.lacussms.dto.RapportPdfModelDTO;
import com.abouna.lacussms.dto.ReportPDFInputDTO;
import com.abouna.lacussms.pdf.PrintReportPDF;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.abouna.lacussms.pdf.DateUtils.getPreviousMonth;
import static com.abouna.lacussms.pdf.DateUtils.getPreviousYear;

@SpringBootTest
@ActiveProfiles(value = "test")
public class PdfReportServiceTest {
    private static final Logger log = LoggerFactory.getLogger(PdfReportServiceTest.class);
    @Autowired
    private PdfReportService pdfReportService;

    @Test
   public void testBuildPartnerContractPdf() {
        List<RapportPdfModelDTO> dtoList = getList();
        ReportPDFInputDTO input = new ReportPDFInputDTO();
        LocalDate currentDate = LocalDate.now();
        int currentMonth = getPreviousMonth(currentDate);
        int currentYear = getPreviousYear(currentDate);
        input.setDataModels(dtoList);
        input.setMonth(String.valueOf(currentMonth));
        input.setYear(String.valueOf(currentYear));
        input.setEntreprise("Test PDF Report");
        try (FileOutputStream outputStream = new FileOutputStream("./target/report.pdf")) {
            outputStream.write(new PrintReportPDF(input).getBytes());
        } catch (Exception e) {
            log.error("Error generating PDF report", e);
        }
    }

    private List<RapportPdfModelDTO> getList() {
        List<RapportPdfModelDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < 1500; i++) {
            RapportPdfModelDTO dto = new RapportPdfModelDTO();
            dto.setIdMessage(String.valueOf(i));
            dto.setClientName("Name " + i);
            dto.setClientPhone("698984177 " + i);
            dto.setMessageStatus("Success " + i);
            dto.setSentDate("Test message " + i);
            dto.setMessageContent("Test message content " + i);
            dto.setMessageTitle("Test message title " + i);
            dto.setIdClient("Client " + i);
            dtoList.add(dto);
        }
        return dtoList;
    }
}
