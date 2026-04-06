package com.walletapp.account.internal.repository;

import com.walletapp.account.internal.domain.Account;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

  List<Account> findAllByUserId(Long userId);

  Optional<Account> findByIdAndUserId(Long id, Long userId);
}
