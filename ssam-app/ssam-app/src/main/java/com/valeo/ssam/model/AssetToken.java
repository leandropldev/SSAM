package com.valeo.ssam.model;

import com.valeo.ssam.converter.ConfidentialDataConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Entity
@Data
public class AssetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private String ownerId;

    @Convert(converter = ConfidentialDataConverter.class)
    private String confidentialData;

    private byte slotBitmap; // 8 bits para slots

    @Version
    private Long updateCounter;

    // Relacionamento com tokens filhos
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private java.util.List<AssetToken> children;

    @ManyToOne
    private AssetToken parent;

    // Getters/Setters
}