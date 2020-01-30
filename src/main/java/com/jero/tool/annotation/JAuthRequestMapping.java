package com.jero.tool.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * @Description 认证+请求处理注解
 * @Date 2020-01-16
 * @Author jero
 * @Version 1.0
 * @ModifyNote (add note when you modify)
 * |---modifyText:
 * |---modifyDate:
 * |---modifyAuthor:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@RequestMapping
@JAuth
public @interface JAuthRequestMapping {

    /**
     * 这个value值，就相当于@RequestMapping 中的value
     * @return
     */
    @AliasFor(annotation = RequestMapping.class,attribute = "value")
    String[] value() default {};
    @AliasFor(annotation = RequestMapping.class,attribute = "path")
    String[] path() default {};
    @AliasFor(annotation = JAuth.class,attribute = "roles")
    String[] roles() default {"public"};
    @AliasFor(annotation = JAuth.class,attribute = "roleGroup")
    String roleGroup() default "";

    @AliasFor(annotation = RequestMapping.class,attribute = "name")
    String name() default "";
    @AliasFor(annotation = RequestMapping.class,attribute = "method")
    RequestMethod[] method() default {};
    @AliasFor(annotation = RequestMapping.class,attribute = "params")
    String[] params() default {};
    @AliasFor(annotation = RequestMapping.class,attribute = "headers")
    String[] headers() default {};
    @AliasFor(annotation = RequestMapping.class,attribute = "consumes")
    String[] consumes() default {};
    @AliasFor(annotation = RequestMapping.class,attribute = "produces")
    String[] produces() default {};
}
