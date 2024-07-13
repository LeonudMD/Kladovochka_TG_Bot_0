package org.example.Services.Impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.example.Services.UpdateProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Log4j
@Component
public class UpdateProducerImpl implements UpdateProducer {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void producer(String rabbitQueue, Update update) {
        log.debug(update.getMessage().getText());
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }
}
