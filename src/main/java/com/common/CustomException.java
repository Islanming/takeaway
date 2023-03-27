package com.common;

/**
 * 自定义业务异常类
 * @author Lenovo
 */
public class CustomException extends RuntimeException{

    public CustomException(String message){
        super(message);
    }

}
