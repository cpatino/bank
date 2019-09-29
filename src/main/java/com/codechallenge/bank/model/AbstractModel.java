package com.codechallenge.bank.model;

/**
 * Abstract model class with common methods from all model entities
 *
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
public abstract class AbstractModel {

    /**
     * Validates the required fields for the model.
     */
    public abstract void validate();
}