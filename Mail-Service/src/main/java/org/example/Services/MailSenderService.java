package org.example.Services;

import org.example.DTO.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
