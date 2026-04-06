package com.walletapp.transfer.internal.repository;

import com.walletapp.transfer.internal.domain.Transfer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransferRepository
    extends JpaRepository<Transfer, Long>, JpaSpecificationExecutor<Transfer> {

  List<Transfer> findAllByUserId(Long userId);

  Optional<Transfer> findByIdAndUserId(Long id, Long userId);
}
