package com.myself.best.warehousing.common;

/**
 * 自定义业务异常
 */
public class CustomException extends RuntimeException{

    //有参的构造方法
    public CustomException(String message) {
        super(message);
    }

}
