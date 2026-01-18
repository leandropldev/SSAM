package com.valeo.ssam.model;

public record CreateAssetToken(
        String ownerId,
        String confidentialData
) { }
