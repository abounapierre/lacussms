package com.abouna.lacussms.service.impl;

import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkCompCli;
import com.abouna.lacussms.entities.BkEve;
import com.abouna.lacussms.entities.Groupe;
import com.abouna.lacussms.service.GroupeSmsService;
import com.abouna.lacussms.service.LacusDataService;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.tools.FakeDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnBean(Environment.class)
public class LacusDataServiceImpl implements LacusDataService {
    private final LacusSmsService lacusSmsService;
    private final GroupeSmsService goupeSMSService;
    private final String profile;

    public LacusDataServiceImpl(LacusSmsService lacusSmsService, GroupeSmsService goupeSMSService, @Value("${spring.profiles.active}") String profile) {
        this.lacusSmsService = lacusSmsService;
        this.goupeSMSService = goupeSMSService;
        this.profile = profile;
    }

    private boolean isDev() {
        return profile.equals("dev");
    }

    public List<BkCli> getAllClients() {
        return isDev() ? FakeDataService.getAllBkClis() : lacusSmsService.getAllCli();
    }

    public List<Groupe> getAllClientsByGroupe() {
        return isDev() ? FakeDataService.getAllGroupes() : goupeSMSService.findAllGroupes();
    }

    public List<BkEve> getAllBkEves() {
        return isDev() ? FakeDataService.getAllBkEves() : lacusSmsService.getBkEveByLimit(100);
    }

    @Override
    public List<BkCompCli> getAllBkCompClis() {
        return isDev() ? FakeDataService.getAllBkCompClis() : lacusSmsService.getAllBkCompClis();
    }
}
