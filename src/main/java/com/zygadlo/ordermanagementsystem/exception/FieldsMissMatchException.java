package com.zygadlo.ordermanagementsystem.exception;

public class FieldsMissMatchException extends Exception {

    public FieldsMissMatchException(){
        super("There are differences between real file structure and saved");
    }

    public FieldsMissMatchException(String message){
        super(message);
    }
}
