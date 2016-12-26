package org.smart4j.plugin.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.plugin.security.exception.AuthcException;

/**
 * Created by shijiapeng on 16/12/24.
 */
public class SecurityHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityHelper.class);

    /**
     * 登录处理
     * @param username 用户名
     * @param password 密码
     */
    public static void login(String username, String password) throws AuthcException {
        Subject subject = SecurityUtils.getSubject();

        if (subject == null)
            return;

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            LOGGER.error("login error (Security login)", e);
            throw new AuthcException(e);
        }
    }

    /**
     * 登出处理
     */
    public static void logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            subject.logout();
        }
    }
}
