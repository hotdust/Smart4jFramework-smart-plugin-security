package org.smart4j.plugin.security.exception;

/**
 * 认证异常
 * Created by shijiapeng on 16/12/23.
 */
public class AuthcException extends Exception {
    public AuthcException() {
    }

    public AuthcException(String message) {
        super(message);
    }

    public AuthcException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthcException(Throwable cause) {
        super(cause);
    }

}
