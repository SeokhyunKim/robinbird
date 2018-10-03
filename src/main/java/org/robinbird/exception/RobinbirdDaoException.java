package org.robinbird.exception;

public class RobinbirdDaoException extends RuntimeException {

    public RobinbirdDaoException() { super(); }

    public RobinbirdDaoException(String message) { super(message); }

    public RobinbirdDaoException(Throwable cause) { super(cause); }

    public RobinbirdDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
