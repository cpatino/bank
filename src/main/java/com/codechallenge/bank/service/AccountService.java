package com.codechallenge.bank.service;

import com.codechallenge.bank.dao.AccountDAO;
import com.codechallenge.bank.model.dto.AccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * AccountDto service with the business logic implementation
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
     * Finds an {@link AccountDto} using the given iban (identifier)
     *
     * @param iban the key of the {@link AccountDto}
     * @return an {@link Optional} with the AccountDto if the iban is already in the database, otherwise it will be empty.
     */
    public Optional<AccountDto> findById(final String iban) {
        return dao.findById(iban);
    }

    /**
     * Finds all {@link AccountDto}s that are stored in the database.
     *
     * @return A list of {@link AccountDto}s
     */
    public List<AccountDto> findAll() {
        return dao.findAll();
    }
    
    public AccountDto save(AccountDto account) {
        return dao.save(account);
    }
}