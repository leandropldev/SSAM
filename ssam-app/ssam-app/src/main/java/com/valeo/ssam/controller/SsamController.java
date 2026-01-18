package com.valeo.ssam.controller;

import com.valeo.ssam.model.AssetToken;
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

    public SsamController(AssetTokenService service) {
        this.service = service;
    }

    // 🔹 Listar todos os tokens
    //TODO: criar um mapper para mostrar somente os campos do frontend
    @GetMapping
    public ResponseEntity<@NonNull List<AssetToken>> listTokens() {
        return ResponseEntity.ok(service.listAllTokens());
    }

    // 🔹 Obter token por ID
    @GetMapping("/{id}")
    public ResponseEntity<@NonNull AssetToken> getToken(@PathVariable UUID id) {
        return service.getAssetTokenById(id);
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
    //TODO: button frontend <Suspend>
    @PostMapping("/{id}/suspend")
    public ResponseEntity<@NonNull Void> suspendToken(@PathVariable UUID id) {
        service.suspendToken(id);
        return ResponseEntity.ok().build();
    }

    // 🔹 Terminar token (com cascata)
    //TODO: button frontend <Terminate>
    @PostMapping("/{id}/terminate")
    public ResponseEntity<@NonNull Void> terminateToken(@PathVariable UUID id) {
        service.terminateToken(id);
        return ResponseEntity.ok().build();
    }
}
