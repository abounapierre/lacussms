package com.abouna.lacussms.sender.context;

import com.abouna.lacussms.config.SmsProvider;
import com.abouna.lacussms.dto.SendResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SenderContextImpl implements SenderContext {
    @Override
    public SendResponseDTO send(String number, String message) {
        try {
            return senders.get(SmsProvider.getInstance().getName()).send(number, message);
        } catch (Exception e) {
            SendResponseDTO sendResponseDTO = new SendResponseDTO();
            sendResponseDTO.setSent(false);
            sendResponseDTO.setMessage("Error: " + e.getMessage());
            return sendResponseDTO;
        }
    }

    @Override
    public SendResponseDTO send(List<String> numbers, String message) {
        try {
            return senders.get(SmsProvider.getInstance().getName()).send(numbers, message);
        } catch (Exception e) {
            SendResponseDTO sendResponseDTO = new SendResponseDTO();
            sendResponseDTO.setSent(false);
            sendResponseDTO.setMessage("Error: " + e.getMessage());
            return sendResponseDTO;
        }
    }
}
