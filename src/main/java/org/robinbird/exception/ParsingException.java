package org.robinbird.exception;

/**
 * Created by seokhyun on 6/7/17.
 *
 * CAN BE REMOVED LATER
 */
public class ParsingException extends RuntimeException
{

	public ParsingException() {
		super();
	}

	public ParsingException(String message) {
		super(message);
	}

	public ParsingException(Throwable cause){
		super(cause);
	}

	public ParsingException(String message, Throwable cause){
		super(message, cause);
	}

}