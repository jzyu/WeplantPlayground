package com.wohuizhong.client.app.http;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by weplant on 16/4/18.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface ACCESS_TOKEN_NONE {
    //表示此api不需要access_token参数
    boolean value() default true;
}
