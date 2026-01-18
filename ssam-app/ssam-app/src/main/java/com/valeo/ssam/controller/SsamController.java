package com.valeo.ssam.controller;

import com.valeo.ssam.entity.AssetToken;
import com.valeo.ssam.model.AssetTokenResponse;
import com.valeo.ssam.model.CreateAssetToken;
import com.valeo.ssam.service.AssetTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Creates a new Token",
            description = "Creates a new digital token linked to an Owner ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token successful created",
                            content = @Content(schema = @Schema(implementation = UUID.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping
    public ResponseEntity<@NonNull UUID> createToken(
            @RequestBody CreateAssetToken request) {
        return ResponseEntity.ok(service.createNewToken(request));
    }

    @Operation(
            summary = "List all tokens",
            description = "Return all tokens existences with basic information.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List successfully retrieved",
                            content = @Content(schema = @Schema(implementation = AssetTokenResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<@NonNull List<AssetTokenResponse>> listTokens() {
        return ResponseEntity.ok(service.listAllTokens());
    }

    @Operation(
            summary = "Get token by ID",
            description = "Return full details of a specific token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token found",
                            content = @Content(schema = @Schema(implementation = AssetToken.class))),
                    @ApiResponse(responseCode = "404", description = "Token NOT found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<@NonNull AssetToken> getToken(
            @Parameter(description = "Token's ID", required = true)
            @PathVariable UUID id) {
        return service.getAssetTokenById(id);
    }

    @Operation(
            summary = "Share token to a friend",
            description = "Creates a children token and inserts it in a parent's token free slot.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token successfully shared",
                            content = @Content(schema = @Schema(implementation = UUID.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid parent token or with no free slots available")
            }
    )
    @PostMapping("/{parentId}/share")
    public ResponseEntity<@NonNull UUID> shareToken(
            @Parameter(description = "Parent's token ID", required = true)
            @PathVariable UUID parentId,
            @Parameter(description = "Friend's email who will receive the token", required = true)
            @RequestParam String friendEmail) {

        return ResponseEntity.ok(service.shareToken(parentId, friendEmail));
    }

    @Operation(
            summary = "Suspend token",
            description = "Changes token status to SUSPENDED.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token successfully suspended"),
                    @ApiResponse(responseCode = "404", description = "Token not found")
            }
    )
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<@NonNull Void> suspendToken(
            @Parameter(description = "Token ID", required = true)
            @PathVariable UUID id) {

        service.suspendToken(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Terminate a token",
            description = "Changes token status to TERMINATED and update all children's to IN_TERMINATION.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token successfully TERMINATED"),
                    @ApiResponse(responseCode = "404", description = "Token not found")
            }
    )
    @PostMapping("/{id}/terminate")
    public ResponseEntity<@NonNull Void> terminateToken(
            @Parameter(description = "Token ID", required = true)
            @PathVariable UUID id) {

        service.terminateToken(id);
        return ResponseEntity.ok().build();
    }
}
