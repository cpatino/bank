package com.codechallenge.bank.dao;

import com.codechallenge.bank.model.dto.AccountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Account hibernate implementation
 *
 * @author Carlos Rodriguez
 * @since 25/09/2019
 */
@Repository
public interface AccountDAO extends JpaRepository<AccountDto, String> {
}
