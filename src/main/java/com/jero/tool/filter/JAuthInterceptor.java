package com.jero.tool.filter;

import com.jero.tool.annotation.JAuth;
import com.jero.tool.annotation.JAuthRequestMapping;
import com.jero.tool.datamodel.JRoleGroup;
import com.jero.tool.datamodel.JUser;
import com.jero.tool.datamodel.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Description 认证拦截器
 *              1，获取HandlerMethod
 *              2，从缓存中获取JRole[]，若存在，就比较当前用户的role；若不存在，就创建，并缓存
 * @Date 2020-01-27
 * @Author jero
 * @Version 1.0
 * @ModifyNote (add note when you modify)
 * |---modifyText:
 * |---modifyDate:
 * |---modifyAuthor:
 */
@Slf4j
public class JAuthInterceptor extends HandlerInterceptorAdapter implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private static final String DEFAULT_ROLE = "public";

    @Autowired
    private UserDetailService userDetailService;
    @Autowired(required = false)
    private JAuthFailProcessor jAuthFailProcessor;
    private final Object lock = new Object();
    private boolean used = false;
    private Map<HandlerMethod, Set<String>> roleMapping = new HashMap<>();
    private ThreadLocal<Map<HandlerMethod, Set<String>>> localRoleMapping = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //log.info("JAuthInterceptor.preHandle=====>" + request.getRequestURI());
        if (!(handler instanceof HandlerMethod)) {
            //没有对应的方法映射，忽略...
            return true;
        }
        //1,获取当前交易允许的角色组
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Set<String> allowRoles = this.checkCache(handlerMethod);
        if (allowRoles == null || allowRoles.size() == 0) {
            allowRoles = this.createRoles(handlerMethod);
        }

        //2,判断当前交易是否又权限
        return this.checkAuth(allowRoles,request,response,handlerMethod);
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //将localRoleMapping 合并到 roleMapping 中
        if (handler instanceof HandlerMethod
                && this.localRoleMapping.get() != null
                && this.localRoleMapping.get().size()>0
                && !used) {
            synchronized (lock) {
                this.used = true;
                this.roleMapping.putAll(this.localRoleMapping.get());
                this.localRoleMapping.get().clear();
                this.used = false;
            }
        }

        super.afterCompletion(request, response, handler, ex);
    }

    private boolean checkAuth(Set<String> allowRoles, HttpServletRequest request, HttpServletResponse response,HandlerMethod handlerMethod) {
        if (allowRoles == null) {
            //当前交易是开放的，不需要任何权限
            return true;
        }
        Object user = this.userDetailService.getUserDetail(request);
        if (null == user) {
            log.warn("this transaction[{}] should be login.",request.getRequestURI());
            if (this.jAuthFailProcessor == null) {
                //默认返回json
                String retMsg = "{\"code\":\"1001\",\"msg\":\"please login first\"}";
                try {
                    response.getOutputStream().write(retMsg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            } else {
                return this.jAuthFailProcessor.proceNoLogin(request, response, handlerMethod);
            }
        }
        if (allowRoles.size() == 1 && DEFAULT_ROLE.equals(allowRoles.iterator().next())) {
            return true;
        } else {
            JUser juser = (JUser) user;
            JRoleGroup userRoleGroup = (JRoleGroup)this.applicationContext.getBean(juser.getRoleGroupName());
            for (String role: allowRoles) {
                if (userRoleGroup.contain(role)) {
                    return true;
                }
            }
            log.warn("user[{}] has no permission to this transaction[{}]",juser.getId(),request.getRequestURI());
            if (this.jAuthFailProcessor == null) {
                //默认返回json
                StringBuffer sb = new StringBuffer("{\"code\":\"2001\",");
                sb.append("\"msg\":\"permission defined to the [");
                sb.append(request.getRequestURI());
                sb.append("].\"}");
                try {
                    response.getOutputStream().write(sb.toString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            } else {
                return this.jAuthFailProcessor.proceNoPermission(request, response, handlerMethod);
            }

        }
    }

    private Set<String> createRoles(HandlerMethod handlerMethod) {

        Method method = handlerMethod.getMethod();
        Class clazz = handlerMethod.getBeanType();
        Set<String> roleSet = new LinkedHashSet<>();

        JAuth clazzJAuth = AnnotatedElementUtils.findMergedAnnotation(clazz, JAuth.class);
        if (clazzJAuth != null && clazzJAuth.roles().length>0) {
            for (int i = 0; i < clazzJAuth.roles().length; i++) {
                roleSet.add(clazzJAuth.roles()[i]);
            }
        }
        if (clazzJAuth != null && !"".equals(clazzJAuth.roleGroup())) {
            JRoleGroup roleGroup = (JRoleGroup)this.applicationContext.getBean(clazzJAuth.roleGroup());
            roleSet.addAll(roleGroup.getAll());
        }
        JAuthRequestMapping clazzJAuthRequestMapping = AnnotatedElementUtils.findMergedAnnotation(clazz, JAuthRequestMapping.class);
        if (clazzJAuthRequestMapping != null && clazzJAuthRequestMapping.roles().length>0) {
            for (int i = 0; i < clazzJAuthRequestMapping.roles().length; i++) {
                roleSet.add(clazzJAuthRequestMapping.roles()[i]);
            }
        }
        if (clazzJAuthRequestMapping != null && !"".equals(clazzJAuthRequestMapping.roleGroup())) {
            JRoleGroup roleGroup = (JRoleGroup)this.applicationContext.getBean(clazzJAuthRequestMapping.roleGroup());
            roleSet.addAll(roleGroup.getAll());
        }
        JAuth methodJAuth = AnnotatedElementUtils.findMergedAnnotation(method, JAuth.class);
        if (methodJAuth != null && methodJAuth.roles().length>0) {
            for (int i = 0; i < methodJAuth.roles().length; i++) {
                roleSet.add(methodJAuth.roles()[i]);
            }
        }
        if (methodJAuth != null && !"".equals(methodJAuth.roleGroup())) {
            JRoleGroup roleGroup = (JRoleGroup)this.applicationContext.getBean(methodJAuth.roleGroup());
            roleSet.addAll(roleGroup.getAll());
        }
        JAuthRequestMapping methodJAuthRequestMapping = AnnotatedElementUtils.findMergedAnnotation(method, JAuthRequestMapping.class);
        if (methodJAuthRequestMapping != null && methodJAuthRequestMapping.roles().length>0) {
            for (int i = 0; i < methodJAuthRequestMapping.roles().length; i++) {
                roleSet.add(methodJAuthRequestMapping.roles()[i]);
            }
        }
        if (methodJAuthRequestMapping != null && !"".equals(methodJAuthRequestMapping.roleGroup())) {
            JRoleGroup roleGroup = (JRoleGroup)this.applicationContext.getBean(methodJAuthRequestMapping.roleGroup());
            roleSet.addAll(roleGroup.getAll());
        }

        if (this.localRoleMapping.get() == null) {
            this.localRoleMapping.set(new HashMap<>(8));
        }
        this.localRoleMapping.get().put(handlerMethod, roleSet.size() > 0 ? roleSet : null);

        return roleSet.size() > 0 ? roleSet : null;
    }

    private Set<String> checkCache(HandlerMethod handlerMethod) {

        if (this.localRoleMapping.get() != null && this.localRoleMapping.get().get(handlerMethod) != null) {
            return this.localRoleMapping.get().get(handlerMethod);
        }
        if (this.roleMapping.get(handlerMethod) != null ) {
            return this.roleMapping.get(handlerMethod);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
