package se.meltastudio.cms.parts.service;

/**
 * Exception thrown by PartsService when operations fail.
 */
public class PartsServiceException extends Exception {

    public PartsServiceException(String message) {
        super(message);
    }

    public PartsServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
