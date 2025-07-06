package com.abouna.lacussms.service.impl;

import com.abouna.lacussms.dao.IBkAgenceDao;
import com.abouna.lacussms.dao.IBkMadDao;
import com.abouna.lacussms.dao.IMessageDao;
import com.abouna.lacussms.dao.IMessageMandatDao;
import com.abouna.lacussms.dto.RapportPdfModelDTO;
import com.abouna.lacussms.dto.RapportPdfModelGroupByAgenceDTO;
import com.abouna.lacussms.entities.BkAgence;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkEve;
import com.abouna.lacussms.entities.BkMad;
import com.abouna.lacussms.entities.Message;
import com.abouna.lacussms.entities.MessageMandat;
import com.abouna.lacussms.service.RapportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class RapportServiceImpl implements RapportService {
    private final IMessageDao messageDao;
    private final IMessageMandatDao messageMandatDao;
    private final IBkAgenceDao bkAgenceDao;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final IBkMadDao bkMadDao;

    public RapportServiceImpl(IMessageDao messageDao, IMessageMandatDao messageMandatDao, IBkAgenceDao bkAgenceDao, IBkMadDao bkMadDao) {
        this.messageDao = messageDao;
        this.messageMandatDao = messageMandatDao;
        this.bkAgenceDao = bkAgenceDao;
        this.bkMadDao = bkMadDao;
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

    @Override
    public List<RapportPdfModelDTO> getMessagesByPeriode(Date debut, Date fin) {
        try {
            List<RapportPdfModelDTO> messages = new ArrayList<>();
            messages.addAll(messageDao.getMessageFromPeriode(debut, fin).stream()
                    .map(this::toModel).collect(Collectors.toList()));
            messages.addAll(messageMandatDao.getMessageFromPeriode(debut, fin).stream()
                    .map(this::toModel).collect(Collectors.toList()));
            return messages;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RapportPdfModelGroupByAgenceDTO> getMessagesByPeriodeGroupByAgence(Date debut, Date fin) {
        try {
            List<RapportPdfModelGroupByAgenceDTO> result = new ArrayList<>();
            List<BkAgence> agences = bkAgenceDao.findAll();
            Map<String, List<RapportPdfModelDTO>> agenceMessagesMap = getMessagesByPeriode(debut, fin).stream()
                    .filter(message -> message.getAgenceCode() != null)
                    .collect(groupingBy(RapportPdfModelDTO::getAgenceCode));
            agenceMessagesMap.forEach((k,v) ->{
                RapportPdfModelGroupByAgenceDTO groupByAgenceDTO = new RapportPdfModelGroupByAgenceDTO();
                groupByAgenceDTO.setCodeAgence(k);
                groupByAgenceDTO.setNomAgence(agences.stream()
                        .filter(agence -> agence.getNuma().equals(k))
                        .map(BkAgence::getNoma)
                        .findFirst()
                        .orElse("N/A"));
                groupByAgenceDTO.setDataModels(v);
                result.add(groupByAgenceDTO);
            });
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        BkEve bkEve = message.getBkEve();
        model.setClientAccountNumber(bkEve != null ? bkEve.getCompte() != null ? bkEve.getCompte() : "N/A" : "N/A");
        BkCli bkCli = message.getBkEve() != null ? message.getBkEve().getCli() : null;
        String clientName = bkCli != null ? bkCli.getNom() +  " " + bkCli.getPrenom() : "N/A";
        model.setMessageTitle(bkEve != null ? bkEve.getOpe() != null ? bkEve.getOpe().getLib() : "N/A" : "N/A");
        model.setClientName(clientName);
        BkAgence bkAgence = message.getBkEve() != null ? message.getBkEve().getBkAgence() : null;
        model.setAgence(bkAgence != null ? bkAgence.getNoma() != null ? bkAgence.getNoma() : "N/A" : "N/A");
        model.setAgenceCode(bkAgence != null ? bkAgence.getNuma() != null ? bkAgence.getNuma() : "N/A" : "N/A");
        model.setMessageStatus(message.getSent() ? "Envoyé" : "Non envoyé");
        return model;
    }

    private RapportPdfModelDTO toModel(MessageMandat message) {
        try {
            RapportPdfModelDTO model = new RapportPdfModelDTO();
            model.setIdMessage(String.valueOf(message.getId()));
            model.setMessageContent(message.getContent());
            model.setClientPhone(message.getNumero());
            model.setSentDate(simpleDateFormat.format(message.getSendDate()));
            BkMad bkMad = bkMadDao.findById(message.getBkMad());
            if(bkMad != null) {
                model.setAgence(bkMad.getAge() != null ? bkMad.getAge().getNoma() != null ?  bkMad.getAge().getNoma() : "N/A": "N/A");
                model.setAgenceCode(bkMad.getAge() != null ? bkMad.getAge().getNuma() != null ?  bkMad.getAge().getNuma() : "N/A": "N/A");
                model.setClientName("N/A");
                model.setClientAccountNumber(bkMad.getEve());
                model.setMessageTitle(bkMad.getOpe() != null ? bkMad.getOpe().getLib() != null ? bkMad.getOpe().getLib() : "N/A" : "N/A");
            } else {
                model.setAgence("N/A");
                model.setAgenceCode("N/A");
                model.setClientName("N/A");
                model.setClientAccountNumber("N/A");
                model.setMessageTitle("N/A");
            }
            model.setMessageStatus(message.getSent() ? "Envoyé" : "Non envoyé");
            return model;
        }  catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    private static int getLastDayOfMonth(int month, int year) {
        YearMonth date = YearMonth.of(year, month);
        return date.atEndOfMonth()
                .getDayOfMonth();
    }
}
