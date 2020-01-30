package com.jero.tool.datamodel;

import java.util.Set;

/**
 * @Description 角色集合
 * @Date 2020-01-16
 * @Author jero
 * @Version 1.0
 * @ModifyNote (add note when you modify)
 * |---modifyText:
 * |---modifyDate:
 * |---modifyAuthor:
 */

public interface JRoleGroup {

    /**
     * 获取用户组名称
     * @return
     */
    String getName();

    /**
     * 添加角色
     * @param role
     */
    void add(String role);

    /**
     * 返回全部角色
     * @return 角色集合
     */
    Set<String> getAll();

    /**
     * 删除指定角色
     * @param role
     * @return 删除的角色
     */
    void delete(String role);

    /**
     * 当前角色组是否存在otherRoleGroup中的任意一个role
     * @param otherRoleGroup
     * @return
     */
    boolean contain(JRoleGroup otherRoleGroup);

    /**
     * 当前角色组是否存在指定的otherRole
     * @param otherRole
     * @return
     */
    boolean contain(String otherRole);
}
