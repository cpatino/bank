package com.codechallenge.bank.controller;

import com.codechallenge.bank.dao.AccountDAO;
import com.codechallenge.bank.dao.TransactionDAO;
import com.codechallenge.bank.exception.InvalidParameterException;
import com.codechallenge.bank.model.TransactionStatus;
import com.codechallenge.bank.model.TransactionStatusRequester;
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

import static com.codechallenge.bank.model.Status.INVALID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class TransactionStatusControllerTest {

    @Autowired
    private TransactionService service;

    @Autowired
    private TransactionStatusController controller;

    @Test(expected = InvalidParameterException.class)
    public void find_notValidMessage() {
        TransactionStatusRequester requester = TransactionStatusRequester.builder().build();
        controller.find(requester);
        fail("InvalidParameterException was expected");
    }

    @Test(expected = InvalidParameterException.class)
    public void find_nullMessage() {
        controller.find(null);
        fail("InvalidParameterException was expected");
    }

    @Test
    public void find_validMessage() {
        TransactionStatus expectedStatus = TransactionStatus.builder().reference("12345A").status(INVALID).build();
        TransactionStatusRequester requester = TransactionStatusRequester.builder()
                .reference("12345A")
                .build();
        when(service.findStatusFromChannel(requester)).thenReturn(expectedStatus);
        TransactionStatus status = controller.find(requester);
        assertEquals(expectedStatus, status);
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
        public TransactionStatusController transactionStatusController() {
            return new TransactionStatusController();
        }
    }
}