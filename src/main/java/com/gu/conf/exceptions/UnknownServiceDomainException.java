package com.gu.conf.exceptions;

// This is deliberately a runtime exception as users of the configuration library
// are not expected to catch and handle it
public class UnknownServiceDomainException extends RuntimeException {
    public UnknownServiceDomainException() {
    }

    public UnknownServiceDomainException(String message) {
        super(message);
    }

    public UnknownServiceDomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownServiceDomainException(Throwable cause) {
        super(cause);
    }
}
