package car.sharing.exception;

public class TelegramNotificationException extends RuntimeException {
    public TelegramNotificationException(String message) {
        super(message);
    }
}
