package com.zovz.dao;

import com.zovz.domain.User;

import java.util.List;

/**
 * @author ：zovz
 * @date ：Created in 2021/8/30 21:58
 * @description：
 * @version: $
 */
public interface UserDao {
    List<User> findAll();

    Long save(User user);

    void saveUserRoleRel(Long id, Long[] roleIds);

    void delUserRoleRel(Long userId);

    void delUser(Long userId);

    User findByUsernameAndPassword(String username, String password);
}
