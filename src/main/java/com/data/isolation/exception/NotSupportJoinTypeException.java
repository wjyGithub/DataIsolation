package com.data.isolation.exception;

/**
 * Created by jianyuan.wei@hand-china.com
 * on 2019/7/14 12:48.
 */
public class NotSupportJoinTypeException extends RuntimeException {

    public NotSupportJoinTypeException(String message) {
        super(message);
    }

    public NotSupportJoinTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
