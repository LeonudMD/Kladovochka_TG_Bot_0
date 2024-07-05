package org.example.Services.Impls;

import org.example.DAO.AppUsersDAO;
import org.example.DAO.RawDataDAO;
import org.example.Entity.AppUsers;
import org.example.Entity.Enums.UsersState;
import org.example.Entity.RawData;
import org.example.Services.MainService;
import org.example.Services.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.example.Entity.Enums.UsersState.BASIC_STATE;

@Service
public class MainServiceIMPL implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUsersDAO appUsersDAO;


    public MainServiceIMPL(RawDataDAO rawDataDAO, ProducerService producerService, AppUsersDAO appUsersDAO) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUsersDAO = appUsersDAO;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var textMessage = update.getMessage();
        var telegramUser = textMessage.getFrom();
        var appUser = findOrSaveUser(telegramUser);

        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("hi, FROM NODE");
        producerService.produceAnswer(sendMessage);
    }

    private AppUsers findOrSaveUser(User telegramUser) {
        AppUsers presistentAppUser = appUsersDAO.findAppUsersByTelegramUserId(telegramUser.getId());
        if (presistentAppUser == null) {
            AppUsers transientAppUser = AppUsers.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    // TODO изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUsersDAO.save(transientAppUser);
        }
        return presistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
