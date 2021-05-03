package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferHistoryDAO implements TransferHistoryDAO {
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferHistoryDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<TransferHistory> getTransferHistory(int id) {
        int accountID = 0;
        String accountSql = "SELECT account_id FROM accounts WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(accountSql, id);
        while(rowSet.next()) {
            accountID = rowSet.getInt("account_id");
        }
        List<TransferHistory> listOfTransfers = new ArrayList<>();
        TransferHistory transfer = new TransferHistory();
        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, username, amount FROM transfers " +
                "JOIN accounts ON accounts.account_id = transfers.account_to " +
                "JOIN users ON accounts.user_id = users.user_id " +
                "JOIN transfer_types ON transfers.transfer_type_id = transfer_types.transfer_type_id " +
                "JOIN transfer_statuses ON transfer_statuses.transfer_status_id = transfers.transfer_status_id WHERE account_from = ? OR account_to = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, accountID, accountID);
        while (result.next()) {
            transfer = mapRowToTransferHistory(result);
            listOfTransfers.add(transfer);
        }
        return listOfTransfers;
    }


    @Override
    public List<TransferHistory> getCurrentTransferHistory() {
        List<TransferHistory> listOfTransfers = new ArrayList<>();
        TransferHistory transfer = new TransferHistory();
        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, username, amount FROM transfers " +
                "JOIN accounts ON accounts.account_id = transfers.account_to " +
                "JOIN users ON accounts.user_id = users.user_id " +
                "JOIN transfer_types ON transfers.transfer_type_id = transfer_types.transfer_type_id " +
                "JOIN transfer_statuses ON transfer_statuses.transfer_status_id = transfers.transfer_status_id";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            transfer = mapRowToTransferHistory(result);
            listOfTransfers.add(transfer);
        }
        return listOfTransfers;
    }
    public TransferHistory getUserName() {
        String sql = "SELECT username FROM users JOIN accounts ON accounts.user_id = users.user_id " +
                "JOIN transfers ON transfers.account_from = accounts.account_id";
        TransferHistory transferHistory = new TransferHistory();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            transferHistory = mapRowToTransferHistory(rowSet);
        }
        return transferHistory;
    }

    private TransferHistory mapRowToTransferHistory(SqlRowSet rowSet) {
                TransferHistory transferHistory = new TransferHistory();
                transferHistory.setTransfer_id(rowSet.getInt("transfer_id"));
                transferHistory.setTransfer_type_desc(rowSet.getString("transfer_type_desc"));
                transferHistory.setTransfer_status_desc(rowSet.getString("transfer_status_desc"));
                transferHistory.setUsername(rowSet.getString("username"));
               String balance = rowSet.getString("amount");
               BigDecimal bd = new BigDecimal(balance);
               transferHistory.setAmount(bd);
        //       transferHistory.setAccount_from(rowSet.getInt("account_from"));
          //     transferHistory.setAccount_to(rowSet.getInt("account_to"));
            //   transferHistory.setUser_id(rowSet.getInt("user_id"));
               return transferHistory;


    }
}
