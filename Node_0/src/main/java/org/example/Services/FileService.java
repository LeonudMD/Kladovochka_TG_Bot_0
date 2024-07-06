package org.example.Services;

import org.example.Entity.AppDocument;
import org.example.Entity.AppPhoto;
import org.example.Services.Enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long docId, LinkType linkType);
}
