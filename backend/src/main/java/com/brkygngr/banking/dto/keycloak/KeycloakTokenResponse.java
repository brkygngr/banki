package com.brkygngr.banking.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakTokenResponse(@JsonProperty("access_token") String accessToken,
                                    @JsonProperty("expires_in") int expiresIn,
                                    @JsonProperty("refresh_token") String refreshToken,
                                    @JsonProperty("refresh_expires_in") int refreshExpiresIn) {

}
