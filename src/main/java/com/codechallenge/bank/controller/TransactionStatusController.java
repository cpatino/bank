package com.codechallenge.bank.controller;

import com.codechallenge.bank.model.TransactionStatus;
import com.codechallenge.bank.model.TransactionStatusRequester;
import com.codechallenge.bank.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * To control the request for the /transactionStatus REST endpoint
 *
 * @author Carlos Rodriguez
 * @since 27/09/2019
 */
@Validated
@RestController
@RequestMapping(path = "/transactionStatus")
public class TransactionStatusController {
  
  private static final Logger logger = LoggerFactory.getLogger(TransactionStatusController.class);
  
  @Autowired
  private TransactionService service;
  
  @GetMapping(path = {"/", ""}, produces = "application/json", consumes = "application/json")
  public TransactionStatus find(@RequestBody @Valid final TransactionStatusRequester requester) {
    logger.info("Requester: {} that will be used to check the status", requester);
    TransactionStatus transactionStatus = service.findStatusFromChannel(requester);
    logger.info("Status: {} from reference: {}", transactionStatus, requester.getReference());
    return transactionStatus;
  }
}