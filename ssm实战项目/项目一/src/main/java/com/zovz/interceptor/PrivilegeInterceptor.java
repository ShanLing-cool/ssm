package com.zovz.interceptor;

import com.zovz.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ：zovz
 * @date ：Created in 2021/8/31 16:50
 * @description：
 * @version: $
 */
public class PrivilegeInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断session中有没有user，如果有放行，如果没有，跳转到登录页面
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            //没有登录
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        } else {
            //放行，访问目标资源
            return true;
        }
    }


}
