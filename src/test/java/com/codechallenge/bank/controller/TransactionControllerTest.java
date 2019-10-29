package com.codechallenge.bank.controller;

import com.codechallenge.bank.dao.AccountDAO;
import com.codechallenge.bank.dao.TransactionDAO;
import com.codechallenge.bank.model.Transaction;
import com.codechallenge.bank.model.dto.TransactionDto;
import com.codechallenge.bank.service.AccountService;
import com.codechallenge.bank.service.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * @author Carlos Rodriguez
 * @since 25/09/2019
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class TransactionControllerTest {

    @Autowired
    private TransactionController controller;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Test
    public void create_validMessage() {
        Transaction transaction = Transaction.builder()
                .account("ABC123")
                .amount(190.38)
                .build();
        try {
            controller.create(transaction);
            verify(transactionService, times(1)).save(transaction);
        } catch (ResponseStatusException ex) {
            fail("Expecting invalidParameterException was expected");
        }
    }

    @Test
    public void findAll_noData() {
        when(transactionService.findAll()).thenReturn(Collections.emptyList());
        List<TransactionDto> transactions = controller.findAll();
        assertEquals(Collections.emptyList(), transactions);
    }

    @Test
    public void findAll_transactionsInOneAccount() {
        TransactionDto expectedTransaction1 = TransactionDto.builder()
                .reference("123A")
                .account("ABC123")
                .amount(100)
                .build();

        TransactionDto expectedTransaction2 = TransactionDto.builder()
                .reference("123B")
                .account("ABC123")
                .amount(90)
                .fee(10d)
                .build();

        List<TransactionDto> expectedTransactions = Arrays.asList(expectedTransaction1, expectedTransaction2);
        when(transactionService.findAll()).thenReturn(expectedTransactions);
        List<TransactionDto> transactions = controller.findAll();
        assertEquals(expectedTransactions, transactions);
        assertEquals(expectedTransaction1, transactions.get(0));
        assertEquals(expectedTransaction2, transactions.get(1));
    }

    @Test
    public void findAll_transactionsInMultipleAccounts() {
        TransactionDto expectedTransaction1 = TransactionDto.builder()
                .reference("123A")
                .account("ABC123")
                .amount(100)
                .build();

        TransactionDto expectedTransaction2 = TransactionDto.builder()
                .reference("123B")
                .account("ABC123")
                .amount(90)
                .fee(10d)
                .build();

        List<TransactionDto> expectedTransactions = Arrays.asList(expectedTransaction1, expectedTransaction2);
        when(transactionService.findAll()).thenReturn(expectedTransactions);
        List<TransactionDto> transactions = controller.findAll();
        assertEquals(expectedTransactions, transactions);
        assertEquals(expectedTransaction1, transactions.get(0));
        assertEquals(expectedTransaction2, transactions.get(1));
    }

    @Test
    public void findAll_iban() {
        TransactionDto expectedTransaction1 = TransactionDto.builder()
                .reference("123A")
                .account("ABC123")
                .amount(100)
                .build();

        TransactionDto expectedTransaction2 = TransactionDto.builder()
                .reference("123B")
                .account("ABC123")
                .amount(90)
                .fee(10d)
                .build();

        List<TransactionDto> expectedTransactions = Arrays.asList(expectedTransaction1, expectedTransaction2);
        when(transactionService.findAll("ABC123", null)).thenReturn(expectedTransactions);
        List<TransactionDto> transactions = controller.findAll("ABC123", null);
        assertEquals(expectedTransactions, transactions);
    }

    @Profile("test")
    @Configuration
    public static class SpringConfiguration {

        @Bean
        @Primary
        public AccountDAO accountDAO() {
            return Mockito.mock(AccountDAO.class);
        }

        @Bean
        @Primary
        public AccountService accountService() {
            return Mockito.mock(AccountService.class);
        }

        @Bean
        @Primary
        public TransactionDAO transactionDAO() {
            return Mockito.mock(TransactionDAO.class);
        }

        @Bean
        public TransactionService transactionService() {
            return Mockito.mock(TransactionService.class);
        }

        @Bean
        public TransactionController transactionController() {
            return new TransactionController();
        }
    }
}