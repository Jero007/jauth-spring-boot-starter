package com.jero.tool.datamodel;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Description 默认实现，主要用于判断当前用户角色是否可以执行当前交易
 * @Date 2020-01-27
 * @Author jero
 * @Version 1.0
 * @ModifyNote (add note when you modify)
 * |---modifyText:
 * |---modifyDate:
 * |---modifyAuthor:
 */

public abstract class AbstractJRoleGroup implements JRoleGroup {


    private String groupName;

    private Set<String> roles = new LinkedHashSet<String>();

    /*public AbstractJRoleGroup(String groupName) {
        Assert.notNull(groupName,"groupName must not be null");
        this.groupName = groupName;
    }
    public AbstractJRoleGroup(String groupName, String... roless) {
        Assert.notNull(groupName,"groupName must not be null");
        this.groupName = groupName;
        for (String role: roless) {
            roles.add(role);
        }
    }

    public AbstractJRoleGroup(String groupName, Set<String> roless) {
        Assert.notNull(groupName,"groupName must not be null");
        this.groupName = groupName;
        this.roles.addAll(roless);
    }*/

    @Override
    public String getName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public void add(String role) {
        roles.add(role);
    }

    @Override
    public Set<String> getAll() {
        return roles;
    }

    @Override
    public void delete(String role) {
        this.roles.remove(role);
    }

    @Override
    public boolean contain(JRoleGroup otherRoleGroup) {
        while (otherRoleGroup.getAll().iterator().hasNext()) {
            if (this.roles.contains(otherRoleGroup.getAll().iterator().next())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contain(String otherRole) {
        return this.roles.contains(otherRole);
    }
}
