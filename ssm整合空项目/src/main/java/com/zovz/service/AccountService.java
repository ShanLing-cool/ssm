package com.zovz.service;

import com.zovz.domain.Account;

import java.io.IOException;
import java.util.List;

/**
 * @author ：zovz
 * @date ：Created in 2021/9/3 19:55
 * @description：
 * @version: $
 */
public interface AccountService {

    public void save(Account account) throws IOException;

    public List<Account> findAll() throws IOException;
}
