package org.example.Controllers;

import lombok.extern.log4j.Log4j;
import org.example.Services.UpdateProducer;
import org.example.Utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.Models.RabbitQueue.*;

@Component
@Log4j
public class UpdateController {
    private TelegramBot tgBot;
    private MessageUtils messageUtils;
    private UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot tgBot) {
        this.tgBot = tgBot;
    }
    
    public void processUpdate(Update update) {
        if (update == null){
            log.error("Полученное сообщение равно null");
            return;
        }
        
        if (update.hasMessage()) {
            distributeMessangesbyType(update);
        }
        else {
            log.error("Получен необрабатываемый тип сообщения: " + update);
        }
    }

    private void distributeMessangesbyType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) {
            processTextMessage(update);
        }
        else if (message.hasDocument()) {
            processDocMessage(update);
        }
        else if (message.hasPhoto()) {
            processPhotoMessage(update);
        }
        else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWhisText(update,
                "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        tgBot.sendAnswerMessage(sendMessage);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.producer(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceiveView(update);
    }

    private void processDocMessage(Update update) {
        updateProducer.producer(DOC_MESSAGE_UPDATE, update);
        setFileIsReceiveView(update);
    }

    private void processTextMessage(Update update) {
        updateProducer.producer(TEXT_MESSAGE_UPDATE, update);
    }

    private void setFileIsReceiveView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWhisText(update,
                "Файл получен. Идёт обработка...");
        setView(sendMessage);
    }
}

