package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferHistory;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDAO{
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer setFromAccount(int id) {
        Transfer transfer = null;
        String sql = "SELECT account_id FROM accounts WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        while(rowSet.next()) {
             transfer = mapRowToAccount(rowSet);
        }
        return transfer;
    }

    @Override
    public Transfer setToAccount(int id) {
        Transfer transfer = null;
        String sql = "SELECT user_id, username FROM users WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        while(rowSet.next()) {
            transfer = mapRowToAccount(rowSet);
        }
        return transfer;

    }

    @Override
    public BigDecimal setAmount() {
        return null;
    }

    @Override
    public void makeTransfer(Transfer transfer) {
        // get the balance for the fromUserName account
        String sql = "SELECT balance FROM accounts WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transfer.getAccount_from());
        if (rowSet.next()){
            BigDecimal fromAcctBalance = rowSet.getBigDecimal("balance");
            if (fromAcctBalance.compareTo(transfer.getAmount()) >= 0){
                //subtract from one account and add to the other one
                String subtractFromBalance = "UPDATE accounts SET balance = balance - ? WHERE user_id = ?";
                jdbcTemplate.update(subtractFromBalance, transfer.getAmount(), transfer.getAccount_from());
                //increase balance on toUser account
                String addToBalance = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
                jdbcTemplate.update(addToBalance, transfer.getAmount(), transfer.getAccount_to());
            String insertTransfer = "INSERT INTO transfers (transfer_status_id, transfer_type_id, account_from, account_to, amount) VALUES (2, 2, ?, ?, ?)";
            jdbcTemplate.update(insertTransfer, getAccountIdFromUserId(transfer.getAccount_from()), getAccountIdFromUserId(transfer.getAccount_to()), transfer.getAmount());
            }
        }
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

    public void acceptTransferRequest(TransferHistory transfer) {
        String sql1 = "SELECT balance FROM accounts WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql1, transfer.getAccount_to());
        if (rowSet.next()) {
            BigDecimal accountBalance = rowSet.getBigDecimal("balance");
            if (accountBalance.compareTo(transfer.getAmount()) > 0) {
                String sql = "UPDATE transfers SET transfer_status_id = 2 WHERE transfer_status_id = 1 AND transfer_id = ?";
                jdbcTemplate.update(sql, transfer.getTransfer_id());
                String sql2 = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
                jdbcTemplate.update(sql2, transfer.getAmount(), getAccountIdFromUserId(transfer.getAccount_to()));
                String sql3 = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
                jdbcTemplate.update(sql3, transfer.getAmount(), getAccountIdFromUserId(transfer.getAccount_from()));
                System.out.println("Transfer is complete and request is now closed.");
            }
            else {
                System.out.println("There are not enough funds in the sender's account to complete this transfer.");
            }

        }
        System.out.println("TransferDao");
    }



    public void rejectTransferRequest(TransferHistory transfer) {
        String sql = "UPDATE transfers SET transfer_status_id = 3 WHERE transfer_status_id = 1 AND transfer_id = ?";
       jdbcTemplate.update(sql, transfer.getTransfer_id());
    }

    public List<Transfer> getTransferHistoryById(int id) {
        List<Transfer> listOfTransfers = new ArrayList<>();
        Transfer transfer = new Transfer();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfers WHERE transfer_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        while (result.next()) {
            transfer = mapRowToAccount(result);
            listOfTransfers.add(transfer);
        }
        return listOfTransfers;
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
