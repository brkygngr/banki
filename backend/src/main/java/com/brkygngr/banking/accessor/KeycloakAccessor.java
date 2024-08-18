package com.brkygngr.banking.accessor;

import com.brkygngr.banking.dto.keycloak.KeycloakClientResponse;
import com.brkygngr.banking.dto.keycloak.KeycloakRealmResponse;
import com.brkygngr.banking.dto.keycloak.KeycloakTokenResponse;
import com.brkygngr.banking.entity.User;
import com.brkygngr.banking.exception.KeycloakException;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class KeycloakAccessor {

  private final RestTemplate restTemplate;
  private final String keycloakUrl;
  private final String adminClientId;
  private final String adminRealm;
  private final String adminUsername;
  private final String adminPassword;
  private final String backendRealm;
  private final String backendClientId;
  private final String backendClientSecret;

  public KeycloakAccessor(final RestTemplate restTemplate,
                          @Value("${keycloak.url}") final String keycloakUrl,
                          @Value("${keycloak.clientId}") final String adminClientId,
                          @Value("${keycloak.admin.realm}") final String adminRealm,
                          @Value("${keycloak.admin.username}") final String adminUsername,
                          @Value("${keycloak.admin.password}") final String adminPassword,
                          @Value("${keycloak.backend.realm}") final String backendRealm,
                          @Value("${keycloak.backend.clientId}") final String backendClientId,
                          @Value("${keycloak.backend.clientSecret}") final String backendClientSecret) {
    this.restTemplate = restTemplate;
    this.keycloakUrl = keycloakUrl;
    this.adminClientId = adminClientId;
    this.adminRealm = adminRealm;
    this.adminUsername = adminUsername;
    this.adminPassword = adminPassword;
    this.backendRealm = backendRealm;
    this.backendClientId = backendClientId;
    this.backendClientSecret = backendClientSecret;
  }

  @PostConstruct
  public void postConstruct() {
    log.info("Initializing keycloak.");

    HttpHeaders headers = getHttpHeaders(getAccessToken(adminRealm, createAdminTokenRequestBody()));

    createRealmIfNotExists(headers);
    createClientIfNotExists(headers);
    disableVerifyProfile(headers);
  }

  public void registerUser(final User user) {
    HttpHeaders headers = getHttpHeaders(getAccessToken(adminRealm, createAdminTokenRequestBody()));

    Map<String, Object> userMap = createRegisterUserBody(user);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(userMap, headers);

    String registerUserUrl = keycloakUrl + "/admin/realms/" + backendRealm + "/users";

    restTemplate.postForEntity(registerUserUrl, request, Void.class);
  }

  public KeycloakTokenResponse loginUser(final User user) {
    String postUserUrl = keycloakUrl + "/realms/" + backendRealm + "/protocol/openid-connect/token";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> userTokenMap = createUserTokenRequestBody(user);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(userTokenMap, headers);

    ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(postUserUrl,
                                                                                request,
                                                                                KeycloakTokenResponse.class);

    return response.getBody();
  }

  private HttpHeaders getHttpHeaders(final String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(token);

    return headers;
  }

  private Map<String, Object> createRegisterUserBody(final User user) {
    return Map.of("username",
                  user.getUsername(),
                  "email",
                  user.getEmail(),
                  "enabled",
                  true,
                  "credentials",
                  Collections.singletonList(Map.of("type",
                                                   "password",
                                                   "value",
                                                   user.getPassword(),
                                                   "temporary",
                                                   false)));
  }

  private MultiValueMap<String, String> createAdminTokenRequestBody() {
    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add("grant_type", "password");
    requestBody.add("client_id", adminClientId);
    requestBody.add("username", adminUsername);
    requestBody.add("password", adminPassword);

    return requestBody;
  }

  private MultiValueMap<String, String> createUserTokenRequestBody(final User user) {
    MultiValueMap<String, String> userTokenMap = new LinkedMultiValueMap<>();
    userTokenMap.add("grant_type", "password");
    userTokenMap.add("client_id", backendClientId);
    userTokenMap.add("client_secret", backendClientSecret);
    userTokenMap.add("username", user.getUsername());
    userTokenMap.add("password", user.getPassword());
    userTokenMap.add("email", user.getEmail());
    return userTokenMap;
  }

  private void createRealmIfNotExists(final HttpHeaders headers) {
    String realmsUrl = keycloakUrl + "/admin/realms";

    ResponseEntity<KeycloakRealmResponse[]> response = restTemplate.exchange(realmsUrl,
                                                                             HttpMethod.GET,
                                                                             new HttpEntity<>(headers),
                                                                             KeycloakRealmResponse[].class);

    if (response.getBody() == null) {
      throw new KeycloakException("app.keycloak.realms.not.found");
    }

    boolean isRealmExists = false;

    for (KeycloakRealmResponse keycloakRealmResponse : response.getBody()) {
      if (keycloakRealmResponse.realm().equals(backendRealm)) {
        isRealmExists = true;
        break;
      }
    }

    if (!isRealmExists) {
      log.info("Keycloak realm does not exists. Creating realm.");

      Map<String, Object> realmMap = Map.of("realm", backendRealm, "enabled", true);

      restTemplate.postForEntity(realmsUrl, new HttpEntity<>(realmMap, headers), Void.class);

      log.info("Keycloak realm created.");
    } else {
      log.info("Keycloak realm already exists. Returning without creating the realm.");
    }
  }

  private void createClientIfNotExists(final HttpHeaders headers) {
    String clientsUrl = keycloakUrl + "/admin/realms/" + backendRealm + "/clients";

    ResponseEntity<KeycloakClientResponse[]> response = restTemplate.exchange(clientsUrl,
                                                                              HttpMethod.GET,
                                                                              new HttpEntity<>(headers),
                                                                              KeycloakClientResponse[].class);

    if (response.getBody() == null) {
      throw new KeycloakException("app.keycloak.clients.not.found");
    }

    boolean isClientExists = false;

    for (KeycloakClientResponse keycloakClientResponse : response.getBody()) {
      if (keycloakClientResponse.clientId().equals(backendClientId)) {
        isClientExists = true;
        break;
      }
    }

    if (!isClientExists) {
      log.info("Keycloak client does not exists. Creating client.");

      Map<String, Object> clientMap = Map.of("clientId",
                                             backendClientId,
                                             "secret",
                                             backendClientSecret,
                                             "clientAuthenticatorType",
                                             "client-secret",
                                             "protocol",
                                             "openid-connect",
                                             "publicClient",
                                             false,
                                             "directAccessGrantsEnabled",
                                             true,
                                             "serviceAccountsEnabled",
                                             true,
                                             "enabled",
                                             true);

      restTemplate.postForEntity(clientsUrl, new HttpEntity<>(clientMap, headers), Void.class);

      log.info("Keycloak client created.");
    } else {
      log.info("Keycloak client already exists. Returning without creating the client.");
    }
  }

  private void disableVerifyProfile(final HttpHeaders headers) {
    log.info("Keycloak disabling verify profile.");

    String authReqActUrl = keycloakUrl
        + "/admin/realms/"
        + backendRealm
        + "/authentication/required-actions/VERIFY_PROFILE";

    Map<String, Object> verifyProfileMap = Map.of("alias",
                                                  "VERIFY_PROFILE",
                                                  "name",
                                                  "Verify Profile",
                                                  "providerId",
                                                  "VERIFY_PROFILE",
                                                  "enabled",
                                                  false,
                                                  "defaultAction",
                                                  false,
                                                  "priority",
                                                  10,
                                                  "config",
                                                  Map.of());

    restTemplate.exchange(authReqActUrl, HttpMethod.PUT, new HttpEntity<>(verifyProfileMap, headers), Void.class);

    log.info("Keycloak disabled verify profile.");
  }

  private String getAccessToken(final String realm, final MultiValueMap<String, String> tokenFormBody) {
    String tokenUri = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(tokenFormBody, headers);

    ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(tokenUri,
                                                                                request,
                                                                                KeycloakTokenResponse.class);

    if (response.getBody() == null) {
      throw new KeycloakException("app.keycloak.admin.token.invalid");
    }

    return response.getBody().accessToken();
  }
}
