package com.zovz.service.impl;

import com.zovz.domain.Account;
import com.zovz.mapper.AccountMapper;
import com.zovz.service.AccountService;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author ：zovz
 * @date ：Created in 2021/9/3 19:56
 * @description：
 * @version: $
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public void save(Account account) throws IOException {
        accountMapper.save(account);
    }

    @Override
    public List<Account> findAll() throws IOException {
        return accountMapper.findAll();
    }
}
