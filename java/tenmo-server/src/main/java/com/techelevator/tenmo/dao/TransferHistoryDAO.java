package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferHistory;

import java.util.List;

public interface TransferHistoryDAO {

    List<TransferHistory> getTransferHistory(int id);

    List<TransferHistory> getCurrentTransferHistory();

    public TransferHistory getUserName();
}
