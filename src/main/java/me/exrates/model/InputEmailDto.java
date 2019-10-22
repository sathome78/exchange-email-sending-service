package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InputEmailDto {
    private String email;
    @JsonProperty("pub_id")
    private String pubId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPubId() {
        return pubId;
    }

    public void setPubId(String pubId) {
        this.pubId = pubId;
    }
}
