package com.rij.amethyst_dev.jsons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AllowOnServer {
    private int id;
    private boolean status;
}
