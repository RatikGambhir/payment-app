package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcRequestHistoryDAO implements RequestHistoryDAO {

    JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public JdbcRequestHistoryDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<TransferHistory> getRequestHistory(int id) {
        int accountID = 0;
        String accountSql = "SELECT account_id FROM accounts WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(accountSql, id);
        while (rowSet.next()) {
            accountID = rowSet.getInt("account_id");
        }
        int transferStatusId = 0;
        String transferStatusSql = "SELECT transfer_status_id FROM transfer_statuses WHERE transfer_status_desc = 'Pending'";
        SqlRowSet rowSet1 = jdbcTemplate.queryForRowSet(transferStatusSql);
        while (rowSet1.next()) {
            transferStatusId = rowSet1.getInt("transfer_status_id");
        }
        List<TransferHistory> listOfTransfers = new ArrayList<>();
        TransferHistory transfer = new TransferHistory();
        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, username, amount, account_from, account_to, users.user_id FROM transfers " +
                "JOIN accounts ON accounts.account_id = transfers.account_to " +
                "JOIN users ON accounts.user_id = users.user_id " +
                "JOIN transfer_types ON transfers.transfer_type_id = transfer_types.transfer_type_id " +
                "JOIN transfer_statuses ON transfer_statuses.transfer_status_id = transfers.transfer_status_id WHERE account_to = ? AND transfer_statuses.transfer_status_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, accountID, transferStatusId);
        while (result.next()) {
            transfer = mapRowToTransferHistory(result);
            listOfTransfers.add(transfer);
        }

        return listOfTransfers;
    }

    private TransferHistory mapRowToTransferHistory(SqlRowSet rowSet) {
        TransferHistory transferHistory = new TransferHistory();
        transferHistory.setUser_id(rowSet.getInt("user_id"));
        transferHistory.setTransfer_id(rowSet.getInt("transfer_id"));
        transferHistory.setTransfer_type_desc(rowSet.getString("transfer_type_desc"));
        transferHistory.setTransfer_status_desc(rowSet.getString("transfer_status_desc"));
        transferHistory.setUsername(rowSet.getString("username"));
        String balance = rowSet.getString("amount");
        BigDecimal bd = new BigDecimal(balance);
        transferHistory.setAmount(bd);
        transferHistory.setAccount_from(rowSet.getInt("account_from"));
        transferHistory.setAccount_to(rowSet.getInt("account_to"));
        return transferHistory;
    }
}

