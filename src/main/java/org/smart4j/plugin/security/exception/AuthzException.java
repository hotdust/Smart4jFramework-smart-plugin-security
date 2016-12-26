package org.smart4j.plugin.security.exception;

/**
 * 授权异常
 * Created by shijiapeng on 16/12/23.
 */
public class AuthzException extends RuntimeException {

    public AuthzException() {
    }

    public AuthzException(String message) {
        super(message);
    }

    public AuthzException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthzException(Throwable cause) {
        super(cause);
    }
}
