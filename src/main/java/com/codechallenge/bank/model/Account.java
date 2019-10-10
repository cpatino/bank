package com.codechallenge.bank.model;

import com.codechallenge.bank.util.BankControllerAdvice;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.codechallenge.bank.util.StringBuilderUtils.append;
import static javax.persistence.FetchType.LAZY;
import static org.apache.commons.lang3.builder.ToStringStyle.NO_CLASS_NAME_STYLE;

/**
 * Model class used to store the account data
 *
 * @author Carlos Rodriguez
 * @since 25/09/2019
 */
@Entity
public class Account extends AbstractModel {

    @Id
    private String iban;
    private Date date;
    private double balance;

    @OneToMany(fetch = LAZY)
    @JoinColumn(name = "account_iban")
    private List<Transaction> transactions;

    public Account() {
        super();
    }

    private Account(Builder builder) {
        iban = builder.iban;
        date = builder.date != null ? builder.date : new Date();
        balance = builder.balance;
        transactions = builder.transactions;
    }

    public String getIban() {
        return iban;
    }

    public Date getDate() {
        return date;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public void validate() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isEmpty(iban)) {
            append(builder, "the IBAN number of the account is required");
        }
        if (builder.length() > 0) {
            throw new IllegalStateException(builder.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
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

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final Account account) {
        return new Builder(account);
    }

    public static class Builder {

        private String iban;
        private Date date;
        private double balance;
        private List<Transaction> transactions = Collections.EMPTY_LIST;

        private Builder() {
            super();
        }

        private Builder(final Account account) {
            iban(account.iban)
                    .date(account.date)
                    .balance(account.balance)
                    .transactions(account.transactions);
        }

        public Builder iban(final String iban) {
            this.iban = iban;
            return this;
        }

        public Builder date(final Date date) {
            this.date = date;
            return this;
        }

        public Builder balance(final double balance) {
            this.balance = balance;
            return this;
        }

        public Builder transactions(final List<Transaction> transactions) {
            this.transactions = transactions;
            return this;
        }

        public Account build() {
            return new Account(this);
        }
    }
}