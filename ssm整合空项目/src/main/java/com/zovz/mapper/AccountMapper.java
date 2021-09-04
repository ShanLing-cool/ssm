package com.zovz.mapper;

import com.zovz.domain.Account;

import java.util.List;

/**
 * @author ：zovz
 * @date ：Created in 2021/9/3 19:54
 * @description：
 * @version: $
 */
public interface AccountMapper {

    public void save(Account account);

    public List<Account> findAll();

}
