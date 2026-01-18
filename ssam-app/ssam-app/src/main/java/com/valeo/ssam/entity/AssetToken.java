package com.valeo.ssam.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.valeo.ssam.converter.ConfidentialDataConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "asset_token")
@AllArgsConstructor
@NoArgsConstructor
public class AssetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private String ownerId;

    @Convert(converter = ConfidentialDataConverter.class)
    private String confidentialData;

    private byte slotBitmap;

    @Version //Every time the entity change, the update counter will get a new version
    private Long updateCounter;

    private Instant lastStatusChange;

    // Relation to child's token
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<AssetToken> children;

    @ManyToOne
    @JsonIgnore
    private AssetToken parent;
}