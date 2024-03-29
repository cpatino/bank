package com.codechallenge.bank.dao;

import com.codechallenge.bank.model.dto.AccountDto;
import com.codechallenge.bank.model.dto.TransactionDto;
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
public interface TransactionDAO extends JpaRepository<TransactionDto, String> {
  
  List<TransactionDto> findByAccount(final AccountDto account);
  
  List<TransactionDto> findByAccount(final AccountDto account, final Sort sort);
}
