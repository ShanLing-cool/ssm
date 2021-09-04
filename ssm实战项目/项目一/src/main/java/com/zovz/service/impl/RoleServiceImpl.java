package com.zovz.service.impl;

import com.zovz.dao.RoleDao;
import com.zovz.domain.Role;
import com.zovz.service.RoleService;

import java.util.List;

/**
 * @author ：zovz
 * @date ：Created in 2021/8/30 20:32
 * @description：
 * @version: $
 */
public class RoleServiceImpl implements RoleService {

    private RoleDao roleDao;

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public List<Role> list() {
        List<Role> roleList = roleDao.findAll();
        return roleList;
    }

    public void save(Role role) {
        roleDao.save(role);
    }
}
