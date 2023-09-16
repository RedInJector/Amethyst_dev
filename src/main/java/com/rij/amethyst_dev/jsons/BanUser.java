package com.rij.amethyst_dev.jsons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BanUser {
    private int id;
    private String reason;
    private String time;
    private boolean isPermaban;

    @JsonProperty("isPermaban")
    public boolean isPermaban() {
        return isPermaban;
    }
    @JsonProperty("isPermaban")
    public void setIsPermaban(boolean permaban) {
        isPermaban = permaban;
    }
}
