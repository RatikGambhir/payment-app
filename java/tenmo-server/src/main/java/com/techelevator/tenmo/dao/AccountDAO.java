package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {

    List<Account> findAll();

    Account getBalance();

    void decreaseBalance(BigDecimal transferAmount, int userId);

    void increaseBalance(BigDecimal transferAmount, int userId);
}
