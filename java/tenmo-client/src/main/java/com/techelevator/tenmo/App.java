package com.techelevator.tenmo;

import com.techelevator.tenmo.models.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.UserService;
import com.techelevator.view.ConsoleService;
import io.cucumber.java.bs.A;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private UserService userService = new UserService();
    private int id;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory(id);
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
				//break;
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		AccountService accountService = new AccountService();
		System.out.println("*** You have $" + accountService.getBalance().getBalance() + " in your account. ***");
	}


	private void viewTransferHistory(int id) {
		try {
			AccountService accountService = new AccountService();
			TransferHistory[] list = accountService.getTransferHistory(currentUser.getUser().getId());
			for (TransferHistory transfer1 : list) {
				System.out.println("-------------------------------------------");
				System.out.printf("%-16s%-16s%-16s\n", "Transfer ID:", "Recipient:", "Amount:");  //16 indicates # of spaces between columns
				System.out.print(String.format("%-16d%-16s%-16s\n", transfer1.getTransfer_id(), transfer1.getUsername(), "$" + transfer1.getAmount()) + "\r\n");
			}
			System.out.println("-------------------------------------------");
		} catch (HttpServerErrorException e) {
			System.out.println("**** No history Found! ****");
		} catch (RestClientResponseException e) {
		System.out.println("**** No history found! ****");
		}
	}


	public void sendBucks() {
		AccountService accountService = new AccountService();
		User[] user = accountService.userList();   //is there a way to exclude current user?
		try {
			System.out.println("Enter recipient of transfer");
			while (true) {
				for (int i = 0; i < user.length; i++) {
					System.out.println(user[i]);
				}
				String userInput = console.getUserInput("Enter name of user to send money to");
				Transfer transfer = new Transfer();
				transfer.setAccount_from(currentUser.getUser().getId());
				transfer.setAccount_to(userService.getUserName(userInput).getId());
				String amount = console.getUserInput("Enter amount you wish to transfer");
				transfer.setAmount(BigDecimal.valueOf(Double.parseDouble(amount)));
				userService.makeTransfer(transfer);
				System.out.println("\n*** You have successfully sent $" + amount + " to " + userInput + ". ***");
				mainMenu();
			}
		} catch (Exception e) {
			System.out.println("\n\n**** Invalid entry, please try again. ****\n");
			sendBucks();
		}
	}


	private void requestBucks() {
		AccountService accountService = new AccountService();
		User[] user = accountService.userList();   //is there a way to exclude current user?
		try {
			System.out.println("Available Users:\n----------------");
			while (true) {
				for (int i = 0; i < user.length; i++) {
					System.out.println(user[i]);
				}
				String userInput = console.getUserInput("\nEnter name of user you would like to request from, or enter 'E' to exit to main menu");
				if (userInput.equalsIgnoreCase("e")) {
					mainMenu();
				}
				Transfer transfer = new Transfer();
				transfer.setAccount_from(currentUser.getUser().getId());
				transfer.setAccount_to(userService.getUserName(userInput).getId());
				String amount = console.getUserInput("Enter requested amount");
				transfer.setAmount(BigDecimal.valueOf(Double.parseDouble(amount)));
				userService.makeRequest(transfer);
				System.out.println("\n*** You have successfully requested $" + amount + " from " + userInput + ". ***");
				mainMenu();
			}
		} catch(Exception e){
			System.out.println("\n\n **** Invalid entry, please try again. **** \n");
			requestBucks();
		}
	}


	public void viewPendingTransfers() {
		AccountService accountService = new AccountService();
		try {
			TransferHistory[] transferList = accountService.getRequestHistory(currentUser.getUser().getId());
			for (TransferHistory transferHistory : transferList) {
				System.out.println("Transaction ID: " + transferHistory.getTransfer_id() + ", From: " + transferHistory.getUsername() + ", Amount: $" + transferHistory.getAmount());
			}
			if (transferList.length == 0) {
				System.out.println("**** No pending transfers found! ****");
			}
		} catch (HttpServerErrorException e) {
			System.out.println("**** No pending transfers found! ****");
		}
	}


	private void viewPendingRequests() {
		AccountService accountService = new AccountService();
		UserService userService = new UserService();
		viewPendingTransfers();

		try {
		TransferHistory[] transferList = accountService.getRequestHistory(currentUser.getUser().getId());
			String input = console.getUserInput("\nEnter a transfer request ID to view options");
			boolean wentIn = false;
		for (TransferHistory transferHistory : transferList) {
			//System.out.println("Transaction ID:" + transferHistory.getTransfer_id() + " " + "From: " + transferHistory.getUsername() + " " + "Amount: $" + transferHistory.getAmount());

			if (Integer.parseInt(input) == transferHistory.getTransfer_id()) {  //*
				wentIn = true;
				String acceptOrReject = console.getUserInput("Would you like to (A)ccept or (R)eject the request? Type 'E' to exit to main menu");
				if (acceptOrReject.trim().equalsIgnoreCase("A")) {
					userService.acceptTransferRequest(transferHistory);
					System.out.println("\n*** You have approved the request! You payed $" + transferHistory.getAmount() + "***\n");
					mainMenu();

				} else if (acceptOrReject.trim().equalsIgnoreCase("R")) {
					userService.declineTransferRequest(transferHistory);
					System.out.println("\n*** You have successfully rejected the request. ***\n");
					mainMenu();

				} else if (acceptOrReject.trim().equalsIgnoreCase("E")) {
					mainMenu();
				}
			}

		 } if(!wentIn) {
				System.out.println("\n**** " + input + " is not a valid option. Please try again. ****\n");

			}
	} catch (NumberFormatException e) {
				System.out.println("\n**** Please enter a valid Transfer Request ID number ****\n");
			}
	}


	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now log in.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to log in again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
