package com.valeo.ssam.service;

import com.valeo.ssam.entity.AssetToken;
import com.valeo.ssam.entity.StatusEnum;
import com.valeo.ssam.repository.AssetTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class TokenCleanupTaskService {

    private final AssetTokenRepository repository;

    public TokenCleanupTaskService(AssetTokenRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 30000) // a cada 30s
    public void cleanup() {
        List<AssetToken> tokens = repository.findAll();
        tokens.stream()
                .filter(t -> t.getStatus() == StatusEnum.IN_TERMINATION)
                .filter(t -> t.getUpdateCounter() != null &&
                        Instant.now().minusSeconds(60)
                                .isAfter(Instant.ofEpochSecond(t.getUpdateCounter())))
                .forEach(repository::delete);
    }

}
