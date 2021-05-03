package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcRequestDAO implements RequestDAO{
    private JdbcTemplate jdbcTemplate;
    public JdbcRequestDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public void makeRequest(Transfer transfer) {
    String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount)" +
            " VALUES (1, 1, ?, ?, ?)";
    jdbcTemplate.update(sql, getAccountIdFromUserId(transfer.getAccount_from()), getAccountIdFromUserId(transfer.getAccount_to()), transfer.getAmount());
    }

    public int getAccountIdFromUserId(int userId) {
        String sql = "SELECT account_id FROM accounts WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        int result = 0;
        while (rowSet.next()) {
            result = rowSet.getInt("account_id");
        }
        return result;
    }

    private Transfer mapRowToAccount(SqlRowSet rowSet) {
        Transfer transfer = new Transfer();
        transfer.setTransfer_type_id(rowSet.getInt("transfer_type_id"));
        transfer.setTransfer_status_id(rowSet.getInt("transfer_status_id"));
        transfer.setAccount_from(rowSet.getInt("account_from"));
        transfer.setAccount_to(rowSet.getInt("account_to"));
        String balance = rowSet.getString("amount");
        BigDecimal amount = new BigDecimal(balance);
        transfer.setAmount(amount);
        return transfer;

    }
}
