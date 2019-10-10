package com.codechallenge.bank.service;

import com.codechallenge.bank.dao.TransactionDAO;
import com.codechallenge.bank.exception.InvalidParameterException;
import com.codechallenge.bank.model.*;
import com.codechallenge.bank.util.LocalDateUtils;
import com.codechallenge.bank.util.TransactionReferenceGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static com.codechallenge.bank.model.Channel.*;
import static com.codechallenge.bank.model.Status.*;

/**
 * Transaction service with the business logic implementation
 *
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
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
    public Optional<Transaction> findById(final String reference) {
        return dao.findById(reference);
    }

    /**
     * Saves the given {@link Transaction} to the database
     *
     * @param transaction the one to be saved.
     */
    public Transaction save(final Transaction transaction) {
        checkIsNewTransaction(transaction.getReference());
        Optional<Account> account = accountService.findById(transaction.getAccount().getIban());
        double balance = checkBalance(account, transaction);
        Transaction savedTransaction = dao.save(Transaction.builder(transaction)
                .reference(StringUtils.isEmpty(transaction.getReference())
                        ? TransactionReferenceGenerator.generate() : transaction.getReference())
                .account(Account.builder(account.isPresent() ? account.get() : transaction.getAccount())
                        .balance(balance)
                        .build())
                .build());
        logger.info("Saved transaction = {}", savedTransaction);
        return savedTransaction;
    }

    /**
     * Finds the {@link TransactionStatus} for the reference from the {@link TransactionStatusRequester}
     *
     * @param requester The {@link TransactionStatusRequester} with the details to be checked to build the status.
     * @return A {@link TransactionStatus} with the details.
     */
    public TransactionStatus findStatusFromChannel(final TransactionStatusRequester requester) {
        Optional<Transaction> transaction = findById(requester.getReference());
        return buildTransactionStatus(transaction, requester);
    }

    /**
     * Checks if the given reference is not stored in the database
     *
     * @param reference the transaction's reference to be checked.
     */
    private void checkIsNewTransaction(String reference) {
        if (StringUtils.isNotEmpty(reference) && dao.findById(reference).isPresent()) {
            throw new InvalidParameterException("The transaction cannot be saved, the reference "
                    + reference + " was already used in other transaction");
        }
    }

    /**
     * Checks that the balance after apply the new amount is not below zero, also if it is a new account, it checks that
     * the first transaction is not a credit.
     *
     * @param account The {@link Account} that has to be checked.
     * @param newTransaction The {@link Transaction} that is going to be save.
     */
    private double checkBalance(final Optional<Account> account, final Transaction newTransaction) {
        if (account.isEmpty()) {
            if (newTransaction.getAmount() < 0) {
                throw new InvalidParameterException("The transaction cannot be saved, the balance account could not be below 0");
            }
            return newTransaction.getAmount();
        } else {
            double balance = account.get().getBalance() + newTransaction.getAmount();
            logger.info("Balance={} from the account={} before save the new transaction", balance, account.get().getIban());
            if (balance < 0) {
                throw new InvalidParameterException("The transaction cannot be saved, the balance account could not be below 0");
            }
            return balance;
        }
    }

    /**
     * Builds a {@link TransactionStatus} for the given {@link Transaction} and {@link TransactionStatusRequester}.
     *
     * When the transaction does not exists in the database, an INVALID status is returned.
     *
     * @param transaction the {@link Transaction} stored in the database.
     * @param requester the requester with the details to be checked to build the status to be returned.
     * @return a {@link TransactionStatus}
     */
    private TransactionStatus buildTransactionStatus(Optional<Transaction> transaction, TransactionStatusRequester requester) {
        TransactionStatus.Builder builder = TransactionStatus.builder();
        if (transaction.isEmpty()) {
            builder.reference(requester.getReference()).status(INVALID);
        } else {
            buildTransactionStatus(builder, transaction.get(), requester.getChannel());
        }
        return builder.build();
    }

    /**
     * Builds a {@link TransactionStatus} following the next rules:
     *
     * For the status field:
     * - When the transaction date is before the current date the {@link Status} should be: SETTLED.
     * - When the transaction date is equal to the current date the {@link Status} should be: PENDING
     * - When the transaction date is equal to the current date the {@link Status} should be: FUTURE
     *
     * For the amount:
     * - When the {@link Channel} is ATM or CLIENT, the amount should be the amount subtracting the fee
     * - When the {@link Channel} is INTERNAL, the amount should be the transaction amount
     *
     * For the fee:
     * - When the {@link Channel} is ATM or CLIENT, the fee should be null
     * - When the {@link Channel} is INTERNAL, the fee should be the transaction fee
     *
     * @param builder the {@link com.codechallenge.bank.model.TransactionStatus.Builder} being build
     * @param transaction the {@link Transaction} stored in the database
     * @param channel the type of {@link Channel} that is asking for the status.
     */
    private void buildTransactionStatus(final TransactionStatus.Builder builder, final Transaction transaction,
                                        final Channel channel) {
        Status status = calculateStatus(transaction.getDate(), channel);
        logger.info("Transaction status = {}");
        builder.reference(transaction.getReference()).status(status);
        if (CLIENT.equals(channel) || ATM.equals(channel)) {
            builder.amount(calculateAmountSubtractingFee(transaction));
        } else if (INTERNAL.equals(channel)) {
            builder.amount(transaction.getAmount()).fee(transaction.getFee());
        } else {
            throw new InvalidParameterException("Please provide a channel for the given reference");
        }
    }

    /**
     * Calculates the {@link Status} using the date from the {@link Transaction} stored in the database.
     *
     * @param date the transaction date
     * @return the {@link Status}
     */
    private Status calculateStatus(final Date date, final Channel channel) {
        LocalDate transactionDate = LocalDateUtils.getLocalDateFromDate(date);
        LocalDate currentDate = LocalDate.now();
        logger.info("Transaction date={}, current date={}", transactionDate, currentDate);
        if (transactionDate.isBefore(currentDate)) {
            return SETTLED;
        } else if (transactionDate.isEqual(currentDate) || (transactionDate.isAfter(currentDate) && ATM.equals(channel))) {
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
    private double calculateAmountSubtractingFee(final Transaction transaction) {
        boolean hasToTurnPositive = (transaction.getAmount() < 0);
        double amount = hasToTurnPositive ? transaction.getAmount() * -1 : transaction.getAmount();
        double fee = transaction.getFee() != null ? transaction.getFee() : 0;
        return (amount - fee) * (hasToTurnPositive ? -1 : 1);
    }
}