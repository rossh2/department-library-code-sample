package library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = BorrowingException.BORROWING_MESSAGE)
public class BorrowingException extends RuntimeException {

    /*package*/ static final String BORROWING_MESSAGE = "The book cannot be borrowed because it is already borrowed, " +
            "or cannot be returned because it is not borrowed.";

    public BorrowingException(String message) {
        super(message);
    }
}
