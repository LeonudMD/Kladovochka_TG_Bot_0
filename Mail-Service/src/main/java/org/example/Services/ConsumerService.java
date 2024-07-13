package org.example.Services;

import org.example.DTO.MailParams;

public interface ConsumerService {

    void consumeRegistrationMail(MailParams mailParams);
}
