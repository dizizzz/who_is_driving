package car.sharing.exception;

public class PaidPaymentsException extends RuntimeException {
    public PaidPaymentsException(String message) {
        super(message);
    }
}