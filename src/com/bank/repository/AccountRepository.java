package com.bank.repository;

import com.bank.model.Account;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Account data persistence.
 * Demonstrates separation of concerns and interface abstraction.
 */
public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findAll();
    boolean delete(String accountNumber);
    String generateNextAccountNumber();
    void persistChanges();
}
