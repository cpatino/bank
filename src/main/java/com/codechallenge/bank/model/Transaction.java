package com.codechallenge.bank.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.builder.ToStringStyle.NO_CLASS_NAME_STYLE;

/**
 * Model class used to store the transaction data
 *
 * @author Carlos Rodriguez
 * @since 25/09/2019
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
  
  private String reference;
  @JsonProperty("account_iban")
  @NotEmpty(message = "the IBAN number of the account is required")
  private String account;
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.046'Z'")
  private LocalDateTime date;
  private double amount;
  @PositiveOrZero(message = "the fee cannot be less than zero")
  private Double fee;
  private String description;
  
  public Transaction() {
    super();
  }
  
  private Transaction(final Builder builder) {
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
  
  public static Builder builder(final Transaction transaction) {
    return new Builder(transaction);
  }
  
  public String getReference() {
    return reference;
  }
  
  public String getAccount() {
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
    Transaction that = (Transaction) o;
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
    private String account;
    private LocalDateTime date;
    private double amount;
    private Double fee;
    private String description;
    
    private Builder() {
      super();
    }
    
    private Builder(final Transaction transaction) {
      reference(transaction.reference)
        .account(transaction.account)
        .date(transaction.date)
        .amount(transaction.amount)
        .fee(transaction.fee)
        .description(transaction.description);
    }
    
    public Builder reference(final String reference) {
      this.reference = reference;
      return this;
    }
    
    public Builder account(final String accountIban) {
      this.account = accountIban;
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
    
    public Transaction build() {
      return new Transaction(this);
    }
  }
}