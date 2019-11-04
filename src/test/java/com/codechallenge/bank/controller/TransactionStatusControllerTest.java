package com.codechallenge.bank.controller;

import com.codechallenge.bank.model.TransactionStatus;
import com.codechallenge.bank.model.TransactionStatusRequester;
import com.codechallenge.bank.service.TransactionStatusService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codechallenge.bank.model.Status.INVALID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionStatusControllerTest {
  
  @MockBean private TransactionStatusService service;
  @Autowired private TransactionStatusController controller;
  
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
}