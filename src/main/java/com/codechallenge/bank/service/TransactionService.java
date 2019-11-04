package com.codechallenge.bank.service;

import com.codechallenge.bank.dao.TransactionDAO;
import com.codechallenge.bank.exception.DataNotFoundException;
import com.codechallenge.bank.model.*;
import com.codechallenge.bank.model.dto.AccountDto;
import com.codechallenge.bank.model.dto.TransactionDto;
import com.codechallenge.bank.util.TransactionReferenceGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.codechallenge.bank.model.Channel.*;
import static com.codechallenge.bank.model.Status.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Transaction service with the business logic implementation
 *
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
@Service
public class TransactionService {
  
  private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
  
  @Autowired
  private AccountService accountService;
  
  @Autowired
  private TransactionDAO dao;
  
  /**
   * Finds a {@link Transaction} using the given reference (identifier)
   *
   * @param reference the key of the {@link Transaction}
   * @return an {@link Optional} with the transaction if the reference is already in the database, otherwise it will be empty.
   */
  public Optional<TransactionDto> findById(final String reference) {
    return dao.findById(reference);
  }
  
  public List<TransactionDto> findAll() {
    return dao.findAll();
  }
  
  public List<TransactionDto> findAll(final String iban, final String sortType) {
    AccountDto account = accountService.findById(iban).orElseThrow(() -> new DataNotFoundException("transactions", iban));
    return (sortType == null || !("asc".equalsIgnoreCase(sortType) || "desc".equalsIgnoreCase(sortType))) ?
      dao.findByAccount(account) :
      dao.findByAccount(account, Sort.by(Sort.Direction.fromString(sortType), "amount"));
  }
  
  /**
   * Saves the given {@link Transaction} to the database
   *
   * @param transaction the one to be saved.
   */
  @Transactional
  public TransactionDto save(final Transaction transaction) {
    checkIsNewTransaction(transaction.getReference());
    return Optional.of(saveAccount(transaction.getAccount(), transaction.getAmount()))
      .map(account -> buildTransaction(account, transaction))
      .map(dao::save)
      .get();
  }
  
  /**
   * Finds the {@link TransactionStatus} for the reference from the {@link TransactionStatusRequester}
   *
   * @param requester The {@link TransactionStatusRequester} with the details to be checked to build the status.
   * @return A {@link TransactionStatus} with the details.
   */
  public TransactionStatus findStatusFromChannel(final TransactionStatusRequester requester) {
    return findById(requester.getReference())
      .map(transaction -> buildTransactionStatus(transaction, requester.getChannel()))
      .orElse(TransactionStatus.builder().reference(requester.getReference()).status(INVALID).build());
  }
  
  private AccountDto saveAccount(final String iban, final double transactionAmount) {
    AccountDto account = findAccountOrDefault(iban);
    return Optional.of(account)
      .map(a -> getNewBalance(a, transactionAmount))
      .map(balance -> AccountDto.builder(account).balance(balance).build())
      .map(accountService::save)
      .get();
  }
  
  private AccountDto findAccountOrDefault(final String iban) {
    return accountService.findById(iban)
      .or(() -> Optional.of(AccountDto.builder(iban).balance(0).build()))
      .get();
  }
  
  private TransactionDto buildTransaction(final AccountDto account, final Transaction transaction) {
    return TransactionDto.builder(transaction)
      .reference(getTransactionReference(transaction.getReference()))
      .account(account)
      .build();
  }
  
  private String getTransactionReference(String reference) {
    return StringUtils.isEmpty(reference) ? TransactionReferenceGenerator.generate() : reference;
  }
  
  /**
   * Checks if the given reference is not stored in the database
   *
   * @param reference the transaction's reference to be checked.
   */
  private void checkIsNewTransaction(String reference) {
    Optional.ofNullable(reference)
      .flatMap(dao::findById)
      .ifPresent(t -> {
        throw new ResponseStatusException(BAD_REQUEST, "The transaction cannot be saved, the reference "
          + reference + " was already used in other transaction");
      });
  }
  
  /**
   * Get the account balance after apply the new transaction amount.
   *
   * @param account           The {@link AccountDto} that has to be checked.
   * @param transactionAmount The new transaction amount that is going to be saved.
   */
  private double getNewBalance(final AccountDto account, final double transactionAmount) {
    return Optional.of(account)
      .map(a -> a.getBalance() + transactionAmount)
      .get();
  }
  
  /**
   * Builds a {@link TransactionStatus} following the next rules:
   * <p>
   * For the status field:
   * - When the transaction date is before the current date the {@link Status} should be: SETTLED.
   * - When the transaction date is equal to the current date the {@link Status} should be: PENDING
   * - When the transaction date is equal to the current date the {@link Status} should be: FUTURE
   * <p>
   * For the amount:
   * - When the {@link Channel} is ATM or CLIENT, the amount should be the amount subtracting the fee
   * - When the {@link Channel} is INTERNAL, the amount should be the transaction amount
   * <p>
   * For the fee:
   * - When the {@link Channel} is ATM or CLIENT, the fee should be null
   * - When the {@link Channel} is INTERNAL, the fee should be the transaction fee
   *
   * @param transaction the {@link Transaction} stored in the database
   * @param channel     the type of {@link Channel} that is asking for the status.
   */
  private TransactionStatus buildTransactionStatus(final TransactionDto transaction, final Channel channel) {
    TransactionStatus.Builder builder = TransactionStatus.builder();
    Status status = calculateStatus(transaction.getDate(), channel);
    logger.info("Transaction status = {}");
    builder.reference(transaction.getReference()).status(status);
    if (CLIENT.equals(channel) || ATM.equals(channel)) {
      builder.amount(calculateAmountSubtractingFee(transaction));
    } else if (INTERNAL.equals(channel)) {
      builder.amount(transaction.getAmount()).fee(transaction.getFee());
    } else {
      throw new ResponseStatusException(BAD_REQUEST, "Please provide a channel for the given reference");
    }
    return builder.build();
  }
  
  /**
   * Calculates the {@link Status} using the date from the {@link Transaction} stored in the database.
   *
   * @param transactionDate the transaction date
   * @return the {@link Status}
   */
  private Status calculateStatus(final LocalDateTime transactionDate, final Channel channel) {
    LocalDateTime currentDate = LocalDateTime.now();
    logger.info("Transaction date={}, current date={}", transactionDate, currentDate);
    if (transactionDate.toLocalDate().isBefore(currentDate.toLocalDate())) {
      return SETTLED;
    } else if (transactionDate.toLocalDate().isEqual(currentDate.toLocalDate())
      || (transactionDate.toLocalDate().isAfter(currentDate.toLocalDate()) && ATM.equals(channel))) {
      return PENDING;
    } else {
      return FUTURE;
    }
  }
  
  /**
   * Calculates the amount after subtracting the fee, that will be used in the {@link TransactionStatus} object.
   *
   * @param transaction the {@link Transaction} stored in the database
   * @return the new calculated amount.
   */
  private double calculateAmountSubtractingFee(final TransactionDto transaction) {
    int signChanger = (transaction.getAmount() < 0) ? -1 : 1;
    double fee = Optional.ofNullable(transaction.getFee()).orElse(0.0);
    return Optional.of(transaction.getAmount())
      .map(Math::abs)
      .map((amount) -> (amount - fee) * signChanger)
      .orElse(transaction.getAmount());
  }
}