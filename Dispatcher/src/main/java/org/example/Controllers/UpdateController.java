package org.example.Controllers;

import lombok.extern.log4j.Log4j;
import org.example.Utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Log4j
public class UpdateController {
    private TelegramBot tgBot;
    private MessageUtils messageUtils;

    public UpdateController(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    public void registerBot(TelegramBot tgBot) {
        this.tgBot = tgBot;
    }
    
    public void processUpdate(Update update) {
        if (update == null){
            log.error("Полученное сообщение равно null");
            return;
        }
        
        if (update.getMessage() != null) {
            distributeMessangesbyType(update);
        }
        else {
            log.error("Получен необрабатываемый тип сообщения: " + update);
        }
    }

    private void distributeMessangesbyType(Update update) {
        var message = update.getMessage();
        if (message.getText() != null) {
            processTextMessage(update);
        }
        else if (message.getDocument() != null) {
            processDocMessage(update);
        }
        else if (message.getPhoto() != null) {
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

    private void setView(SendMessage sendMessage) {
        tgBot.sendAnswerMessage(sendMessage);
    }

    private void processPhotoMessage(Update update) {

    }

    private void processDocMessage(Update update) {

    }

    private void processTextMessage(Update update) {
    }
}

