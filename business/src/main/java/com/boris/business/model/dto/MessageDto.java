package com.boris.business.model.dto;

import java.time.LocalDateTime;

public record MessageDto(

        String name,
        String text,
        LocalDateTime createdAt) {
}
