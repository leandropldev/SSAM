package com.valeo.ssam.model;

import com.valeo.ssam.entity.AssetToken;
import com.valeo.ssam.entity.StatusEnum;

import java.util.UUID;

public record AssetTokenResponse(
        UUID id,
        StatusEnum status,
        String ownerId,
        Long updateCounter
) {

    public static AssetTokenResponse fromEntity(AssetToken entity) {
        if (entity == null) {
            return null;
        }

        return new AssetTokenResponse(
            entity.getId(),
            entity.getStatus(),
            entity.getOwnerId(),
            entity.getUpdateCounter()
        );
    }

}
