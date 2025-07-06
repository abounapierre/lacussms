package com.abouna.lacussms.service;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.dto.RapportPdfModelDTO;
import com.abouna.lacussms.dto.RapportPdfModelGroupByAgenceDTO;

import java.util.Date;
import java.util.List;

public interface RapportService {
    List<RapportPdfModelDTO> getMessagesByMonthAndYear(int month, int year);
    List<RapportPdfModelDTO> getMessagesByMonthAndYearAgence(int month, int year, String agence);
    static RapportService getInstance() { return ApplicationConfig.getApplicationContext().getBean(RapportService.class); }
    List<RapportPdfModelDTO> getMessagesByPeriode(Date debut, Date fin);
    List<RapportPdfModelGroupByAgenceDTO> getMessagesByPeriodeGroupByAgence(Date debut, Date fin);
}
