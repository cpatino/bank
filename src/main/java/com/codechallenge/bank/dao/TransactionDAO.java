package com.codechallenge.bank.dao;

import com.codechallenge.bank.model.Account;
import com.codechallenge.bank.model.Transaction;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Transaction hibernate implementation
 *
 * @author Carlos Rodriguez
 * @since 25/09/2019
 */
@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public interface TransactionDAO extends JpaRepository<Transaction, String> {

    List<Transaction> findByAccount(final Account account);

    List<Transaction> findByAccount(final Account account, final Sort sort);
}
