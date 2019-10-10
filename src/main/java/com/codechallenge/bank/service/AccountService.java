package com.codechallenge.bank.service;

import com.codechallenge.bank.dao.AccountDAO;
import com.codechallenge.bank.exception.DataNotFoundException;
import com.codechallenge.bank.model.Account;
import com.codechallenge.bank.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Account service with the business logic implementation
 *
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AccountService {

    @Autowired
    private AccountDAO dao;

    /**
     * Finds an {@link Account} using the given iban (identifier)
     *
     * @param iban the key of the {@link Account}
     * @return an {@link Optional} with the account if the iban is already in the database, otherwise it will be empty.
     */
    public Optional<Account> findById(final String iban) {
        return dao.findById(iban);
    }

    /**
     * Finds all {@link Account}s that are stored in the database.
     *
     * @return A list of {@link Account}s
     */
    public List<Account> findAll() {
        return dao.findAll();
    }
}