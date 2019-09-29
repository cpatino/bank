package com.codechallenge.bank.dao;

import com.codechallenge.bank.model.Transaction;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Transaction hibernate implementation
 *
 * @author Carlos Rodriguez
 * @since 25/09/2019
 */
@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public interface TransactionDAO extends JpaRepository<Transaction, String> {
}
