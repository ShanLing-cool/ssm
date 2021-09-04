package com.zovz.service.impl;

import com.zovz.dao.RoleDao;
import com.zovz.dao.UserDao;
import com.zovz.domain.Role;
import com.zovz.domain.User;
import com.zovz.service.UserService;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

/**
 * @author ：zovz
 * @date ：Created in 2021/8/30 21:57
 * @description：
 * @version: $
 */
public class UserServiceImpl implements UserService {

    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    private RoleDao roleDao;

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public List<User> list() {
        //封装userlist中的每一个User的roles数据
        List<User> userList = userDao.findAll();
        for (User user : userList) {
            //获得user的id
            Long id = user.getId();
            //将id作为参数查询当前userid对象的role的集合数据
            List<Role> roles = roleDao.findRoleById(id);
            user.setRoles(roles);
        }


        return userList;
    }

    public void save(User user, Long[] roleIds) {
        //1.向user表中存储user数据
        Long userId = userDao.save(user);

        //2.向user_role关系表中存储多条数据
        userDao.saveUserRoleRel(userId, roleIds);
    }

    public void del(Long userId) {
        //1.删除sys_user_role关系表
        userDao.delUserRoleRel(userId);

        //2.删除sys_user表
        userDao.delUser(userId);
    }

    public User login(String username, String password) {
        try {
            User user = userDao.findByUsernameAndPassword(username, password);
            return user;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}