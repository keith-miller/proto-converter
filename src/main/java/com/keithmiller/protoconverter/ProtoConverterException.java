package com.keithmiller.protoconverter;

public class ProtoConverterException extends Exception {
    public ProtoConverterException(String message) {
        super(message);
    }

    public ProtoConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtoConverterException(Throwable cause) {
        super(cause);
    }

    public ProtoConverterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
