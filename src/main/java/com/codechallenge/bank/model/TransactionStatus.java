package com.codechallenge.bank.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import static com.codechallenge.bank.util.StringBuilderUtils.append;
import static org.apache.commons.lang3.builder.ToStringStyle.NO_CLASS_NAME_STYLE;

/**
 * Model class used to store the transaction status data
 *
 * @author Carlos Rodriguez
 * @since 27/09/2019
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionStatus extends AbstractModel {

    @JsonProperty
    private String reference;

    @JsonProperty
    private Status status;

    @JsonProperty
    private Double amount;

    @JsonProperty
    private Double fee;

    private TransactionStatus(final Builder builder) {
        reference = builder.reference;
        status = builder.status;
        amount = builder.amount;
        fee = builder.fee;
    }

    public String getReference() {
        return reference;
    }

    public Status getStatus() {
        return status;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getFee() {
        return fee;
    }

    @Override
    public void validate() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isEmpty(reference)) {
            append(builder, "the reference is required");
        }
        if (status == null) {
            append(builder, "the status is required");
        }
        if (builder.length() > 0) {
            throw new IllegalStateException(builder.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionStatus that = (TransactionStatus) o;
        return new EqualsBuilder()
                .append(reference, that.reference)
                .append(status, that.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(reference)
                .append(status)
                .toHashCode();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, NO_CLASS_NAME_STYLE);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String reference;
        private Status status;
        private Double amount;
        private Double fee;

        private Builder() {
            super();
        }

        public Builder reference(final String reference) {
            this.reference = reference;
            return this;
        }

        public Builder status(final Status status) {
            this.status = status;
            return this;
        }

        public Builder amount(final Double amount) {
            this.amount = amount;
            return this;
        }

        public Builder fee(final Double fee) {
            this.fee = fee;
            return this;
        }

        public TransactionStatus build() {
            return new TransactionStatus(this);
        }
    }
}