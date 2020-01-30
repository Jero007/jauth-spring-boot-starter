package com.jero.tool.datamodel;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description 获取当前用户服务类
 * @Date 2020-01-28
 * @Author jero
 * @Version 1.0
 * @ModifyNote (add note when you modify)
 * |---modifyText:
 * |---modifyDate:
 * |---modifyAuthor:
 */
public interface UserDetailService<T extends JUser> {

    T getUserDetail(HttpServletRequest request);
}
