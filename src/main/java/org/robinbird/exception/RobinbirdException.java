package org.robinbird.exception;

public class RobinbirdException extends RuntimeException{

    public RobinbirdException() { super(); }

    public RobinbirdException(String message) { super(message); }

    public RobinbirdException(Throwable cause) { super(cause); }

    public RobinbirdException(String message, Throwable cause) {
        super(message, cause);
    }

}
