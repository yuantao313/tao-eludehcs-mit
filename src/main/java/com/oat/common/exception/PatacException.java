package com.oat.common.exception;


/**
 * @author:yhl
 * @create: 2022-08-19 11:15
 * @Description: 自定义异常类
 */

public class PatacException extends RuntimeException {


    public PatacException() {
    }

    public PatacException(String message) {
        super(message);
    }

    public PatacException(String message, Throwable cause) {
        super(message, cause);
    }

    public PatacException(Throwable cause, String message) {
        super(message, cause);
    }

    public PatacException(Throwable cause) {
        super(cause);
    }

}
