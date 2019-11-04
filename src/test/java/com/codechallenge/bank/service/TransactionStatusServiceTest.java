package com.codechallenge.bank.service;

import com.codechallenge.bank.model.TransactionStatus;
import com.codechallenge.bank.model.TransactionStatusRequester;
import com.codechallenge.bank.model.dto.TransactionDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.codechallenge.bank.model.Channel.*;
import static com.codechallenge.bank.model.Status.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionStatusServiceTest {
  
  @MockBean private TransactionService transactionService;
  @Autowired private TransactionStatusService service;
  
  @Test
  public void findStatusFromChannel_notFoundTransaction() {
    TransactionStatus expectedStatus = TransactionStatus.builder().reference("12345A").status(INVALID).build();
    TransactionStatusRequester requester = TransactionStatusRequester.builder().reference("12345A").build();
    when(transactionService.findById("12345A")).thenReturn(Optional.empty());
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
    when(transactionService.findById("12345A")).thenReturn(Optional.of(transaction));
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
    when(transactionService.findById("12345A")).thenReturn(Optional.of(transaction));
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
    when(transactionService.findById("12345A")).thenReturn(Optional.of(transaction));
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
    when(transactionService.findById("12345A")).thenReturn(Optional.of(transaction));
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
    when(transactionService.findById("12345A")).thenReturn(Optional.of(transaction));
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
    when(transactionService.findById("12345A")).thenReturn(Optional.of(transaction));
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
    when(transactionService.findById("12345A")).thenReturn(Optional.of(transaction));
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
    when(transactionService.findById("12345A")).thenReturn(Optional.of(transaction));
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
    when(transactionService.findById("12345A")).thenReturn(Optional.of(transaction));
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
    when(transactionService.findById("12345A")).thenReturn(Optional.of(transaction));
    TransactionStatus transactionStatus = service.findStatusFromChannel(requester);
    assertEquals(expectedStatus, transactionStatus);
    assertEquals(expectedStatus.getAmount(), transactionStatus.getAmount());
    assertEquals(expectedStatus.getFee(), transactionStatus.getFee());
  }
}
