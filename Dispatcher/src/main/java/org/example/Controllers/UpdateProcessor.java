package org.example.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.example.Configurations.RabbitConfiguration;
import org.example.Services.UpdateProducer;
import org.example.Utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@Log4j
@RequiredArgsConstructor
@Component
public class UpdateProcessor {

    private TelegramBot tgBot;

    private MessageUtils messageUtils;

    private UpdateProducer updateProducer;

    private final RabbitConfiguration rabbitConfiguration;

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

    private void setFileIsReceiveView(Update update) {

        var sendMessage = messageUtils.generateSendMessageWhisText(update,
                "Файл получен. Идёт обработка...");
        setView(sendMessage);

    }

    public void setView(SendMessage sendMessage) {
        tgBot.sendAnswerMessage(sendMessage);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.producer(rabbitConfiguration.getPhotoMessageUpdateQueue(), update);
        setFileIsReceiveView(update);
    }

    private void processDocMessage(Update update) {
        updateProducer.producer(rabbitConfiguration.getPhotoMessageUpdateQueue(),
                update);
        setFileIsReceiveView(update);
    }

    private void processTextMessage(Update update) {
        updateProducer.producer(rabbitConfiguration.getTextMessageUpdateQueue(), update);
    }


}

