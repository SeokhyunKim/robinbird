package org.robinbird.exception;

/**
 * Created by seokhyun on 7/3/17.
 */
public class ExistingTypeNameException extends RuntimeException {

	public ExistingTypeNameException() { super(); }

	public ExistingTypeNameException(String message) { super(message); }

	public ExistingTypeNameException(Throwable cause) { super(cause); }

	public ExistingTypeNameException(String message, Throwable cause) {
		super(message, cause);
	}

}