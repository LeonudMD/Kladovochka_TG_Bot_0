package org.example.Services;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducer {
    void producer(String rabitQueue, Update update);
}
