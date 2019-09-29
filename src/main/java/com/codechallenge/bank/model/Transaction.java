package com.codechallenge.bank.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.persistence.*;
import java.util.Date;

import static com.codechallenge.bank.util.StringBuilderUtils.append;
import static org.apache.commons.lang3.builder.ToStringStyle.NO_CLASS_NAME_STYLE;

/**
 * Model class used to store the transaction data
 *
 * @author Carlos Rodriguez
 * @since 25/09/2019
 */
@Entity(name = "account_transaction")
public class Transaction extends AbstractModel {

    @Id
    private String reference;

    @ManyToOne(cascade = CascadeType.ALL)
    private Account account;

    private Date date;

    @Column(nullable = false)
    private double amount;

    private Double fee;

    private String description;

    public Transaction() {
        super();
    }

    private Transaction(final Builder builder) {
        reference = builder.reference;
        account = builder.account;
        date = (builder.date != null) ? builder.date : new Date();
        amount = builder.amount;
        fee = builder.fee;
        description = builder.description;
    }

    public String getReference() {
        return reference;
    }

    public Account getAccount() {
        return account;
    }

    public Date getDate() {
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
    public void validate() {
        StringBuilder builder = new StringBuilder();
        if (account == null || StringUtils.isEmpty(account.getIban())) {
            append(builder, "the IBAN number of the account is required");
        }
        if (amount == 0) {
            append(builder, "the amount is required and must be different to zero");
        }
        if (fee != null && fee < 0) {
            append(builder, "the fee cannot be less than zero");
        }
        if (builder.length() > 0) {
            throw new IllegalStateException(builder.toString());
        }
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

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final Transaction transaction) {
        return new Builder(transaction);
    }

    public static class Builder {

        private String reference;
        private Account account;
        private Date date;
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
            this.account = Account.builder().iban(accountIban).build();
            return this;
        }

        public Builder account(final Account account) {
            this.account = account;
            return this;
        }

        public Builder date(final Date date) {
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