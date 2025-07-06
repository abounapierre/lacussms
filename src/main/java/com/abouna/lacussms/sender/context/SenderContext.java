package com.abouna.lacussms.sender.context;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.dto.SendResponseDTO;
import com.abouna.lacussms.sender.LacusSenderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SenderContext {
    SendResponseDTO send(String number, String message);
    SendResponseDTO send(List<String> numbers, String message);
    Map<String, LacusSenderService> senders = new HashMap<>();
    static SenderContext getInstance() {
        return ApplicationConfig.getApplicationContext().getBean(SenderContext.class);
    }
}
