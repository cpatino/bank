package com.codechallenge.bank.controller;

import com.codechallenge.bank.exception.InvalidParameterException;
import com.codechallenge.bank.model.Transaction;
import com.codechallenge.bank.service.AccountService;
import com.codechallenge.bank.service.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * To control the request for the /transactions REST endpoint
 *
 * @author Carlos Rodriguez
 * @since 25/09/2019
 */
@RestController
@RequestMapping(path = "/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path= { "/", "" }, consumes = "application/json")
    public void create(@RequestBody final Transaction transaction) {
        logger.info("Transaction to be saved: {}", transaction);
        try {
            transaction.validate();
            transactionService.save(transaction);
            logger.info("The transaction was saved correctly");
        } catch (IllegalStateException | NullPointerException ex) {
            logger.error(ex.getMessage());
            throw new InvalidParameterException(ex.getMessage());
        }
    }

    @GetMapping(path= { "/", "" }, produces = "application/json")
    public List<Transaction> findAll() {
        logger.info("Find all transactions");
        return accountService.findAll().stream()
                .map(account -> account.getTransactions())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @GetMapping("/{iban}")
    public List<Transaction> findAll(@PathVariable String iban, @RequestHeader("sort-type") String sortType) {
        return accountService.findTransactionsById(iban, sortType);
    }
}