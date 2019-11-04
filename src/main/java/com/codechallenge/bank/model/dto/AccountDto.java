package com.codechallenge.bank.model.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.NO_CLASS_NAME_STYLE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Model class used to store the account data
 *
 * @author Carlos Rodriguez
 * @since 25/09/2019
 */
@Entity
public class AccountDto {
  
  @Id
  private String iban;
  private LocalDateTime date;
  private double balance;
  
  public AccountDto() {
    super();
  }
  
  private AccountDto(Builder builder) {
    iban = builder.iban;
    date = builder.date != null ? builder.date : LocalDateTime.now();
    balance = builder.balance;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static Builder builder(final AccountDto account) {
    return new Builder(account);
  }
  
  public static Builder builder(final String accountIban) {
    return new Builder().iban(accountIban);
  }
  
  public String getIban() {
    return iban;
  }
  
  public LocalDateTime getDate() {
    return date;
  }
  
  public double getBalance() {
    return balance;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AccountDto account = (AccountDto) o;
    return new EqualsBuilder()
      .append(iban, account.iban)
      .isEquals();
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
      .append(iban)
      .toHashCode();
  }
  
  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, NO_CLASS_NAME_STYLE);
  }
  
  public static class Builder {
    
    private String iban;
    private LocalDateTime date;
    private double balance;
    private List<TransactionDto> transactions = Collections.EMPTY_LIST;
    
    private Builder() {
      super();
    }
    
    private Builder(final AccountDto account) {
      iban(account.iban)
        .date(account.date)
        .balance(account.balance);
    }
    
    public Builder iban(final String iban) {
      this.iban = iban;
      return this;
    }
    
    public Builder date(final LocalDateTime date) {
      this.date = date;
      return this;
    }
    
    public Builder balance(final double balance) {
      this.balance = balance;
      if (balance < 0) {
        throw new ResponseStatusException(BAD_REQUEST, "The balance account could not be below 0");
      }
      return this;
    }
    
    public Builder transactions(final List<TransactionDto> transactions) {
      this.transactions = transactions;
      return this;
    }
    
    public AccountDto build() {
      return new AccountDto(this);
    }
  }
}