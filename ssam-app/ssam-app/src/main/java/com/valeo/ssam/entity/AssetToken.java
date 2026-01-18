package com.valeo.ssam.entity;

import com.valeo.ssam.converter.ConfidentialDataConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "asset_token")
@AllArgsConstructor
@NoArgsConstructor
public class AssetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private String ownerId;

    @Convert(converter = ConfidentialDataConverter.class)
    private String confidentialData;

    private byte slotBitmap; // 8 bits children slots

    @Version //Every time the entity change, the update counter will get a new version
    private Long updateCounter;

    // Relation to child's token
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<AssetToken> children;

    @ManyToOne
    private AssetToken parent;
}