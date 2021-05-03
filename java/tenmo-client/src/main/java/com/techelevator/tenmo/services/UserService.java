package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.*;
import com.techelevator.view.ConsoleService;
import org.apiguardian.api.API;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.parser.Entity;
import java.math.BigDecimal;

public class UserService {
    public static String AUTH_TOKEN = "";
    private static final String API_BASE_URL = "http://localhost:8080/";
    public RestTemplate restTemplate = new RestTemplate();
    private ConsoleService console;
    private AuthenticatedUser currentUser;

        public void increaseBalance(BigDecimal transferAmount, int userId) {
            restTemplate.put(API_BASE_URL + "balance", transferAmount, userId);
        }


        public void decreaseBalance(BigDecimal transferAmount, int userId) {
            restTemplate.put(API_BASE_URL + "request", transferAmount, userId);
        }

        public User getUserName(String userName) {
        return restTemplate.exchange(API_BASE_URL + "user/" + userName, HttpMethod.GET, makeAuthEntity(), User.class).getBody();

        }
        public Transfer makeTransfer(Transfer transfer) {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "transfer", HttpMethod.POST, makeTransferEntity(transfer), Transfer.class);
            return response.getBody();
        }

        public Transfer makeRequest(Transfer transfer) {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "transfer/request", HttpMethod.POST, makeTransferEntity(transfer), Transfer.class);
            return response.getBody();
        }




    private HttpEntity<User> makeUserEntity(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        return entity;
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
    private HttpEntity<TransferHistory> makeTransferHistoryEntity(TransferHistory transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<TransferHistory> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }

    public TransferHistory acceptTransferRequest(TransferHistory transfer) {
        ResponseEntity<TransferHistory> response = restTemplate.exchange(API_BASE_URL + "transfer/request/accepted", HttpMethod.PUT, makeTransferHistoryEntity(transfer), TransferHistory.class);
        System.out.println("Userservice");
        return response.getBody();
    }

    public TransferHistory declineTransferRequest(TransferHistory transfer) {
            ResponseEntity<TransferHistory> responseEntity = restTemplate.exchange(API_BASE_URL + "transfer/request/rejected", HttpMethod.PUT, makeTransferHistoryEntity(transfer), TransferHistory.class);
            return responseEntity.getBody();
    }



 //

}
