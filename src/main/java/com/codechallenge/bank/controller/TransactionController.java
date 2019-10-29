package com.codechallenge.bank.controller;

import com.codechallenge.bank.exception.InvalidParameterException;
import com.codechallenge.bank.model.Transaction;
import com.codechallenge.bank.model.dto.TransactionDto;
import com.codechallenge.bank.service.AccountService;
import com.codechallenge.bank.service.TransactionService;
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
    public TransactionDto create(@RequestBody final Transaction transaction) {
        logger.info("Transaction to be saved: {}", transaction);
        try {
            transaction.validate();
            TransactionDto savedTransaction = transactionService.save(transaction);
            logger.info("The transaction was saved correctly");
            return savedTransaction;
        } catch (IllegalStateException | NullPointerException ex) {
            logger.error(ex.getMessage());
            throw new InvalidParameterException(ex.getMessage());
        }
    }

    @GetMapping(path= { "/", "" }, produces = "application/json")
    public List<TransactionDto> findAll() {
        logger.info("Find all transactions");
        return transactionService.findAll();
    }

    @GetMapping("/{iban}")
    public List<TransactionDto> findAll(@PathVariable String iban, @RequestHeader(value = "sort-type", required = false) String sortType) {
        return transactionService.findAll(iban, sortType);
    }
}