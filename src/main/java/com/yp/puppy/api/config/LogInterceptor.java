package com.yp.puppy.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        log.info("[LogInterceptor] requestUri: {}, requestMethod: {}", requestUri, requestMethod);

        return true;
    }
}
