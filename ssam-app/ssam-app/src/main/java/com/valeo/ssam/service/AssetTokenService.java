package com.valeo.ssam.service;

import com.valeo.ssam.model.AssetToken;
import com.valeo.ssam.model.StatusEnum;
import com.valeo.ssam.repository.AssetTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AssetTokenService {

    private final AssetTokenRepository repository;

    public AssetTokenService(AssetTokenRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AssetToken shareToken(UUID parentId, String friendEmail) {
        AssetToken parent = repository.findById(parentId).get();

        // Encontrar primeiro slot livre
        for (int i = 0; i < 8; i++) {
            if ((parent.getSlotBitmap() & (1 << i)) == 0) {
                parent.setSlotBitmap((byte) (parent.getSlotBitmap() | (1 << i)));

                AssetToken child = new AssetToken();
                child.setId(UUID.randomUUID());
                child.setOwnerId(friendEmail);
                child.setStatus(StatusEnum.ACTIVE);
                child.setParent(parent);

                parent.getChildren().add(child);
                repository.save(parent);
                return child;
            }
        }
        throw new RuntimeException("No available slots");
    }

    @Transactional
    public void terminateToken(UUID tokenId) {
        AssetToken token = repository.findById(tokenId).get();
        token.setStatus(StatusEnum.TERMINATED);

        // Cascading revocation
        token.getChildren().forEach(child -> child.setStatus(StatusEnum.IN_TERMINATION));

        repository.save(token);
    }
}
