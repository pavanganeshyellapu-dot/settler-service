package com.settler.domain.ledger.repository;

import com.settler.domain.ledger.entity.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LedgerRepository extends JpaRepository<Ledger, UUID> {
}
