package org.robinbird.exception;

/**
 * Created by seokhyun on 6/7/17.
 *
 * CAN BE REMOVED LATER
 */
public class ParseTreeException extends Exception
{

	private static final long serialVersionUID = 1L;

	public ParseTreeException() {}

	public ParseTreeException(String message) {
		super(message);
	}

	public ParseTreeException(Throwable cause){
		super(cause);
	}

	public ParseTreeException(String message, Throwable cause){
		super(message, cause);
	}

	public ParseTreeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}