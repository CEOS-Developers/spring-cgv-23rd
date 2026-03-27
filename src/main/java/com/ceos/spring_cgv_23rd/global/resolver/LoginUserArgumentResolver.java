package com.ceos.spring_cgv_23rd.global.resolver;

import com.ceos.spring_cgv_23rd.global.annotation.LoginUser;
import com.ceos.spring_cgv_23rd.global.apiPayload.code.GeneralErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import com.ceos.spring_cgv_23rd.global.security.AuthUserDetails;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(LoginUser.class);
        boolean isLongType = Long.class.isAssignableFrom(parameter.getParameterType());

        // @LoginUser가 붙어있고, 타입이 Long일 때만 작동
        return hasAnnotation && isLongType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AuthUserDetails userDetails)) {
            throw new GeneralException(GeneralErrorCode.MISSING_AUTH_INFO);
        }

        return userDetails.getUserId();
    }
}
