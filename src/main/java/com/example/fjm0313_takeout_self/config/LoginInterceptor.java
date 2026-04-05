package com.example.fjm0313_takeout_self.config;


import com.example.fjm0313_takeout_self.common.LoginRequired;
import com.example.fjm0313_takeout_self.common.Result;
import com.example.fjm0313_takeout_self.common.UserContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod handlerMethod)){
            return true;
        }
        LoginRequired annotation = handlerMethod.getMethodAnnotation(LoginRequired.class);

        if(annotation == null){
            return true;
        }
        String type = annotation.value();

        if(type.equals("CUSTOMER")){
            Long userId = (Long) request.getSession().getAttribute("userId");
            if(userId == null){
                writeError(response,"用户未登录");
                return false;
            }
            UserContext.setUserId(userId);
        }else{
            Long employeeId = (Long) request.getSession().getAttribute("employeeId");
            if(employeeId == null){
                writeError(response,"商家未登录");
                return false;
            }
            UserContext.setEmployeeId(employeeId);
        }
        return true;

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,Exception ex) throws Exception {
        UserContext.clear();

    }

    private void writeError(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(msg)));
    }
}
