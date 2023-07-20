package com.rij.amethyst_dev.jsons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Donation {
    @JsonProperty("pubId")
    private String pubId;

    @JsonProperty("clientName")
    private String clientName;

    @JsonProperty("message")
    private String message;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("source")
    private String source;

    @JsonProperty("goal")
    private String goal;

    @JsonProperty("isPublished")
    private boolean isPublished;

    @JsonProperty("createdAt")
    private long createdAt;
}
