package org.example.Services.Impls;

import lombok.extern.log4j.Log4j;
import org.example.Services.ConsumerService;
import org.example.Services.MainService;
import org.example.Services.ProducerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.Models.RabbitQueue.*;

@Service
@Log4j
public class ConsumerServiceIMPL implements ConsumerService {
    private final MainService mainService;


    public ConsumerServiceIMPL(MainService mainService) {
        this.mainService = mainService;
    }


    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdate(Update update) {
        log.debug("NODE: Текстовое сообщение было доставленно");
        mainService.processTextMessage(update);
    }

    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    @Override
    public void consumeDocMessageUpdate(Update update) {
        log.debug("NODE: Док сообщение было доставленно");
    }

    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    @Override
    public void consumePhotoMessageUpdate(Update update) {
        log.debug("NODE: Фото сообщение было доставленно");
    }
}
