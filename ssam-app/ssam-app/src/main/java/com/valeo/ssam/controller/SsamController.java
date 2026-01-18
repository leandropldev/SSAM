package com.valeo.ssam.controller;

import com.valeo.ssam.entity.AssetToken;
import com.valeo.ssam.model.AssetTokenResponse;
import com.valeo.ssam.model.CreateAssetToken;
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

    @PostMapping
    public ResponseEntity<@NonNull UUID> createToken(@RequestBody CreateAssetToken request){
        return ResponseEntity.ok(service.createNewToken(request));
    }

    @GetMapping
    public ResponseEntity<@NonNull List<AssetTokenResponse>> listTokens() {
        return ResponseEntity.ok(service.listAllTokens());
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull AssetToken> getToken(@PathVariable UUID id) {
        return service.getAssetTokenById(id);
    }

    //TODO: implement a mapper to prevent infinity loop parent->child->parent
    @PostMapping("/{parentId}/share")
    public ResponseEntity<@NonNull AssetToken> shareToken(
            @PathVariable UUID parentId,
            @RequestParam String friendEmail) {
        AssetToken child = service.shareToken(parentId, friendEmail);
        return ResponseEntity.ok(child);
    }

    //TODO: button frontend <Suspend>
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<@NonNull Void> suspendToken(@PathVariable UUID id) {
        service.suspendToken(id);
        return ResponseEntity.ok().build();
    }

    //TODO: IMPLEMENT A LOCALDATETIME TO AssetToken so it can track IN_TERMINATION timelife
    //TODO: button frontend <Terminate>
    @PostMapping("/{id}/terminate")
    public ResponseEntity<@NonNull Void> terminateToken(@PathVariable UUID id) {
        service.terminateToken(id);
        return ResponseEntity.ok().build();
    }
}
