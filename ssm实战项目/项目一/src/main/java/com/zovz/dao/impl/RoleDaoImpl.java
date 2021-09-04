package com.zovz.dao.impl;

import com.zovz.dao.RoleDao;
import com.zovz.domain.Role;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * @author ：zovz
 * @date ：Created in 2021/8/30 20:35
 * @description：
 * @version: $
 */
public class RoleDaoImpl implements RoleDao {

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Role> findAll() {
        List<Role> roleList = jdbcTemplate.query("select * from sys_role", new BeanPropertyRowMapper<Role>(Role.class));
        return roleList;
    }

    public void save(Role role) {
        jdbcTemplate.update("insert into sys_role values(?,?,?)", null, role.getRoleName(), role.getRoleDesc());
    }

    public List<Role> findRoleById(Long id) {
        List<Role> roleList = jdbcTemplate.query("select * from sys_user_role ur,sys_role r where ur.userId = ? and r.id=ur.roleId", new BeanPropertyRowMapper<Role>(Role.class), id);
        return roleList;
    }


}
