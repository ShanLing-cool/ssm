package com.zovz.controller;

import com.zovz.domain.Role;
import com.zovz.domain.User;
import com.zovz.service.RoleService;
import com.zovz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author ：zovz
 * @date ：Created in 2021/8/30 21:52
 * @description：
 * @version: $
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/login")
    public String del(String username, String password, HttpSession session) {
        User user = userService.login(username, password);
        if (user != null) {
            //登录成功，将user存入session
            session.setAttribute("user", user);
            return "redirect:/index.jsp";
        }
        return "redirect:/login.jsp";
    }

    @RequestMapping(value = "/del/{userId}")
    public String del(@PathVariable(value = "userId") Long userId) {
        userService.del(userId);
        return "redirect:/user/list";
    }

    @RequestMapping(value = "/save")
    public String save(User user, Long[] roleIds) {
        userService.save(user, roleIds);
        return "redirect:/user/list";
    }

    @RequestMapping(value = "/saveUI")
    public ModelAndView saveUI() {
        ModelAndView modelAndView = new ModelAndView();
        List<Role> roleList = roleService.list();
        modelAndView.addObject("roleList", roleList);
        modelAndView.setViewName("user-add");
        return modelAndView;
    }

    @RequestMapping(value = "/list")
    public ModelAndView list() {
        List<User> userList = userService.list();

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("userList", userList);

        modelAndView.setViewName("user-list");

        return modelAndView;
    }


}
