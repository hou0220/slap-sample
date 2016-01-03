package com.hauchee.annotationprocessor;

class ProcessingException extends Exception {

    public ProcessingException(String msg, Object... args) {
        super(String.format(msg, args));
    }
}
