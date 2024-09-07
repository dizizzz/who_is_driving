package car.sharing.mock.service;

import car.sharing.service.telegram.TelegramNotificationService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Profile("test")
@Service
public class TelegramNotificationServiceMock extends TelegramLongPollingBot
        implements TelegramNotificationService {

    @Override
    public void sendMessage(String message) {
        System.out.println("Mock sendMessage called with message: " + message);
    }

    @Override
    public String getBotUsername() {
        return "mockBotUsername";
    }

    @Override
    public String getBotToken() {
        return "mockBotToken";
    }

    @Override
    public void onUpdateReceived(Update update) {

    }
}
