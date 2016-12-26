package org.smart4j.plugin.security.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.plugin.security.SecurityConstant;
import org.smart4j.plugin.security.SmartSecurity;
import org.smart4j.plugin.security.password.Md5CredentialsMatcher;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by shijiapeng on 16/12/25.
 */
public class SmartCustomRealm extends AuthorizingRealm{

    private final SmartSecurity smartSecurity;

    public SmartCustomRealm(SmartSecurity smartSecurity) {
        this.smartSecurity = smartSecurity;
        super.setName(SecurityConstant.REALMS_CUSTOM);
        super.setCredentialsMatcher(new Md5CredentialsMatcher());
    }


    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {

        if (token == null) {
            throw new AuthenticationException("parameter token is null");
        }

        // 取得用户名
        String username = ((UsernamePasswordToken) token).getUsername();
        // 通过用户取得数据库中的密码
        String password = smartSecurity.getPassword(username);
        // 把用户名和密码放到AuthenticationInfo中
        // 也可以使用下面的形式，SimpleAuthenticationInfo的构造函数内部就是下面分开写法。
        // SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username ,password, super.getName());
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo();
        info.setPrincipals(new SimplePrincipalCollection(username, super.getName()));
        info.setCredentials(password);

        return info;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        if (principals == null) {
            throw new AuthorizationException("parameter principals is null");
        }

        // 取得用户名
        String username = (String) super.getAvailablePrincipal(principals);
        // 取得角色
        Set<String> roleNameSet = smartSecurity.getRoleNameSet(username);
        // 取得权限
        Set<String> permissionSet = new HashSet<String>();
        if (!CollectionUtil.isEmpty(roleNameSet)) {
            for (String roleName : roleNameSet) {
                Set<String> currentPermissionNameSet = smartSecurity.getPermissionNameSet(roleName);
                permissionSet.addAll(currentPermissionNameSet);
            }
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roleNameSet);
        info.setStringPermissions(permissionSet);

        return info;
    }
}
