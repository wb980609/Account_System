package com.example.account.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
    Integer countByAccountUser(AccountUser accountUser);

    Optional<Account> findFirstByOrderByIdDesc();

    List<Account> findByAccountUser(AccountUser accountUser);

    Optional<Account> findByAccountNumber(String AccountNumber);
}
