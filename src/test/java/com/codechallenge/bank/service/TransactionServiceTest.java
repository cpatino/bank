package com.codechallenge.bank.service;

import com.codechallenge.bank.dao.AccountDAO;
import com.codechallenge.bank.dao.TransactionDAO;
import com.codechallenge.bank.exception.DataNotFoundException;
import com.codechallenge.bank.model.Transaction;
import com.codechallenge.bank.model.TransactionStatus;
import com.codechallenge.bank.model.TransactionStatusRequester;
import com.codechallenge.bank.model.dto.AccountDto;
import com.codechallenge.bank.model.dto.TransactionDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.codechallenge.bank.model.Channel.*;
import static com.codechallenge.bank.model.Status.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class TransactionServiceTest {
  
  @Autowired
  private AccountService accountService;
  
  @Autowired
  private TransactionDAO dao;
  
  @Autowired
  private TransactionService service;
  
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
    when(accountService.findById("ABC123")).thenReturn(Optional.empty());
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
  
  @Test
  public void findStatusFromChannel_notFoundTransaction() {
    TransactionStatus expectedStatus = TransactionStatus.builder().reference("12345A").status(INVALID).build();
    TransactionStatusRequester requester = TransactionStatusRequester.builder().reference("12345A").build();
    when(dao.findById("12345A")).thenReturn(Optional.empty());
    TransactionStatus transactionStatus = service.findStatusFromChannel(requester);
    assertEquals(expectedStatus, transactionStatus);
  }
  
  @Test(expected = ResponseStatusException.class)
  public void findStatusFromChannel_noChannelProvided_dateBeforeCurrent() {
    TransactionStatusRequester requester = TransactionStatusRequester.builder()
      .reference("12345A")
      .build();
    TransactionDto transaction = TransactionDto.builder()
      .reference("12345A")
      .date(LocalDateTime.now().minusDays(1))
      .amount(193.38)
      .fee(3.18)
      .build();
    when(dao.findById("12345A")).thenReturn(Optional.of(transaction));
    service.findStatusFromChannel(requester);
    fail("Expecting invalid parameter exception");
  }
  
  @Test
  public void findStatusFromChannel_clientChannel_dateBeforeCurrent() {
    TransactionStatus expectedStatus = TransactionStatus.builder()
      .reference("12345A")
      .status(SETTLED)
      .amount(190.20)
      .build();
    TransactionStatusRequester requester = TransactionStatusRequester.builder()
      .reference("12345A")
      .channel(CLIENT)
      .build();
    TransactionDto transaction = TransactionDto.builder()
      .reference("12345A")
      .date(LocalDateTime.now().minusDays(1))
      .amount(193.38)
      .fee(3.18)
      .build();
    when(dao.findById("12345A")).thenReturn(Optional.of(transaction));
    TransactionStatus transactionStatus = service.findStatusFromChannel(requester);
    assertEquals(expectedStatus, transactionStatus);
    assertEquals(expectedStatus.getAmount(), transactionStatus.getAmount());
    assertNull(transactionStatus.getFee());
  }
  
  @Test
  public void findStatusFromChannel_atmChannel_dateBeforeCurrent() {
    TransactionStatus expectedStatus = TransactionStatus.builder()
      .reference("12345A")
      .status(SETTLED)
      .amount(190.20)
      .build();
    TransactionStatusRequester requester = TransactionStatusRequester.builder()
      .reference("12345A")
      .channel(ATM)
      .build();
    TransactionDto transaction = TransactionDto.builder()
      .reference("12345A")
      .date(LocalDateTime.now().minusDays(1))
      .amount(193.38)
      .fee(3.18)
      .build();
    when(dao.findById("12345A")).thenReturn(Optional.of(transaction));
    TransactionStatus transactionStatus = service.findStatusFromChannel(requester);
    assertEquals(expectedStatus, transactionStatus);
    assertEquals(expectedStatus.getAmount(), transactionStatus.getAmount());
    assertNull(transactionStatus.getFee());
  }
  
  @Test
  public void findStatusFromChannel_internalChannel_dateBeforeCurrent() {
    TransactionStatus expectedStatus = TransactionStatus.builder()
      .reference("12345A")
      .status(SETTLED)
      .amount(193.38)
      .fee(3.18)
      .build();
    TransactionStatusRequester requester = TransactionStatusRequester.builder()
      .reference("12345A")
      .channel(INTERNAL)
      .build();
    TransactionDto transaction = TransactionDto.builder()
      .reference("12345A")
      .date(LocalDateTime.now().minusDays(1))
      .amount(193.38)
      .fee(3.18)
      .build();
    when(dao.findById("12345A")).thenReturn(Optional.of(transaction));
    TransactionStatus transactionStatus = service.findStatusFromChannel(requester);
    assertEquals(expectedStatus, transactionStatus);
    assertEquals(expectedStatus.getAmount(), transactionStatus.getAmount());
    assertEquals(expectedStatus.getFee(), transactionStatus.getFee());
  }
  
  @Test
  public void findStatusFromChannel_clientChannel_dateEqualsCurrent() {
    TransactionStatus expectedStatus = TransactionStatus.builder()
      .reference("12345A")
      .status(PENDING)
      .amount(190.20)
      .build();
    TransactionStatusRequester requester = TransactionStatusRequester.builder()
      .reference("12345A")
      .channel(CLIENT)
      .build();
    TransactionDto transaction = TransactionDto.builder()
      .reference("12345A")
      .date(LocalDateTime.now())
      .amount(193.38)
      .fee(3.18)
      .build();
    when(dao.findById("12345A")).thenReturn(Optional.of(transaction));
    TransactionStatus transactionStatus = service.findStatusFromChannel(requester);
    assertEquals(expectedStatus, transactionStatus);
    assertEquals(expectedStatus.getAmount(), transactionStatus.getAmount());
    assertNull(transactionStatus.getFee());
  }
  
  @Test
  public void findStatusFromChannel_atmChannel_dateEqualsCurrent() {
    TransactionStatus expectedStatus = TransactionStatus.builder()
      .reference("12345A")
      .status(PENDING)
      .amount(190.20)
      .build();
    TransactionStatusRequester requester = TransactionStatusRequester.builder()
      .reference("12345A")
      .channel(ATM)
      .build();
    TransactionDto transaction = TransactionDto.builder()
      .reference("12345A")
      .date(LocalDateTime.now())
      .amount(193.38)
      .fee(3.18)
      .build();
    when(dao.findById("12345A")).thenReturn(Optional.of(transaction));
    TransactionStatus transactionStatus = service.findStatusFromChannel(requester);
    assertEquals(expectedStatus, transactionStatus);
    assertEquals(expectedStatus.getAmount(), transactionStatus.getAmount());
    assertNull(transactionStatus.getFee());
  }
  
  @Test
  public void findStatusFromChannel_internalChannel_dateEqualsCurrent() {
    TransactionStatus expectedStatus = TransactionStatus.builder()
      .reference("12345A")
      .status(PENDING)
      .amount(193.38)
      .fee(3.18)
      .build();
    TransactionStatusRequester requester = TransactionStatusRequester.builder()
      .reference("12345A")
      .channel(INTERNAL)
      .build();
    TransactionDto transaction = TransactionDto.builder()
      .reference("12345A")
      .date(LocalDateTime.now())
      .amount(193.38)
      .fee(3.18)
      .build();
    when(dao.findById("12345A")).thenReturn(Optional.of(transaction));
    TransactionStatus transactionStatus = service.findStatusFromChannel(requester);
    assertEquals(expectedStatus, transactionStatus);
    assertEquals(expectedStatus.getAmount(), transactionStatus.getAmount());
    assertEquals(expectedStatus.getFee(), transactionStatus.getFee());
  }
  
  @Test
  public void findStatusFromChannel_clientChannel_dateAfterCurrent() {
    TransactionStatus expectedStatus = TransactionStatus.builder()
      .reference("12345A")
      .status(FUTURE)
      .amount(190.20)
      .build();
    TransactionStatusRequester requester = TransactionStatusRequester.builder()
      .reference("12345A")
      .channel(CLIENT)
      .build();
    TransactionDto transaction = TransactionDto.builder()
      .reference("12345A")
      .date(LocalDateTime.now().plusDays(1))
      .amount(193.38)
      .fee(3.18)
      .build();
    when(dao.findById("12345A")).thenReturn(Optional.of(transaction));
    TransactionStatus transactionStatus = service.findStatusFromChannel(requester);
    assertEquals(expectedStatus, transactionStatus);
    assertEquals(expectedStatus.getAmount(), transactionStatus.getAmount());
    assertNull(transactionStatus.getFee());
  }
  
  @Test
  public void findStatusFromChannel_atmChannel_dateAfterCurrent() {
    TransactionStatus expectedStatus = TransactionStatus.builder()
      .reference("12345A")
      .status(PENDING)
      .amount(190.20)
      .build();
    TransactionStatusRequester requester = TransactionStatusRequester.builder()
      .reference("12345A")
      .channel(ATM)
      .build();
    TransactionDto transaction = TransactionDto.builder()
      .reference("12345A")
      .date(LocalDateTime.now().plusDays(1))
      .amount(193.38)
      .fee(3.18)
      .build();
    when(dao.findById("12345A")).thenReturn(Optional.of(transaction));
    TransactionStatus transactionStatus = service.findStatusFromChannel(requester);
    assertEquals(expectedStatus, transactionStatus);
    assertEquals(expectedStatus.getAmount(), transactionStatus.getAmount());
    assertNull(transactionStatus.getFee());
  }
  
  @Test
  public void findStatusFromChannel_internalChannel_dateAfterCurrent() {
    TransactionStatus expectedStatus = TransactionStatus.builder()
      .reference("12345A")
      .status(FUTURE)
      .amount(193.38)
      .fee(3.18)
      .build();
    TransactionStatusRequester requester = TransactionStatusRequester.builder()
      .reference("12345A")
      .channel(INTERNAL)
      .build();
    TransactionDto transaction = TransactionDto.builder()
      .reference("12345A")
      .date(LocalDateTime.now().plusDays(1))
      .amount(193.38)
      .fee(3.18)
      .build();
    when(dao.findById("12345A")).thenReturn(Optional.of(transaction));
    TransactionStatus transactionStatus = service.findStatusFromChannel(requester);
    assertEquals(expectedStatus, transactionStatus);
    assertEquals(expectedStatus.getAmount(), transactionStatus.getAmount());
    assertEquals(expectedStatus.getFee(), transactionStatus.getFee());
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
      return new TransactionService();
    }
  }
}