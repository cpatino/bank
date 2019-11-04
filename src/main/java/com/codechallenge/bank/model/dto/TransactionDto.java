package com.codechallenge.bank.model.dto;

import com.codechallenge.bank.model.Transaction;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.builder.ToStringStyle.NO_CLASS_NAME_STYLE;

/**
 * Model class used to store the transaction data
 *
 * @author Carlos Rodriguez
 * @since 25/09/2019
 */
@Entity(name = "account_transaction")
public class TransactionDto {
  
  @Id
  private String reference;
  
  @ManyToOne
  private AccountDto account;
  
  private LocalDateTime date;
  
  @Column(nullable = false)
  private double amount;
  
  private Double fee;
  
  private String description;
  
  //For hibernate
  public TransactionDto() {
    super();
  }
  
  private TransactionDto(final Builder builder) {
    reference = builder.reference;
    account = builder.account;
    date = (builder.date != null) ? builder.date : LocalDateTime.now();
    amount = builder.amount;
    fee = builder.fee;
    description = builder.description;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static Builder builder(final TransactionDto transaction) {
    return new Builder(transaction);
  }
  
  public static Builder builder(final Transaction transaction) {
    return new Builder(transaction);
  }
  
  public String getReference() {
    return reference;
  }
  
  public AccountDto getAccount() {
    return account;
  }
  
  public LocalDateTime getDate() {
    return date;
  }
  
  public double getAmount() {
    return amount;
  }
  
  public Double getFee() {
    return fee;
  }
  
  public String getDescription() {
    return description;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TransactionDto that = (TransactionDto) o;
    return new EqualsBuilder()
      .append(reference, that.reference)
      .isEquals();
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
      .append(reference)
      .toHashCode();
  }
  
  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, NO_CLASS_NAME_STYLE);
  }
  
  public static class Builder {
    
    private String reference;
    private AccountDto account;
    private LocalDateTime date;
    private double amount;
    private Double fee;
    private String description;
    
    private Builder() {
      super();
    }
    
    private Builder(final TransactionDto transaction) {
      reference(transaction.reference)
        .account(transaction.account)
        .date(transaction.date)
        .amount(transaction.amount)
        .fee(transaction.fee)
        .description(transaction.description);
    }
    
    private Builder(final Transaction transaction) {
      reference(transaction.getReference())
        .account(transaction.getAccount())
        .date(transaction.getDate())
        .amount(transaction.getAmount())
        .fee(transaction.getFee())
        .description(transaction.getDescription());
    }
    
    public Builder reference(final String reference) {
      this.reference = reference;
      return this;
    }
    
    public Builder account(final String accountIban) {
      this.account = AccountDto.builder().iban(accountIban).build();
      return this;
    }
    
    public Builder account(final AccountDto account) {
      this.account = account;
      return this;
    }
    
    public Builder date(final LocalDateTime date) {
      this.date = date;
      return this;
    }
    
    public Builder amount(final double amount) {
      this.amount = amount;
      return this;
    }
    
    public Builder fee(final Double fee) {
      this.fee = fee;
      return this;
    }
    
    public Builder description(final String description) {
      this.description = description;
      return this;
    }
    
    public TransactionDto build() {
      return new TransactionDto(this);
    }
  }
}