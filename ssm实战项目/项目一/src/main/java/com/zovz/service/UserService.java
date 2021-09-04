package com.zovz.service;

import com.zovz.domain.User;

import java.util.List;

/**
 * @author ：zovz
 * @date ：Created in 2021/8/30 21:56
 * @description：
 * @version: $
 */
public interface UserService {
    List<User> list();

    void save(User user, Long[] roleIds);

    void del(Long userId);

    User login(String username, String password);
}
