package com.abouna.lacussms.config;

import com.abouna.lacussms.dto.BkEtatOpConfigBean;
import com.abouna.lacussms.entities.BkEtatOp;
import com.abouna.lacussms.entities.Config;
import com.abouna.lacussms.service.LacusSmsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Configuration
public class ConditionConfig {
    private final LacusSmsService lacusSmsService;

    public ConditionConfig(LacusSmsService lacusSmsService) {
        this.lacusSmsService = lacusSmsService;
    }

    @PostConstruct
    public void init() {
        if (lacusSmsService.getAllConfig().isEmpty()) {
            lacusSmsService.enregistrerConfig(new Config(true, true, true, true));
        }
    }

    @Bean
    public BkEtatOpConfigBean condition() {
        return getBkEtatOpConfigBean();
    }

    private BkEtatOpConfigBean getBkEtatOpConfigBean() {
        List<BkEtatOp> list = lacusSmsService.getListBkEtatOp(true);

        StringBuilder condition = new StringBuilder();
        List<String> listString = new ArrayList<>();
        int taille = list.size();
        int i = 0;

        for(Iterator<BkEtatOp> opIterator = list.iterator(); opIterator.hasNext(); ++i) {
            BkEtatOp nextOp = opIterator.next();
            listString.add(nextOp.getValeur());
            if (i != taille - 1) {
                condition.append("b.ETA='").append(nextOp.getValeur()).append("' OR ");
            } else {
                condition.append("b.ETA='").append(nextOp.getValeur()).append("'");
            }
        }

        return new BkEtatOpConfigBean(listString, condition.toString());
    }
}
