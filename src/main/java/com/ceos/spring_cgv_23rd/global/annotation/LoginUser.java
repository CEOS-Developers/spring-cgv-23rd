package com.ceos.spring_cgv_23rd.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 파라미터에만 붙일 수 있도록 설정
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지되도록 설정
public @interface LoginUser {
}
