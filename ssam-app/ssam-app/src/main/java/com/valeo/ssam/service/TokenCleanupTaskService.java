package com.valeo.ssam.service;

import com.valeo.ssam.entity.AssetToken;
import com.valeo.ssam.entity.StatusEnum;
import com.valeo.ssam.repository.AssetTokenRepository;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@Log
public class TokenCleanupTaskService {

    private final AssetTokenRepository repository;

    public TokenCleanupTaskService(AssetTokenRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 30000) //run at each 30 sec
    public void cleanup() {
        log.info("Starting a new cleanup service.");
        Instant cutoff = Instant.now().minusSeconds(60);
        List<AssetToken> tokens = repository.findAll();
        tokens.stream()
                .filter(t -> t.getStatus() == StatusEnum.IN_TERMINATION)
                .filter(t -> t.getUpdateCounter() != null &&
                        t.getLastStatusChange().isBefore(cutoff))
                .forEach(repository::delete);
    }

}
