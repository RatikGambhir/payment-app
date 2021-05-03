package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferHistory;

import java.util.List;

public interface RequestDAO {

    public void makeRequest(Transfer transfer);

}
