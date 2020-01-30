package com.jero.tool.filter;

import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description TODO
 * @Date 2020-01-30
 * @Author jero
 * @Version 1.0
 * @ModifyNote (add note when you modify)
 * |---modifyText:
 * |---modifyDate:
 * |---modifyAuthor:
 */
public interface JAuthFailProcessor {

    /**
     * 处理用户未登陆情况
     * @param request
     * @param response
     * @param handler
     * @return
     */
    boolean proceNoLogin(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler);

    /**
     * 处理用户权限不足情况
     * @param request
     * @param response
     * @param handler
     * @return
     */
    boolean proceNoPermission(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler);
}
