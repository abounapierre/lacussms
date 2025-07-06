package com.abouna.lacussms.config;

import com.abouna.lacussms.dto.BkEtatOpConfigBean;
import com.abouna.lacussms.entities.BkEtatOp;
import com.abouna.lacussms.entities.Config;
import com.abouna.lacussms.service.LacusSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

@Configuration
public class ConditionConfig {
    private static final Logger log = LoggerFactory.getLogger(ConditionConfig.class);
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
    public BkEtatOpConfigBean condition(@Qualifier("external-configs") Properties externalConfigs) {
        return getBkEtatOpConfigBean(externalConfigs);
    }

    private BkEtatOpConfigBean getBkEtatOpConfigBean(Properties externalConfigs) {
        List<BkEtatOp> list = lacusSmsService.getListBkEtatOp(true);

        if (list.isEmpty()) {
            String operations = externalConfigs.getProperty("application.etat.operation.enabled");
            int i = 1;
            for(String op : operations.split(",")) {
                list.add(new BkEtatOp(i, op.trim(), true));
                i++;
            }
        }

        log.info("##### List of operations: {} #####", list);

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
