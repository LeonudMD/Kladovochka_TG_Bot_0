package org.example.Services.Impls;

import org.example.Controllers.UpdateController;
import org.example.Services.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.example.Models.RabbitQueue.ANSWER_MESSAGE;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @RabbitListener(queues = ANSWER_MESSAGE)
    @Override
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);

    }
}
