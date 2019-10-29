package com.codechallenge.bank.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.validation.constraints.NotEmpty;

import static org.apache.commons.lang3.builder.ToStringStyle.NO_CLASS_NAME_STYLE;

/**
 * Model class used to store the transaction status requester data
 *
 * @author Carlos Rodriguez
 * @since 27/09/2019
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionStatusRequester {
  
  @NotEmpty(message = "the reference is required")
  private String reference;
  private Channel channel;
  
  public TransactionStatusRequester() {
    super();
  }
  
  private TransactionStatusRequester(final Builder builder) {
    reference = builder.reference;
    channel = builder.channel;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public String getReference() {
    return reference;
  }
  
  public Channel getChannel() {
    return channel;
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