package com.hmall.common.interceptors;

import cn.hutool.Hutool;
import cn.hutool.core.util.StrUtil;
import com.hmall.common.utils.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @param
 * @return
 */
public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1获取登陆用户信息
        String userinfo = request.getHeader("user-info");

        //2判断是否获取了用户，如果有，就存入ThreadLocal
        if (StrUtil.isNotBlank(userinfo)){
            UserContext.setUser(Long.valueOf(userinfo));
        }
        //3放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清理用户
        UserContext.removeUser();
    }
}
