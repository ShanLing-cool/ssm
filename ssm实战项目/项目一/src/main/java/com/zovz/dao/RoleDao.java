package com.zovz.dao;

import com.zovz.domain.Role;

import java.util.List;

/**
 * @author ：zovz
 * @date ：Created in 2021/8/30 20:35
 * @description：
 * @version: $
 */
public interface RoleDao {
    List<Role> findAll();

    void save(Role role);

    List<Role> findRoleById(Long id)    ;
}
