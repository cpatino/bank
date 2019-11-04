package com.codechallenge.bank.service;

import com.codechallenge.bank.model.*;
import com.codechallenge.bank.model.dto.TransactionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Predicate;

import static com.codechallenge.bank.model.Channel.*;
import static com.codechallenge.bank.model.Status.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class TransactionStatusService {
  
  private static final Logger logger = LoggerFactory.getLogger(TransactionStatusService.class);
  @Autowired private TransactionService transactionService;
  
  /**
   * Finds the {@link TransactionStatus} for the reference from the {@link TransactionStatusRequester}
   *
   * @param requester The {@link TransactionStatusRequester} with the details to be checked to build the status.
   * @return A {@link TransactionStatus} with the details.
   */
  public TransactionStatus findStatusFromChannel(final TransactionStatusRequester requester) {
    return transactionService.findById(requester.getReference())
      .map(transaction -> buildTransactionStatus(transaction, requester.getChannel()))
      .orElse(TransactionStatus.builder().reference(requester.getReference()).status(INVALID).build());
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
    return Optional.of(TransactionStatus.builder())
      .map(builder -> builder.status(calculateStatus(transaction.getDate(), channel)))
      .map(builder -> builder.reference(transaction.getReference()))
      .map(builder -> builder.amount(calculateAmount(transaction, channel)))
      .map(builder -> {
        if (INTERNAL.equals(channel)) {
          builder.fee(transaction.getFee());
        }
        return builder;
      })
      .map(TransactionStatus.Builder::build)
      .get();
  }
  
  /**
   * Calculates the {@link Status} using the date from the {@link Transaction} stored in the database.
   *
   * @param transactionDate the transaction date
   * @return the {@link Status}
   */
  private Status calculateStatus(final LocalDateTime transactionDate, final Channel channel) {
    return calculateSettledStatus(transactionDate)
      .or(() -> calculatePendingStatus(transactionDate))
      .or(() -> calculatePendingStatusForATM(transactionDate, channel))
      .or(() -> calculateFutureStatus(transactionDate))
      .get();
  }
  
  private Optional<Status> calculateSettledStatus(final LocalDateTime transactionDate) {
    return Optional.of(LocalDateTime.now())
      .filter(currentDate -> transactionDate.toLocalDate().isBefore(currentDate.toLocalDate()))
      .map(c -> SETTLED);
  }
  
  private Optional<Status> calculatePendingStatus(final LocalDateTime transactionDate) {
    return Optional.of(LocalDateTime.now())
      .filter(currentDate -> transactionDate.toLocalDate().isEqual(currentDate.toLocalDate()))
      .map(c -> PENDING);
  }
  
  private Optional<Status> calculatePendingStatusForATM(LocalDateTime transactionDate, Channel channel) {
    return Optional.of(LocalDateTime.now())
      .filter(currentDate -> transactionDate.toLocalDate().isAfter(currentDate.toLocalDate()) && getAtmPredicate().test(channel))
      .map(c -> PENDING);
  }
  
  private Optional<Status> calculateFutureStatus(final LocalDateTime transactionDate) {
    return Optional.of(LocalDateTime.now())
      .filter(currentDate -> transactionDate.toLocalDate().isAfter(currentDate.toLocalDate()))
      .map(c -> FUTURE);
  }
  
  private double calculateAmount(final TransactionDto transaction, final Channel channel) {
    return calculateClientAmount(transaction, channel)
      .or(() -> calculateAtmAmount(transaction, channel))
      .or(() -> calculateInternalAmount(transaction, channel))
      .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Please provide a channel for the given reference"));
  }
  
  private Predicate<Channel> getClientPredicate() {
    return CLIENT::equals;
  }
  
  private Predicate<Channel> getAtmPredicate() {
    return ATM::equals;
  }
  
  private Predicate<Channel> getInternalPredicate() {
    return INTERNAL::equals;
  }
  
  private Optional<Double> calculateClientAmount(final TransactionDto transaction, final Channel channel) {
    return Optional.ofNullable(channel)
      .filter(getClientPredicate())
      .map(c -> calculateAmountSubtractingFee(transaction));
  }
  
  private Optional<Double> calculateAtmAmount(final TransactionDto transaction, final Channel channel) {
    return Optional.ofNullable(channel)
      .filter(getAtmPredicate())
      .map(c -> calculateAmountSubtractingFee(transaction));
  }
  
  private Optional<Double> calculateInternalAmount(final TransactionDto transaction, final Channel channel) {
    return Optional.ofNullable(channel)
      .filter(getInternalPredicate())
      .map(c -> transaction.getAmount());
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
