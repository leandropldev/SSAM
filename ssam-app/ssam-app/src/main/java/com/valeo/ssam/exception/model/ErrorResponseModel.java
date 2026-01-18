package com.valeo.ssam.exception.model;

import java.time.LocalDateTime;

public record ErrorResponseModel (
    String message,
    int status,
    LocalDateTime timestamp
){ }
