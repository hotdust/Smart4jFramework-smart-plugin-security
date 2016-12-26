package org.smart4j.plugin.security;

import java.util.Set;

/**
 * Created by shijiapeng on 16/12/23.
 */
public interface SmartSecurity {

    String getPassword(String username);

    Set<String> getRoleNameSet(String username);

    Set<String> getPermissionNameSet(String roleName);
}
