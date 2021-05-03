package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDAO implements AccountDAO{
    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Account> findAll() {
        return null;
    }


    @Override
    public Account getBalance() {
        Account account = new Account();
        String sql = "SELECT * FROM accounts";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
       while (rowSet.next()) {
           account = mapRowToAccount(rowSet);
       }
       return account;
    }

    @Override
    public void decreaseBalance(BigDecimal transferAmount, int userId) {
        String sql = "UPDATE accounts SET balance = balance - ? WHERE user_id = ?";
        jdbcTemplate.update(sql, transferAmount, userId);
    }

    @Override
    public void increaseBalance(BigDecimal transferAmount, int userId) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
        jdbcTemplate.update(sql, transferAmount, userId);
    }

    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccount_id(rowSet.getInt("account_id"));
        account.setUser_id(rowSet.getInt("user_id"));
        String balanceStr = rowSet.getString("balance");
        BigDecimal balanceBD = new BigDecimal(balanceStr);
        account.setBalance(balanceBD);
        return account;
    }

}
