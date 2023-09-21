package com.rij.amethyst_dev.DTO;

import org.springframework.http.HttpEntity;

public record PostCall(
        String url,
        HttpEntity<String> requestEntity
) {
}
