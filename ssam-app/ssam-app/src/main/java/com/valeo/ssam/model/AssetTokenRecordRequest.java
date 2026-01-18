package com.valeo.ssam.model;

import com.valeo.ssam.entity.AssetToken;
import com.valeo.ssam.entity.StatusEnum;

import java.util.UUID;

public record AssetTokenRecordRequest(
        String ownerId,
        String confidentialData
) {

    public AssetToken toEntity() {
        AssetToken entity = new AssetToken();
        entity.setStatus(StatusEnum.ACTIVE);
        entity.setOwnerId(this.ownerId);
        entity.setConfidentialData(this.confidentialData);
        entity.setSlotBitmap((byte) 0xFF); //by default token starts with 8 free slots
        entity.setUpdateCounter(0L);
        return entity;
    }


}
