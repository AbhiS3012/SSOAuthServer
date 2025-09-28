package domain.in.exception;

@SuppressWarnings("serial")
public class InvalidCaptchaException extends RuntimeException {

	public InvalidCaptchaException(String message) {
		super(message);
	}

}
