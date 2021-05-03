package com.techelevator.tenmo.services;

import com.techelevator.tenmo.App;
import com.techelevator.tenmo.models.*;
import com.techelevator.view.ConsoleService;
import io.cucumber.java.bs.A;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Scanner;

public class AccountService {
    public static String AUTH_TOKEN = "";
    private static final String API_BASE_URL = "http://localhost:8080/";
    public RestTemplate restTemplate = new RestTemplate();


    public Account getBalance() {
        return restTemplate.exchange(API_BASE_URL + "balance", HttpMethod.GET, makeAuthEntity(), Account.class).getBody();
    }

    public User[] userList() {
        return restTemplate.exchange(API_BASE_URL + "user", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
    }

    public TransferHistory[] getTransferHistory(int id) {
        TransferHistory[] transfers;
        transfers = restTemplate.exchange(API_BASE_URL + "transfer/history/currentuser/" + id, HttpMethod.GET, makeAuthEntity(), TransferHistory[].class).getBody();
        return transfers;
    }

    public Transfer[] getTransferHistoryById(int id) {
        Transfer[] transfers = null;
        transfers = restTemplate.exchange(API_BASE_URL + "transfer/history/" + id, HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
        return transfers;
    }

    public TransferHistory[] getOwnTransferHistory() {
        TransferHistory[] transferHistories = null;
        transferHistories = restTemplate.exchange(API_BASE_URL + "transfer/history", HttpMethod.GET, makeAuthEntity(), TransferHistory[].class).getBody();
        return transferHistories;
    }

    public TransferHistory[] getRequestHistory(int id) {
        TransferHistory[] transferHistories = null;
        transferHistories = restTemplate.exchange(API_BASE_URL + "transfer/request/" + id, HttpMethod.GET, makeAuthEntity(), TransferHistory[].class).getBody();

        return transferHistories;
    }

    public TransferHistory getFromUserName() {
        TransferHistory transferHistory;
        transferHistory = restTemplate.exchange(API_BASE_URL + "transfer/request/username", HttpMethod.GET, makeAuthEntity(), TransferHistory.class).getBody();
        return transferHistory;
    }


    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<Account> entity = new HttpEntity<>(account, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }



}
