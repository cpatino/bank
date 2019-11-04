package com.codechallenge.bank.service;

import com.codechallenge.bank.dao.TransactionDAO;
import com.codechallenge.bank.exception.DataNotFoundException;
import com.codechallenge.bank.model.Transaction;
import com.codechallenge.bank.model.dto.AccountDto;
import com.codechallenge.bank.model.dto.TransactionDto;
import com.codechallenge.bank.util.TransactionReferenceGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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
    return Optional.ofNullable(sortType)
      .filter(st -> "asc".equalsIgnoreCase(st) || "desc".equalsIgnoreCase(st))
      .map(st -> Sort.by(Sort.Direction.fromString(st), "amount"))
      .map(st -> dao.findByAccount(account, st))
      .orElse(dao.findByAccount(account));
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
}