package com.jero.tool.datamodel;

import java.util.Set;

/**
 * @Description 用户类
 *              角色组RoleGroupName，角色集合Roles 要至少设置一个
 * @Date 2020-01-16
 * @Author jero
 * @Version 1.0
 * @ModifyNote (add note when you modify)
 * |---modifyText:
 * |---modifyDate:
 * |---modifyAuthor:
 */
public interface JUser {
    /**
     * 返回用户唯一标识
     * @return
     */
    String getId();

    /**
     * 返回角色组名称
     * @return
     */
    String getRoleGroupName();

    /**
     * 返回拥有的所有角色
     * @return
     */
    Set<String> getRoles();
}
