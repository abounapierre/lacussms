package com.abouna.lacussms.sender;

import com.abouna.lacussms.dto.SendResponseDTO;

public interface LacusSenderService {
    SendResponseDTO send(String number, String msg);
}
