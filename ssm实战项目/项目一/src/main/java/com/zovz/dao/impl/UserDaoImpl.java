package com.zovz.dao.impl;

import com.zovz.dao.UserDao;
import com.zovz.domain.User;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author ：zovz
 * @date ：Created in 2021/8/30 21:59
 * @description：
 * @version: $
 */
public class UserDaoImpl implements UserDao {

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        List<User> userList = jdbcTemplate.query("select * from sys_user", new BeanPropertyRowMapper<User>(User.class));
        return userList;
    }

    public Long save(final User user) {

        //创建PreparedStatementCreator
        PreparedStatementCreator creator = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                //使用原始jdbc来完成有个PreparedStatement的组件
                PreparedStatement preparedStatement = connection.prepareStatement("insert into sys_user values (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                preparedStatement.setObject(1, null);
                preparedStatement.setObject(2, user.getUsername());
                preparedStatement.setObject(3, user.getEmail());
                preparedStatement.setObject(4, user.getPassword());
                preparedStatement.setObject(5, user.getPhoneNum());
                return preparedStatement;
            }
        };

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(creator, keyHolder);

        //获得生成的主键
        long userId = keyHolder.getKey().longValue();

        return userId;
    }

    public void saveUserRoleRel(Long id, Long[] roleIds) {
        for (Long roleId : roleIds) {
            jdbcTemplate.update("insert into sys_user_role values (?,?)", id, roleId);
        }
    }

    public void delUserRoleRel(Long userId) {
        jdbcTemplate.update("delete from sys_user_role where userId = ?", userId);
    }

    public void delUser(Long userId) {
        jdbcTemplate.update("delete from sys_user where id = ?", userId);
    }

    public User findByUsernameAndPassword(String username, String password) throws EmptyResultDataAccessException {
        User user = jdbcTemplate.queryForObject("select * from sys_user where username=? and password=?", new BeanPropertyRowMapper<User>(User.class), username, password);
        return user;
    }

}
