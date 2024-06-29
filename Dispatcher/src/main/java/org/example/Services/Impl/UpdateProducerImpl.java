package org.example.Services.Impl;

import lombok.extern.log4j.Log4j;
import org.example.Services.UpdateProducer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Log4j
public class UpdateProducerImpl implements UpdateProducer {
    @Override
    public void producer(String rabitQueue, Update update) {
        log.debug(update.getMessage().getText());
    }
}
