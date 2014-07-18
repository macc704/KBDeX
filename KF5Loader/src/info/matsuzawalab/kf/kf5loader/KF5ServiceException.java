package info.matsuzawalab.kf.kf5loader;

public class KF5ServiceException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private int httpCode;
	
	public KF5ServiceException(String message, int httpCode) {
		super(message);
		this.httpCode = httpCode;
	}

	public KF5ServiceException(Throwable cause, int httpCode) {
		super(cause);
		this.httpCode = httpCode;
	}

	public int getHttpCode() {
		return httpCode;
	}
	
}
