package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferHistory;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {
    public Transfer setFromAccount(int id);

    public Transfer setToAccount(int id);

    public BigDecimal setAmount();

    public void makeTransfer(Transfer transfer);

    public List<Transfer> getTransferHistoryById(int id);

    public void acceptTransferRequest(TransferHistory transfer);

    public void rejectTransferRequest(TransferHistory transfer);
}
