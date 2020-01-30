package com.jero.tool.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @Description 认证注解
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
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface JAuth {

    @AliasFor("roles")
    String[] value() default {"public"};

    @AliasFor("value")
    String[] roles() default {"public"};

    String roleGroup() default "";

}
