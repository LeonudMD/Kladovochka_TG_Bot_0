package org.example.Services.Impls;

import org.example.Controllers.UpdateProcessor;
import org.example.Services.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.example.Models.RabbitQueue.ANSWER_MESSAGE;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateProcessor updateProcessor;

    public AnswerConsumerImpl(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }

    @RabbitListener(queues = ANSWER_MESSAGE)
    @Override
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);

    }
}
