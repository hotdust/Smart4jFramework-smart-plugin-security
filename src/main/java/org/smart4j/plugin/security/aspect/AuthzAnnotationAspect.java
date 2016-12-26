package org.smart4j.plugin.security.aspect;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.annotation.Aspect;
import org.smart4j.framework.annotation.Controller;
import org.smart4j.framework.proxy.AspectProxy;
import org.smart4j.plugin.security.annotation.*;
import org.smart4j.plugin.security.exception.AuthzException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by shijiapeng on 16/12/25.
 */
@Aspect(Controller.class)
public class AuthzAnnotationAspect extends AspectProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthzAnnotationAspect.class);


    private static final Class[] ANNOTATION_CLASS_ARRAY = {
            Authenticated.class, User.class, Guest.class, HasRoles.class, HasPermissions.class
    };

    @Override
    public void before(Class<?> targetClass, Method targetMethod, Object[] methodParams) {

        LOGGER.info("AuthzAnnotationAspect before start");

        Annotation annotation = getAnnotation(targetClass, targetMethod);
        if (annotation == null)
            return;

        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (annotationType.equals(User.class)) {
            LOGGER.info("AuthzAnnotationAspect User annotation handler");
            handleUser();

        } else if (annotationType.equals(HasRoles.class)) {
            handleHasRoles(((HasRoles) annotation));
            LOGGER.info("AuthzAnnotationAspect HasRole annotation handler");

        } else if (annotationType.equals(HasPermissions.class)) {
            LOGGER.info("AuthzAnnotationAspect HasPermisstion annotation handler");
            handleHasPermissions(((HasPermissions) annotation));

        } else if (annotationType.equals(Authenticated.class)) {
            LOGGER.info("AuthzAnnotationAspect Authenticated annotation handler");
            handleAuthenticated();
        } else if (annotationType.equals(Guest.class)) {
            LOGGER.info("AuthzAnnotationAspect Guest annotation handler");
            handleGuest();
        }

        LOGGER.info("AuthzAnnotationAspect before end");
    }

    private void handleGuest() {
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principals = subject.getPrincipals();
        if (principals != null || !principals.isEmpty()) {
            throw new AuthzException("当前用户不是访客");
        }
    }

    private void handleAuthenticated() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            throw new AuthzException("当前用户尚未认证");
        }
    }

    private void handleHasPermissions(HasPermissions annotation) {
        String permissionName = annotation.value();
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isPermitted(permissionName)) {
            throw new AuthzException("当前用户没有指定权限，权限名：" + permissionName);
        }
    }

    private void handleHasRoles(HasRoles annotation) {
        String roleName = annotation.value();
        Subject subject = SecurityUtils.getSubject();
        if (!(subject.hasRole(roleName))) {
            throw new AuthzException("当前用户没有指定角色，角色名：" + roleName);
        }
    }


    private void handleUser() {
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principals = subject.getPrincipals();
        if (principals == null || principals.isEmpty()) {
            throw new AuthzException("当前用户未登录");
        }
    }

    private Annotation getAnnotation(Class<?> cls, Method method) {
        for (Class<? extends Annotation> annotationClass : ANNOTATION_CLASS_ARRAY) {
            if (method.isAnnotationPresent(annotationClass)) {
                return method.getAnnotation(annotationClass);
            }

            if (cls.isAnnotationPresent(annotationClass)) {
                return cls.getAnnotation(annotationClass);
            }

        }

        return null;
    }
}
