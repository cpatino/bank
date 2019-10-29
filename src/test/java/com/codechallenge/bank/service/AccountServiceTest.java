package com.codechallenge.bank.service;

import com.codechallenge.bank.dao.AccountDAO;
import com.codechallenge.bank.model.dto.AccountDto;
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