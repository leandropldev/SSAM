package com.valeo.ssam.service;

import com.valeo.ssam.entity.AssetToken;
import com.valeo.ssam.entity.StatusEnum;
import com.valeo.ssam.exception.GenericException;
import com.valeo.ssam.model.CreateAssetToken;
import com.valeo.ssam.model.AssetTokenResponse;
import com.valeo.ssam.repository.AssetTokenRepository;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AssetTokenService {

    private final AssetTokenRepository repository;

    public AssetTokenService(AssetTokenRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UUID createNewToken(CreateAssetToken request) {
        AssetToken entity = new AssetToken();
        entity.setStatus(StatusEnum.ACTIVE);
        entity.setOwnerId(request.ownerId());
        entity.setConfidentialData(request.confidentialData());
        entity.setSlotBitmap((byte) 0x00); //by default token starts with 8 free slots
        entity.setUpdateCounter(0L);
        entity.setLastStatusChange(Instant.now());
        return repository.save(entity).getId();
    }

    @Transactional
    public List<AssetTokenResponse> listAllTokens(){
        return repository.findAll().stream()
                .map(AssetTokenResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ResponseEntity<@NonNull AssetToken> getAssetTokenById(UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    public void suspendToken(UUID id){
        AssetToken token = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invalid token ID"));
        token.setStatus(StatusEnum.SUSPENDED);
        repository.save(token);
    }

    @Transactional
    public UUID shareToken(UUID parentId, String friendEmail) {
        AssetToken parent = repository.findById(parentId)
                    .orElseThrow(() -> new GenericException("Invalid TokenID"));

        // bitmask logic
        for (int i = 0; i < 8; i++) {
            if ((parent.getSlotBitmap() & (1 << i)) == 0) {
                parent.setSlotBitmap((byte) (parent.getSlotBitmap() | (1 << i)));

                AssetToken child = new AssetToken();
                child.setOwnerId(friendEmail);
                child.setStatus(StatusEnum.ACTIVE);
                child.setConfidentialData(parent.getConfidentialData());
                child.setParent(parent);
                child.setUpdateCounter(0L);
                child.setLastStatusChange(Instant.now());

                parent.getChildren().add(child);
                return repository.save(parent).getId();
            }
        }
        throw new GenericException("No available slots");
    }

    @Transactional
    public void terminateToken(UUID tokenId) {
        AssetToken token = repository.findById(tokenId)
                .orElseThrow(() -> new GenericException("Invalid TokenID!"));
        token.setStatus(StatusEnum.TERMINATED);

        // Cascading revocation
        token.getChildren().forEach(child -> {
            child.setStatus(StatusEnum.IN_TERMINATION);
            child.setLastStatusChange(Instant.now());
        });

        repository.save(token);
    }
}
