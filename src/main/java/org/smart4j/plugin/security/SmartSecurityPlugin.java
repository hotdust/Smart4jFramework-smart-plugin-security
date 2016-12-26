package org.smart4j.plugin.security;

import org.apache.shiro.web.env.EnvironmentLoaderListener;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * Created by shijiapeng on 16/12/24.
 */
public class SmartSecurityPlugin implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        // 设置初始化参数，在EnvironmentLoaderListener里会读取这个参数
        ctx.setInitParameter("shiroConfigLocations", "classpath:smart-security.ini");
        // 注册listener
        ctx.addListener(EnvironmentLoaderListener.class);
        // 注册filter
        // addFilter方法的第二个参数必须为flase。设置为false后，这个filter就在所有Filter的最前面。
        FilterRegistration.Dynamic filter = ctx.addFilter("smartSecurityFilter", SmartSecurityFilter.class);
        filter.addMappingForUrlPatterns(null, false, "/*");
    }
}
