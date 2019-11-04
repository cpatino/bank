package com.codechallenge.bank.service;

import com.codechallenge.bank.dao.TransactionDAO;
import com.codechallenge.bank.exception.DataNotFoundException;
import com.codechallenge.bank.model.Transaction;
import com.codechallenge.bank.model.dto.AccountDto;
import com.codechallenge.bank.model.dto.TransactionDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {
  
  @Autowired private TransactionService service;
  @MockBean private AccountService accountService;
  @MockBean private TransactionDAO dao;
  
  @Test
  public void findById_notFound() {
    when(dao.findById("12345A")).thenReturn(Optional.empty());
    Optional<TransactionDto> transaction = service.findById("12345A");
    assertTrue(transaction.isEmpty());
  }
  
  @Test
  public void findById_found() {
    TransactionDto expectedTransaction = TransactionDto.builder()
      .reference("12345A")
      .account("ABC123")
      .amount(100)
      .build();
    when(dao.findById("12345A")).thenReturn(Optional.of(expectedTransaction));
    Optional<TransactionDto> transaction = service.findById("12345A");
    assertEquals(expectedTransaction, transaction.get());
  }
  
  @Test
  public void save_checkIsNewTransaction_emptyReference_newAccount_positiveBalance() {
    AccountDto account = AccountDto.builder()
      .iban("ABC123")
      .balance(100)
      .build();
    
    TransactionDto expectedTransaction = TransactionDto.builder()
      .reference("TEST")
      .account("ABC123")
      .amount(100)
      .date(LocalDateTime.now())
      .build();
    
    Transaction transaction = Transaction.builder()
      .account("ABC123")
      .amount(100)
      .build();
    when(accountService.findById("ABC123")).thenReturn(Optional.empty());
    when(accountService.save(any(AccountDto.class))).thenReturn(account);
    when(dao.findById(anyString())).thenReturn(Optional.empty());
    when(dao.save(any(TransactionDto.class))).thenReturn(expectedTransaction);
    service.save(transaction);
  }
  
  @Test(expected = ResponseStatusException.class)
  public void save_checkIsNewTransaction_emptyReference_newAccount_negativeBalance() {
    Transaction transaction = Transaction.builder()
      .account("ABC123")
      .amount(-100)
      .build();
    when(accountService.findById("ABC123")).thenReturn(Optional.empty());
    service.save(transaction);
    fail("Expecting invalid parameter exception");
  }
  
  @Test(expected = ResponseStatusException.class)
  public void save_checkIsNewTransaction_notEmptyUsedReference() {
    TransactionDto expectedTransaction = TransactionDto.builder()
      .reference("12345A")
      .account("ABC123")
      .amount(100)
      .build();
    Transaction transaction = Transaction.builder()
      .reference("12345A")
      .account("ABC123")
      .amount(100)
      .build();
    when(dao.findById("12345A")).thenReturn(Optional.of(expectedTransaction));
    service.save(transaction);
    fail("Expecting invalid parameter exception");
  }
  
  @Test(expected = ResponseStatusException.class)
  public void save_checkBalance_oldAccount_negativeBalance() {
    Transaction transaction = Transaction.builder()
      .reference("12345A")
      .account("ABC123")
      .amount(-100)
      .build();
    
    AccountDto account = AccountDto.builder()
      .iban("ABC123")
      .transactions(Collections.singletonList(TransactionDto.builder().amount(50).build()))
      .balance(50)
      .build();
    
    when(dao.findById("12345A")).thenReturn(Optional.empty());
    when(accountService.findById("ABC123")).thenReturn(Optional.of(account));
    service.save(transaction);
    fail("Expecting invalid parameter exception");
  }
  
  @Test
  public void save_checkBalance_newAccount_positiveBalance() {
    Transaction transaction = Transaction.builder()
      .account("ABC123")
      .amount(100)
      .build();
  
    TransactionDto transactionDto = TransactionDto.builder(transaction)
      .build();
  
    AccountDto account = AccountDto.builder()
      .iban("ABC123")
      .transactions(Collections.singletonList(TransactionDto.builder().amount(50).build()))
      .balance(50)
      .build();
    
    when(accountService.findById("ABC123")).thenReturn(Optional.empty());
    when(accountService.save(any(AccountDto.class))).thenReturn(account);
    when(dao.save(any(TransactionDto.class))).thenReturn(transactionDto);
    try {
      service.save(transaction);
    } catch (ResponseStatusException ex) {
      fail("Not expecting invalid parameter exception");
    }
  }
  
  @Test
  public void save_checkBalance_oldAccount_positiveBalance() {
    Transaction transaction = Transaction.builder()
      .reference("12345A")
      .account("ABC123")
      .amount(-40)
      .build();
    
    TransactionDto transactionDto = TransactionDto.builder(transaction)
      .build();
    
    AccountDto account = AccountDto.builder()
      .iban("ABC123")
      .transactions(Collections.singletonList(TransactionDto.builder().amount(50).build()))
      .balance(50)
      .build();
    
    when(dao.findById("12345A")).thenReturn(Optional.empty());
    when(dao.save(any(TransactionDto.class))).thenReturn(transactionDto);
    when(accountService.findById("ABC123")).thenReturn(Optional.of(account));
    when(accountService.save(any(AccountDto.class))).thenReturn(account);
    try {
      service.save(transaction);
    } catch (ResponseStatusException ex) {
      fail("Not expecting invalid parameter exception");
    }
  }
  
  @Test(expected = DataNotFoundException.class)
  public void findTransactionsById_notFound() {
    when(accountService.findById("ABC123")).thenReturn(Optional.empty());
    service.findAll("ABC123", null);
    fail("DataNotFoundException was expected");
  }
  
  @Test
  public void findTransactionsById_found_notSorted() {
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
    AccountDto expectedAccount = AccountDto.builder().iban("ABC123").transactions(expectedTransactions).build();
    when(accountService.findById("ABC123")).thenReturn(Optional.of(expectedAccount));
    when(dao.findByAccount(expectedAccount)).thenReturn(expectedTransactions);
    
    List<TransactionDto> transactions = service.findAll("ABC123", null);
    assertEquals(expectedTransaction1, transactions.get(0));
    assertEquals(expectedTransaction2, transactions.get(1));
  }
  
  @Test
  public void findTransactionsById_found_SortedAsc() {
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
    AccountDto expectedAccount = AccountDto.builder().iban("ABC123").transactions(expectedTransactions).build();
    when(accountService.findById("ABC123")).thenReturn(Optional.of(expectedAccount));
    when(dao.findByAccount(expectedAccount, Sort.by(Sort.Direction.ASC, "amount")))
      .thenReturn(Arrays.asList(expectedTransaction2, expectedTransaction1));
    
    List<TransactionDto> transactions = service.findAll("ABC123", "ASC");
    assertEquals(expectedTransaction2, transactions.get(0));
    assertEquals(expectedTransaction1, transactions.get(1));
  }
  
  @Test
  public void findTransactionsById_found_SortedDesc() {
    TransactionDto expectedTransaction1 = TransactionDto.builder()
      .reference("123A")
      .account("ABC123")
      .amount(90)
      .build();
    
    TransactionDto expectedTransaction2 = TransactionDto.builder()
      .reference("123B")
      .account("ABC123")
      .amount(100)
      .fee(10d)
      .build();
    
    List<TransactionDto> expectedTransactions = Arrays.asList(expectedTransaction1, expectedTransaction2);
    AccountDto expectedAccount = AccountDto.builder().iban("ABC123").transactions(expectedTransactions).build();
    when(accountService.findById("ABC123")).thenReturn(Optional.of(expectedAccount));
    when(dao.findByAccount(expectedAccount, Sort.by(Sort.Direction.DESC, "amount")))
      .thenReturn(Arrays.asList(expectedTransaction2, expectedTransaction1));
    
    List<TransactionDto> transactions = service.findAll("ABC123", "DESC");
    assertEquals(expectedTransaction2, transactions.get(0));
    assertEquals(expectedTransaction1, transactions.get(1));
  }
}