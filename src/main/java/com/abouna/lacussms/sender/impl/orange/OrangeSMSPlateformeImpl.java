package com.abouna.lacussms.sender.impl.orange;

import com.abouna.lacussms.dto.SendResponseDTO;
import com.abouna.lacussms.sender.LacusSenderService;
import com.abouna.lacussms.sender.context.SenderContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service("orangeSmsBean")
public class OrangeSMSPlateformeImpl implements LacusSenderService {
    @Override
    public SendResponseDTO send(String number, String msg) {
        return OrangeSMSPlateforme.send(number, msg);
    }

    @Override
    public SendResponseDTO send(List<String> numbers, String msg) {
        return OrangeSMSPlateforme.send(numbers, msg);
    }

    @PostConstruct
    void register() {
        SenderContext.senders.put("orange", this);
    }
}
