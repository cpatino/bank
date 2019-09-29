package com.codechallenge.bank.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import static com.codechallenge.bank.util.StringBuilderUtils.append;
import static org.apache.commons.lang3.builder.ToStringStyle.NO_CLASS_NAME_STYLE;

/**
 * Model class used to store the transaction status requester data
 *
 * @author Carlos Rodriguez
 * @since 27/09/2019
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionStatusRequester extends AbstractModel {

    @JsonProperty
    private String reference;

    @JsonProperty
    private Channel channel;

    private TransactionStatusRequester(final Builder builder) {
        reference = builder.reference;
        channel = builder.channel;
    }

    public String getReference() {
        return reference;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void validate() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isEmpty(reference)) {
            append(builder, "the reference is required");
        }
        if (builder.length() > 0) {
            throw new IllegalStateException(builder.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionStatusRequester that = (TransactionStatusRequester) o;
        return new EqualsBuilder()
                .append(reference, that.reference)
                .append(channel, that.channel)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(reference)
                .append(channel)
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
        private Channel channel;

        private Builder() {
            super();
        }

        public Builder reference(final String reference) {
            this.reference = reference;
            return this;
        }

        public Builder channel(final Channel channel) {
            this.channel = channel;
            return this;
        }

        public TransactionStatusRequester build() {
            return new TransactionStatusRequester(this);
        }
    }
}