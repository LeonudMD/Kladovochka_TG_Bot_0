package org.example.Services.Impls;

import lombok.extern.log4j.Log4j;
import org.example.DAO.AppUsersDAO;
import org.example.DAO.RawDataDAO;
import org.example.Entity.AppUsers;
import org.example.Entity.RawData;
import org.example.Services.MainService;
import org.example.Services.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.example.Entity.Enums.UsersState.BASIC_STATE;
import static org.example.Entity.Enums.UsersState.WAIT_FOR_EMAIL_STATE;
import static org.example.Services.Enums.ServicesCommands.CANCEL;
import static org.example.Services.Enums.ServicesCommands.HELP;
import static org.example.Services.Enums.ServicesCommands.REGISTRATION;
import static org.example.Services.Enums.ServicesCommands.START;

@Service
@Log4j
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

        var appUser = findOrSaveUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        if (CANCEL.equals(text)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //TODO обработка емайла
        } else {
            log.error("Неизвестная ошибка при обработке команды " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова...";
        }

        var chatID = update.getMessage().getChatId();
        sendAnswer(output, chatID);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);

        var chatID = update.getMessage().getChatId();
        var appUser = findOrSaveUser(update);

        if (isNotAllowToSendContent(chatID, appUser)) {
            return;
        }

        var answer = "Документ успешно загружен! Ссылка на скачивание: ///////// ";
        sendAnswer(answer, chatID);

    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);

        var chatID = update.getMessage().getChatId();
        var appUser = findOrSaveUser(update);

        if (isNotAllowToSendContent(chatID, appUser)) {
            return;
        }

        var answer = "Фото успешно загружен! Ссылка на скачивание: ///////// ";
        sendAnswer(answer, chatID);

    }

    private boolean isNotAllowToSendContent(Long chatID, AppUsers appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            var error = "Зарегистрируйтесь или активируйте свою учётную запись для загрузки контента.";
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую команду с помощью /сфтсуд для отправки файлов";
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatID) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);

    }

    private String processServiceCommand(AppUsers appUser, String cmd) {
        if (REGISTRATION.equals(cmd)) {
            //TODO добавить регистрацию
            return "Временно недоступно";
        } else if (HELP.equals(cmd)) {
            return help();
        } else if (START.equals(cmd)) {
            return "Приветствую! Чтобы узнать список всех доступных команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы узнать список всех доступных команд введите /help";
        }
    }

    private String help() {
        return """
        *Доступные команды:*

        \uD83D\uDCCC */help* - показывает это сообщение
        \uD83D\uDCCB */registration* - регистрация нового пользователя
        \u274C */cancel* - отмена текущей операции
        \uD83D\uDC4B */start* - приветственное сообщение и начало работы

        Если у вас есть вопросы или нужна помощь, просто введите одну из команд выше.
        """;
    }


    private String cancelProcess(AppUsers appUser) {
        appUser.setState(BASIC_STATE);
        appUsersDAO.save(appUser);
        return "Комманда отменена!";
    }

    private AppUsers findOrSaveUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
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
