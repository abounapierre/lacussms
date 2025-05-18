/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.pdf;

import com.abouna.lacussms.dto.RapportPdfModelDTO;
import com.abouna.lacussms.dto.ReportPDFInputDTO;
import com.abouna.lacussms.views.utils.Logger;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 *
 * @author SATELLITE
 */
public class PrintReportPDF {
    private final byte[] bytes;
    private static final String EMPTY_STRING = "";

    public PrintReportPDF(ReportPDFInputDTO input) {
        Font headerFont = new Font();
        headerFont.setStyle(Font.BOLD);
        headerFont.setSize(7.5f);
        Font titleFont = new Font();
        titleFont.setStyle(Font.BOLD);
        titleFont.setSize(16f);
        titleFont.setColor(BaseColor.RED);
        Document document = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            document = new Document(PageSize.A4.rotate(), 10, 10, 50, 50);
            PdfWriter.getInstance(document, output);
            document.open();
            Paragraph p = new Paragraph(input.getEntreprise() == null ? EMPTY_STRING : input.getEntreprise(), titleFont);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);
            document.add(new Paragraph(" "));
            p = new Paragraph("RAPPORT DES MESSAGES ENVOYES DE LA PERIODE ALLANT DU MOIS " + input.getMonth() + " " + input.getYear(), titleFont);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);
            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(9);
            table.setWidths(new float[]{2f, 3f, 10f, 4f, 4f, 6f, 4f, 4f, 4f});
            table.setSpacingBefore(10);
            table.setWidthPercentage(100);
            PdfPCell cell;
            cell = new PdfPCell(new Phrase("Code", headerFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Titre", headerFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Message", headerFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Date", headerFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Destinataire", headerFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Client", headerFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Compte", headerFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Agence", headerFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Statut", headerFont));
            table.addCell(cell);

            int i = 1;
            for (RapportPdfModelDTO msg : input.getDataModels()) {
                cell = new PdfPCell(new Phrase(msg.getIdMessage() == null ? EMPTY_STRING : msg.getIdMessage(), headerFont));
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(msg.getMessageTitle() == null ? EMPTY_STRING : msg.getMessageTitle(), headerFont));
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(msg.getMessageContent() == null ? EMPTY_STRING : msg.getMessageContent(), headerFont));
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(msg.getSentDate() == null ? EMPTY_STRING : msg.getSentDate(), headerFont));
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(msg.getClientPhone() == null ? EMPTY_STRING : msg.getClientPhone(), headerFont));
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(msg.getClientName() == null ? EMPTY_STRING : msg.getClientName(), headerFont));
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(msg.getClientAccountNumber() == null ? EMPTY_STRING : msg.getClientAccountNumber(), headerFont));
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(msg.getAgence() == null ? EMPTY_STRING : msg.getAgence(), headerFont));
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(msg.getMessageStatus() == null ? EMPTY_STRING : msg.getMessageStatus(), headerFont));
                table.addCell(cell);
                i++;
            }
            document.add(table);
            document.add(new Paragraph(" "));
            p = new Paragraph("TOTAL ENVOYE(S) : " + i, headerFont);
            p.setAlignment(Element.ALIGN_LEFT);
            document.add(p);
            document.add(new Paragraph(" "));
            Date d = new Date();
            p = new Paragraph(d.toString(), headerFont);
            p.setAlignment(Element.ALIGN_RIGHT);
            document.add(p);
        } catch (DocumentException e) {
            Logger.error("Erreur lors de la création du document PDF: " + e.getMessage(), e, getClass());
        } finally {
            assert document != null;
            document.close();
            this.bytes = output.toByteArray();
            try {
                output.close();
            } catch (Exception e) {
                Logger.error("Erreur lors de la fermeture du flux de sortie: " + e.getMessage(), e, getClass());
            }
        }
    }

    public byte[] getBytes() {
        return bytes;
    }
}
