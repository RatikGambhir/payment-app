package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferHistory;

import java.util.List;

public interface RequestHistoryDAO {

    public List<TransferHistory> getRequestHistory(int id);
}
