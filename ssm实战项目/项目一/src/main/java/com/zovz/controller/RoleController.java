package com.zovz.controller;

import com.zovz.domain.Role;
import com.zovz.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author ：zovz
 * @date ：Created in 2021/8/30 20:23
 * @description：
 * @version: $
 */

@RequestMapping(value = "/role")
@Controller
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/save")
    public String save(Role role) {
        roleService.save(role);
        return "redirect:/role/list";
    }

    @RequestMapping(value = "/list")
    public ModelAndView list() {
        ModelAndView modelAndView = new ModelAndView();
        List<Role> roleList = roleService.list();
        //设置模型
        modelAndView.addObject("roleList", roleList);
        //设置视图
        modelAndView.setViewName("role-list");

        return modelAndView;
    }
}
