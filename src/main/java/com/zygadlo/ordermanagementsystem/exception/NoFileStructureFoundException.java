package com.zygadlo.ordermanagementsystem.exception;

public class NoFileStructureFoundException extends RuntimeException{

    public NoFileStructureFoundException(String message) {
        super(message);
    }

    public NoFileStructureFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
