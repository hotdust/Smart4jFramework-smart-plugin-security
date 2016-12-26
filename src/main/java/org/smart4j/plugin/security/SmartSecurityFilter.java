package org.smart4j.plugin.security;

import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.CachingSecurityManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.smart4j.framework.util.ArrayUtil;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.framework.util.StringUtil;
import org.smart4j.plugin.security.realm.SmartCustomRealm;
import org.smart4j.plugin.security.realm.SmartJdbcRealm;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by shijiapeng on 16/12/24.
 */
public class SmartSecurityFilter extends ShiroFilter{


    @Override
    public void init() throws Exception {
        super.init();
        WebSecurityManager securityManager = super.getSecurityManager();
        // 按照自定义的ini文件中的配置，EnvironmentLoaderListener是无法设置Realm的，
        // 所以自己根据配置，设置Realm。
        // 不是所有定义在smart-security.ini文件里的配置，都需要我们手动来设置。
        // 那些不符合shiro.ini规则的配置需要手动来设置，那些符合规则的靠shiro自己识别就行。
        // TODO: 16/12/25 这个设置Realm的动作，可不可以扩展Listener来做呢？在这里做的好处是什么呢？
        // 设置Realm，可同时支持多个Realm，并按先后顺序用逗号分隔
        setRealms(securityManager);
        // 设置cache
        setCache(securityManager);
    }



    private void setRealms(WebSecurityManager webSecurityManager) {
        // 读取smart.plugin.security.realms
        String reamls = SecurityConfig.getReamls();
        // 判断是否为空
        if (StringUtil.isEmpty(reamls)) {
            return;
        }

        // 用逗号分隔
        String[] realmArray = reamls.split(",");
        if (ArrayUtil.isEmpty(realmArray))
            return;

        // 使Realms有唯一性和顺序性
        Set<Realm> realmSet = new LinkedHashSet<Realm>();
        for (String realm : realmArray) {
            if (SecurityConstant.REALMS_JDBC.equalsIgnoreCase(realm)) {
                addJdbcRealm(realmSet);
            } else if (SecurityConstant.REALMS_CUSTOM.equalsIgnoreCase(realm)) {
                addCustomRealm(realmSet);
            }
        }

        // 设置realm
        if (CollectionUtil.isEmpty(realmSet))
            return;
        RealmSecurityManager realmSecurityManager = (RealmSecurityManager) webSecurityManager;
        realmSecurityManager.setRealms(realmSet);
    }

    private void addCustomRealm(Set<Realm> realmSet) {
        SmartSecurity smartSecurity = SecurityConfig.getSmartSecurity();
        SmartCustomRealm smartCustomRealm = new SmartCustomRealm(smartSecurity);
        realmSet.add(smartCustomRealm);
    }

    private void addJdbcRealm(Set<Realm> realmSet) {
        SmartJdbcRealm smartJdbcRealm = new SmartJdbcRealm();
        realmSet.add(smartJdbcRealm);
    }

    /**
     * 设置缓存
     * @param webSecurityManager
     */
    private void setCache(WebSecurityManager webSecurityManager) {
        if (!SecurityConfig.isCache())
            return;

        CachingSecurityManager cachingSecurityManager =
                (CachingSecurityManager) webSecurityManager;
        MemoryConstrainedCacheManager cacheManager = new MemoryConstrainedCacheManager();
        cachingSecurityManager.setCacheManager(cacheManager);
    }


}
