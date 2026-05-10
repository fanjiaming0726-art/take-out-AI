package com.example.fjm0313_takeout_self.config;

import com.example.common.annotation.LoginRequired;
import com.example.common.result.Result;
import com.example.common.context.UserContext;
import com.example.fjm0313_takeout_self.service.BlacklistService;
import com.example.fjm0313_takeout_self.service.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        LoginRequired annotation = handlerMethod.getMethodAnnotation(LoginRequired.class);

        if (annotation == null) {
            return true;
        }

        String type = annotation.value();

        if ("CUSTOMER".equals(type)) {
            Long userId = (Long) request.getSession().getAttribute("userId");
            if (userId == null) {
                writeError(response, "用户未登录", 401);
                return false;
            }

            // 黑名单检查
            if (blacklistService.isBlacklisted(userId)) {
                writeError(response, "您的账号已被限制使用", 403);
                return false;
            }

            // 限流检查：每个用户每秒最多5次请求
            if (!rateLimitService.isAllowed(userId, 5, 1)) {
                writeError(response, "请求过于频繁，请稍后再试", 429);
                return false;
            }

            UserContext.setUserId(userId);

        } else if ("EMPLOYEE".equals(type)) {
            Long employeeId = (Long) request.getSession().getAttribute("employeeId");
            if (employeeId == null) {
                writeError(response, "员工未登录", 401);
                return false;
            }
            UserContext.setEmployeeId(employeeId);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeError(HttpServletResponse response, String msg, int status) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(status);
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(msg)));
    }
}