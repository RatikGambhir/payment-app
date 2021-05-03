package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferHistory;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@RestController
public class AccountController {

    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private TransferDAO transferDAO;
    @Autowired
    private TransferHistoryDAO transferHistoryDAO;
    @Autowired
    private RequestDAO requestDAO;
    @Autowired
    private RequestHistoryDAO requestHistoryDAO;

    @RequestMapping(path = "balance", method = RequestMethod.GET)
       public Account getBalance() {
        Account account;
        account = accountDAO.getBalance();
        return account;
    }

    @RequestMapping(path = "user", method = RequestMethod.GET)
        public List<User> listAllUser() {
         return userDAO.findAll();
    }



    @RequestMapping(path = "user/{name}", method = RequestMethod.GET)
    public User getUserByName(@PathVariable String name)
    {
        System.out.println(name);
        User user;
         user = userDAO.findByUsername(name);
         return user;
    }
    @RequestMapping(path = "transfer", method = RequestMethod.POST)
    public void makeTransfer(@RequestBody Transfer transfer, Principal principal) {
        transferDAO.makeTransfer(transfer);
    }
    @RequestMapping(path = "transfer/history/currentuser/{id}", method = RequestMethod.GET)
    public List<TransferHistory> getAllTransfers(@PathVariable int id) {
        List<TransferHistory> dd = new ArrayList<>();
        dd = transferHistoryDAO.getTransferHistory(id);
        System.out.println(dd.get(0).getAmount());
        return dd;
    }

    @RequestMapping(path = "transfer/history/{id}", method = RequestMethod.GET)
    public List<Transfer> getTransferHistoryById(@PathVariable int id) {
        return transferDAO.getTransferHistoryById(id);
    }

    @RequestMapping(path = "transfer/request", method = RequestMethod.POST)
    public void makeRequest(@RequestBody Transfer transfer, Principal principal) {
        requestDAO.makeRequest(transfer);
    }

    @RequestMapping(path = "transfer/request/{id}", method = RequestMethod.GET)
    public List<TransferHistory> getTransferRequests(@PathVariable int id) {
        List<TransferHistory> history = new ArrayList<>();
        history = requestHistoryDAO.getRequestHistory(id);
        return history;
    }

    @RequestMapping(path = "transfer/request/accepted", method = RequestMethod.PUT)
    public void acceptTransferRequest(@RequestBody TransferHistory transfer) {
        transferDAO.acceptTransferRequest(transfer);
        System.out.println("UserService");
    }

    @RequestMapping(path = "transfer/request/rejected", method = RequestMethod.PUT)
    public void rejectTransferRequest(@RequestBody TransferHistory transfer) {
        transferDAO.rejectTransferRequest(transfer);
    }
    @RequestMapping(path = "transfer/request/username", method = RequestMethod.GET)
    public TransferHistory getFromUserName() {
        TransferHistory transferHistory;
        transferHistory = transferHistoryDAO.getUserName();
        return transferHistory;
    }


}
