package com.codechallenge.bank.service;

import com.codechallenge.bank.dao.AccountDAO;
import com.codechallenge.bank.model.dto.AccountDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountServiceTest {
  
  @Autowired private AccountService service;
  @MockBean private AccountDAO dao;
  
  @Test
  public void findById_notFound() {
    when(dao.findById("ABC123")).thenReturn(Optional.empty());
    Optional<AccountDto> account = service.findById("ABC123");
    assertTrue(account.isEmpty());
  }
  
  @Test
  public void findById_found() {
    AccountDto expectedAccount = AccountDto.builder().iban("ABC123").build();
    when(dao.findById("ABC123")).thenReturn(Optional.of(expectedAccount));
    Optional<AccountDto> account = service.findById("ABC123");
    assertEquals(expectedAccount, account.get());
  }
  
  @Test
  public void findAll_notFound() {
    when(dao.findAll()).thenReturn(Collections.EMPTY_LIST);
    List<AccountDto> accounts = service.findAll();
    assertTrue(accounts.isEmpty());
  }
  
  @Test
  public void findAll_found() {
    AccountDto expectedAccount = AccountDto.builder().iban("ABC123").build();
    List<AccountDto> expectedAccounts = Collections.singletonList(expectedAccount);
    when(dao.findAll()).thenReturn(expectedAccounts);
    List<AccountDto> accounts = service.findAll();
    assertEquals(expectedAccounts, accounts);
    assertEquals(expectedAccount, accounts.get(0));
  }
}