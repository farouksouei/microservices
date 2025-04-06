package kripton.candidateservice.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email address already exists: " + email);
    }

	public String getEmail() {
		return getEmail();
	}
}