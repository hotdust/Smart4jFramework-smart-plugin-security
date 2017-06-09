package org.smart4j.plugin.security;

import org.apache.shiro.web.env.EnvironmentLoaderListener;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * 这段代码的作用是，省略了在 web.xml 配置。
 * ServletContainerInitializer 接口的作用是，web 组件在启动时，可以使用使用这个接口，
 * 来对组件做一些初始化操作
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
