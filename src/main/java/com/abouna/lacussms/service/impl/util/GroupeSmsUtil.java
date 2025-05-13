package com.abouna.lacussms.service.impl.util;

import com.abouna.lacussms.dto.GroupeClientDTO;
import com.abouna.lacussms.dto.GroupeSmsRequestDTO;
import com.abouna.lacussms.dto.SendResponseDTO;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.sender.context.SenderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.abouna.lacussms.views.tools.ConstantUtils.COMA;

public class GroupeSmsUtil {
    private static final Logger log = LoggerFactory.getLogger(GroupeSmsUtil.class);

    public static void validateGroupeSmsRequest(GroupeSmsRequestDTO requestDTO) {
    }

    public static void sendWithPhoneNumbers(SenderContext senderContext, String msg, String phoneNumbers) {
        try {
            SendResponseDTO res = senderContext.send(Arrays.stream(phoneNumbers.split(COMA)).collect(Collectors.toList()), msg);
            log.info("Sent SMS to phone numbers: {}", res);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SMS", e);
        }
    }

    public static void sendWithGroupIds(SenderContext senderContext, String msg, List<GroupeClientDTO> groupeClients, boolean personalized) {
        groupeClients.forEach( groupeClientDTO -> sendSmsWithClients(senderContext, msg, groupeClientDTO.getClients(), personalized));
    }

    public static void sendWithClientIds(SenderContext senderContext, String msg, List<BkCli> clients, boolean personalized) {
        sendSmsWithClients(senderContext, msg, clients, personalized);
    }

    private static void sendSmsWithClients(SenderContext senderContext, String msg, List<BkCli> clients, boolean personalized) {
        try {
            if(personalized) {
                List<SendResponseDTO> res = clients.stream().map(client -> senderContext.send(msg, Long.toString(client.getPhone()))).collect(Collectors.toList());
                log.info("Sent personalized SMS to clients: {}", res);
            } else {
                SendResponseDTO res = senderContext.send(clients.stream().map(client -> Long.toString(client.getPhone())).collect(Collectors.toList()), msg);
                log.info("Sent bulk SMS to clients: {}", res);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SMS", e);
        }
    }
}
