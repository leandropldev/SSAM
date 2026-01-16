package com.valeo.ssam.controller;

import com.valeo.ssam.model.AssetToken;
import com.valeo.ssam.model.StatusEnum;
import com.valeo.ssam.repository.AssetTokenRepository;
import com.valeo.ssam.service.AssetTokenService;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tokens")
public class SsamController {

    private final AssetTokenService service;
    private final AssetTokenRepository repository;

    public SsamController(AssetTokenService service, AssetTokenRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    // 🔹 Listar todos os tokens
    @GetMapping
    public ResponseEntity<@NonNull List<AssetToken>> listTokens() {
        return ResponseEntity.ok(repository.findAll());
    }

    // 🔹 Obter token por ID
    @GetMapping("/{id}")
    public ResponseEntity<@NonNull AssetToken> getToken(@PathVariable UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Compartilhar token com amigo
    @PostMapping("/{parentId}/share")
    public ResponseEntity<@NonNull AssetToken> shareToken(
            @PathVariable UUID parentId,
            @RequestParam String friendEmail) {
        AssetToken child = service.shareToken(parentId, friendEmail);
        return ResponseEntity.ok(child);
    }

    // 🔹 Suspender token
    @PostMapping("/{id}/suspend")
    public ResponseEntity<@NonNull Void> suspendToken(@PathVariable UUID id) {
        AssetToken token = repository.findById(id).orElseThrow();
        token.setStatus(StatusEnum.SUSPENDED);
        repository.save(token);
        return ResponseEntity.ok().build();
    }

    // 🔹 Terminar token (com cascata)
    @PostMapping("/{id}/terminate")
    public ResponseEntity<@NonNull Void> terminateToken(@PathVariable UUID id) {
        service.terminateToken(id);
        return ResponseEntity.ok().build();
    }

}
