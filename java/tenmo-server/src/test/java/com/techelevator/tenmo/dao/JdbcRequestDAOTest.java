package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class JdbcRequestDAOTest {

    private static final long TEST_ID = Long.valueOf(70);
    private static final int TRANSFER_TYPE = 1;
    private static final int TRANSFER_STATUS = 1;
    private static final int ACCOUNT_FROM = 2001;
    private static final int ACCOUNT_TO = 2002;
    private static BigDecimal AMOUNT = BigDecimal.valueOf(Long.parseLong("20"));
    private static SingleConnectionDataSource dataSource;
    private JdbcRequestDAO dao;
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);


    @BeforeClass
    public static void setupDataSource() {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:8080/tenmo");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false);
    }

    @AfterClass
    public static void closeDataSource() throws SQLException {
        dataSource.destroy();
    }

    @Before
    public void setup() {
        String sqlInsert = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(sqlInsert, TRANSFER_TYPE, TRANSFER_STATUS, ACCOUNT_FROM, ACCOUNT_TO, AMOUNT);
        dao = new JdbcRequestDAO(dataSource);
    }

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }


    @Test
    public void makeRequest() {

    }

    @Test
    public void getAccountIdFromUserId() {
    }


}