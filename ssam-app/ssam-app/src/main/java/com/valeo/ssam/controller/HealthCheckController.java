package com.valeo.ssam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/health-check")
public class HealthCheckController {

    @Operation(
            summary = "App's health",
            description = "Check App's health",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Service up and running!",
                            content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    @GetMapping
    public String getStatus() {
        return "Service up and running!";
    }
}
