package org.example.Services.Impls;

import lombok.RequiredArgsConstructor;
import org.example.Controllers.UpdateProcessor;
import org.example.Services.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


@RequiredArgsConstructor
@Service
public class AnswerConsumerImpl implements AnswerConsumer {

    private final UpdateProcessor updateProcessor;


    @RabbitListener(queues = "${spring.rabbitmq.queues.answer-message}")
    @Override
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);

    }
}
