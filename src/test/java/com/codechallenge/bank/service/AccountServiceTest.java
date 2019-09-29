package com.codechallenge.bank.service;

import com.codechallenge.bank.dao.AccountDAO;
import com.codechallenge.bank.exception.DataNotFoundException;
import com.codechallenge.bank.model.Account;
import com.codechallenge.bank.model.Transaction;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class AccountServiceTest {

    @Autowired
    private AccountService service;

    @Autowired
    private AccountDAO dao;

    @Test
    public void findById_notFound() {
        when(dao.findById("ABC123")).thenReturn(Optional.empty());
        Optional<Account> account = service.findById("ABC123");
        assertTrue(account.isEmpty());
    }

    @Test
    public void findById_found() {
        Account expectedAccount = Account.builder().iban("ABC123").build();
        when(dao.findById("ABC123")).thenReturn(Optional.of(expectedAccount));
        Optional<Account> account = service.findById("ABC123");
        assertEquals(expectedAccount, account.get());
    }

    @Test
    public void findAll_notFound() {
        when(dao.findAll()).thenReturn(Collections.EMPTY_LIST);
        List<Account> accounts = service.findAll();
        assertTrue(accounts.isEmpty());
    }

    @Test
    public void findAll_found() {
        Account expectedAccount = Account.builder().iban("ABC123").build();
        List<Account> expectedAccounts = Collections.singletonList(expectedAccount);
        when(dao.findAll()).thenReturn(expectedAccounts);
        List<Account> accounts = service.findAll();
        assertEquals(expectedAccounts, accounts);
        assertEquals(expectedAccount, accounts.get(0));
    }

    @Test(expected = DataNotFoundException.class)
    public void findTransactionsById_notFound() {
        when(dao.findById("ABC123")).thenReturn(Optional.empty());
        service.findTransactionsById("ABC123", null);
        fail("DataNotFoundException was expected");
    }

    @Test
    public void findTransactionsById_found_notSorted() {
        Transaction expectedTransaction1 = Transaction.builder()
                .reference("123A")
                .account("ABC123")
                .amount(100)
                .build();

        Transaction expectedTransaction2 = Transaction.builder()
                .reference("123B")
                .account("ABC123")
                .amount(90)
                .fee(10d)
                .build();

        List<Transaction> expectedTransactions = Arrays.asList(expectedTransaction1, expectedTransaction2);
        Account expectedAccount = Account.builder().iban("ABC123").transactions(expectedTransactions).build();
        when(dao.findById("ABC123")).thenReturn(Optional.of(expectedAccount));

        List<Transaction> transactions = service.findTransactionsById("ABC123", null);
        assertEquals(expectedTransaction1, transactions.get(0));
        assertEquals(expectedTransaction2, transactions.get(1));
    }

    @Test
    public void findTransactionsById_found_SortedAsc() {
        Transaction expectedTransaction1 = Transaction.builder()
                .reference("123A")
                .account("ABC123")
                .amount(100)
                .build();

        Transaction expectedTransaction2 = Transaction.builder()
                .reference("123B")
                .account("ABC123")
                .amount(90)
                .fee(10d)
                .build();

        List<Transaction> expectedTransactions = Arrays.asList(expectedTransaction1, expectedTransaction2);
        Account expectedAccount = Account.builder().iban("ABC123").transactions(expectedTransactions).build();
        when(dao.findById("ABC123")).thenReturn(Optional.of(expectedAccount));

        List<Transaction> transactions = service.findTransactionsById("ABC123", "ASC");
        assertEquals(expectedTransaction2, transactions.get(0));
        assertEquals(expectedTransaction1, transactions.get(1));
    }

    @Test
    public void findTransactionsById_found_SortedDesc() {
        Transaction expectedTransaction1 = Transaction.builder()
                .reference("123A")
                .account("ABC123")
                .amount(90)
                .build();

        Transaction expectedTransaction2 = Transaction.builder()
                .reference("123B")
                .account("ABC123")
                .amount(100)
                .fee(10d)
                .build();

        List<Transaction> expectedTransactions = Arrays.asList(expectedTransaction1, expectedTransaction2);
        Account expectedAccount = Account.builder().iban("ABC123").transactions(expectedTransactions).build();
        when(dao.findById("ABC123")).thenReturn(Optional.of(expectedAccount));

        List<Transaction> transactions = service.findTransactionsById("ABC123", "DESC");
        assertEquals(expectedTransaction2, transactions.get(0));
        assertEquals(expectedTransaction1, transactions.get(1));
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
        public AccountService accountService() {
            return new AccountService();
        }
    }
}