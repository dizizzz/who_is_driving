package car.sharing.service.telegram;

import car.sharing.exception.TelegramNotificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
@Component
public class TelegramNotificationServiceImpl extends TelegramLongPollingBot
        implements TelegramNotificationService {
    @Value("${telegram.bot.username}")
    private String username;

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.chat.id}")
    private Long chatId;

    @Override
    public void sendMessage(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        sendMessage.setParseMode("HTML");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new TelegramNotificationException("Can`t send message: " + message);
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                chatId = update.getMessage().getChatId();
                sendInfoMessage(update.getMessage().getChat().getFirstName());
            }
        }
    }

    private void sendInfoMessage(String name) {
        String info = "Hello, " + name + "!\n"
                + "<b>I am your Car Sharing Bot.</b>\n"
                + "\nI can help you with the following tasks:\n"
                + "<b>Send Notifications on Rental Creation:</b> "
                + "I'll notify you about new rentals with all details.\n"
                + "<b>Daily Overdue Rental Checks:</b> "
                + "I'll check and notify you about overdue rentals daily.";
        sendMessage(info);
    }
}
