package com.abouna.lacussms.sender;

import com.abouna.lacussms.dto.SendResponseDTO;

import java.util.List;

public interface LacusSenderService {
    SendResponseDTO send(String number, String msg);
    SendResponseDTO send(List<String> numbers, String msg);
}
