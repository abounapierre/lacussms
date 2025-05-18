package com.abouna.lacussms.service.impl;

import com.abouna.lacussms.dao.IMessageDao;
import com.abouna.lacussms.dao.IMessageMandatDao;
import com.abouna.lacussms.dto.RapportPdfModelDTO;
import com.abouna.lacussms.entities.Message;
import com.abouna.lacussms.entities.MessageMandat;
import com.abouna.lacussms.service.RapportService;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RapportServiceImpl implements RapportService {
    private final IMessageDao messageDao;
    private final IMessageMandatDao messageMandatDao;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public RapportServiceImpl(IMessageDao messageDao, IMessageMandatDao messageMandatDao) {
        this.messageDao = messageDao;
        this.messageMandatDao = messageMandatDao;
    }

    @Override
    public List<RapportPdfModelDTO> getMessagesByMonthAndYear(int month, int year) {
        try {
            if (month < 1 || month > 12) {
                throw new IllegalArgumentException("Invalid month: " + month);
            }
            if (year < 2025) {
                throw new IllegalArgumentException("Invalid year: " + year + ". Year must be 2025 or later.");
            }
            List<RapportPdfModelDTO> messages = new ArrayList<>();
            messages.addAll(getMessages(month, year));
            messages.addAll(getMessagesMandat(month, year));
            return messages;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving messages: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RapportPdfModelDTO> getMessagesByMonthAndYearAgence(int month, int year, String agence) {
        return Collections.emptyList();
    }

    private List<RapportPdfModelDTO> getMessagesMandat(int month, int year) {
        try {
            Date startDate = getStartDate(month, year);
            Date endDate = getEndDate(month, year);
            return messageMandatDao.getMessageFromPeriode(startDate, endDate).stream()
                    .map(this::toModel).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<RapportPdfModelDTO> getMessages(int month, int year) {
        try {
            Date startDate = getStartDate(month, year);
            Date endDate = getEndDate(month, year);
            return messageDao.getMessageFromPeriode(startDate, endDate).stream()
                    .map(this::toModel).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Date getEndDate(int month, int year) throws ParseException {
        return simpleDateFormat.parse(getLastDayOfMonth(month, year) + "-" + month + "-" + year + " 23:59:59");
    }

    private Date getStartDate(int month, int year) throws ParseException {
        return simpleDateFormat.parse("01-" + month + "-" + year + " 00:00:00");
    }

    private RapportPdfModelDTO toModel(Message message) {
        RapportPdfModelDTO model = new RapportPdfModelDTO();
        model.setIdMessage(String.valueOf(message.getId()));
        model.setMessageContent(message.getContent());
        model.setClientPhone(message.getNumero());
        model.setSentDate(simpleDateFormat.format(message.getSendDate()));
        model.setMessageStatus(message.getSent() ? "Envoyé" : "Non envoyé");
        return model;
    }

    private RapportPdfModelDTO toModel(MessageMandat message) {
        RapportPdfModelDTO model = new RapportPdfModelDTO();
        model.setIdMessage(String.valueOf(message.getId()));
        model.setMessageContent(message.getContent());
        model.setClientPhone(message.getNumero());
        model.setSentDate(simpleDateFormat.format(message.getSendDate()));
        model.setMessageStatus(message.getSent() ? "Envoyé" : "Non envoyé");
        return model;
    }

    private static int getLastDayOfMonth(int month, int year) {
        YearMonth date = YearMonth.of(year, month);
        return date.atEndOfMonth()
                .getDayOfMonth();
    }
}
