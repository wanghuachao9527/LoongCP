package org.loong.exception;

public class LoongPoolException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public LoongPoolException(String msg) {
        super(msg);
    }

    public LoongPoolException(String msg, Throwable t) {
        super(msg, t);
    }

}
