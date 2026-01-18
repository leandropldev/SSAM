package com.valeo.ssam.repository;

import com.valeo.ssam.entity.AssetToken;
import jakarta.persistence.LockModeType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssetTokenRepository extends JpaRepository<@NonNull AssetToken, @NonNull UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @NonNull
    Optional<AssetToken> findById(UUID id);
}
