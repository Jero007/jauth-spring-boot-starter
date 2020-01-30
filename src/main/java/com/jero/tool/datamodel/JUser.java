package com.jero.tool.datamodel;

import lombok.Data;

/**
 * @Description 用户类
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
     * 返回用户组
     * @return
     */
    String getRoleGroupName();
}
