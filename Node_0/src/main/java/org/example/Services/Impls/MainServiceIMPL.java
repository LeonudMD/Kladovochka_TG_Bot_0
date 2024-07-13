package org.example.Services.Impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.example.DAO.AppUsersDAO;
import org.example.DAO.RawDataDAO;
import org.example.Entity.AppDocument;
import org.example.Entity.AppPhoto;
import org.example.Entity.AppUsers;
import org.example.Entity.RawData;
import org.example.Exeptions.UploadFileException;
import org.example.Services.AppUserService;
import org.example.Services.Enums.LinkType;
import org.example.Services.Enums.ServicesCommands;
import org.example.Services.FileService;
import org.example.Services.MainService;
import org.example.Services.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.transaction.Transactional;

import static org.example.Entity.Enums.UsersState.BASIC_STATE;
import static org.example.Entity.Enums.UsersState.WAIT_FOR_EMAIL_STATE;
import static org.example.Services.Enums.ServicesCommands.CANCEL;


@Log4j
@RequiredArgsConstructor
@Service
public class MainServiceIMPL implements MainService {

    private final RawDataDAO rawDataDAO;

    private final ProducerService producerService;

    private final AppUsersDAO appUsersDAO;

    private final FileService fileService;

    private final AppUserService appUserService;

    @Transactional
    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var appUser = findOrSaveUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServicesCommands.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text);
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
        var appUser = findOrSaveUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument doc = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(doc.getId(), LinkType.GET_DOC);
            var answer = "Документ успешно загружен! "
                    + "Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            var answer = "Фото успешно загружено! "
                    + "Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "К сожалению, загрузка фото не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    private boolean isNotAllowToSendContent(Long chatID, AppUsers appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            var error = "Зарегистрируйтесь или активируйте свою учётную запись для загрузки контента.";
            sendAnswer(error, chatID);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую команду с помощью /сфтсуд для отправки файлов";
            sendAnswer(error, chatID);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatID) {

        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);

    }

    private String processServiceCommand(AppUsers appUser, String cmd) {
        ServicesCommands serviceCommand = ServicesCommands.fromValue(cmd);
        if (serviceCommand == null) {
            return "Неизвестная команда! Чтобы узнать список всех доступных команд введите /help";
        }

        switch (serviceCommand) {
            case REGISTRATION:
                return appUserService.registerUser(appUser);
            case HELP:
                return help();
            case START:
                return "Приветствую! Чтобы узнать список всех доступных команд введите /help";
            default:
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
        var optional = appUsersDAO.findByTelegramUserId(telegramUser.getId());
        if (optional.isEmpty()) {
            AppUsers transientAppUser = AppUsers.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            return appUsersDAO.save(transientAppUser);
        }
        return optional.get();
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
