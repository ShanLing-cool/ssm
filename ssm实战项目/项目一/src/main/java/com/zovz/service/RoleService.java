package com.zovz.service;

import com.zovz.domain.Role;

import java.util.List;

/**
 * @author ：zovz
 * @date ：Created in 2021/8/30 20:31
 * @description：
 * @version: $
 */
public interface RoleService {
    public List<Role> list();
    void save(Role role);
}
