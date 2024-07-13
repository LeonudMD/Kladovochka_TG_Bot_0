package org.example.Services.Impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.example.Services.ConsumerService;
import org.example.Services.MainService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceIMPL implements ConsumerService {
    private final MainService mainService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.text-message-update}")
    public void consumeTextMessageUpdate(Update update) {
        log.debug("NODE: Текстовое сообщение было доставленно");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.doc-message-update}")
    public void consumeDocMessageUpdate(Update update) {
        log.debug("NODE: Док сообщение было доставленно");
        mainService.processDocMessage(update);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.photo-message-update}")
    @Override
    public void consumePhotoMessageUpdate(Update update) {
        log.debug("NODE: Фото сообщение было доставленно");
        mainService.processPhotoMessage(update);
    }
}
