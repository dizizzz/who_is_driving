package car.sharing.exception;

public class PendingPaymentsException extends RuntimeException {
    public PendingPaymentsException(String message) {
        super(message);
    }
}