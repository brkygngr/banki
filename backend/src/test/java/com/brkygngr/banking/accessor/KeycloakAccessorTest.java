package com.brkygngr.banking.accessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.brkygngr.banking.dto.keycloak.KeycloakClientResponse;
import com.brkygngr.banking.dto.keycloak.KeycloakRealmResponse;
import com.brkygngr.banking.dto.keycloak.KeycloakTokenResponse;
import com.brkygngr.banking.entity.User;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class KeycloakAccessorTest {

  @Captor
  ArgumentCaptor<HttpEntity<Map<String, Object>>> requestCaptor;

  private AutoCloseable autoCloseable;

  @Mock
  private RestTemplate restTemplate;

  private KeycloakAccessor keycloakAccessor;

  @BeforeEach
  void setUp() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    keycloakAccessor = new KeycloakAccessor(
        restTemplate,
        "http://localhost:8080",
        "admin-client",
        "admin-realm",
        "admin",
        "password",
        "backend-realm",
        "backend-client",
        "backend-secret"
    );
  }

  @AfterEach
  void tearDown() throws Exception {
    autoCloseable.close();
  }

  @Test
  void registerUser_givenUser_thenRegistersUserToKeycloak() {
    User user = new User();
    user.setUsername("test-user");
    user.setEmail("test@example.com");
    user.setPassword("password");

    KeycloakTokenResponse tokenResponse = new KeycloakTokenResponse("token", 1, "refresh_token", 2);

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
        .thenReturn(ResponseEntity.ok().build());
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class)))
        .thenReturn(ResponseEntity.ok(tokenResponse));

    keycloakAccessor.registerUser(user);

    verify(restTemplate, times(1)).postForEntity(anyString(), requestCaptor.capture(), eq(Void.class));

    HttpEntity<Map<String, Object>> request = requestCaptor.getValue();

    assertNotNull(request.getBody());

    Map<String, Object> body = request.getBody();

    assertEquals("test-user", body.get("username"));
    assertEquals("test@example.com", body.get("email"));
  }

  @Test
  void loginUser_givenUser_thenReturnsUserKeycloakToken() {
    User user = new User();
    user.setUsername("test-user");
    user.setPassword("password");

    KeycloakTokenResponse tokenResponse = new KeycloakTokenResponse("token", 1, "refresh_token", 2);

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class)))
        .thenReturn(ResponseEntity.ok(tokenResponse));

    KeycloakTokenResponse response = keycloakAccessor.loginUser(user);

    assertEquals("token", response.accessToken());
  }

  @Test
  void postConstruct_whenRealmExist_thenDoesNotCallKeycloak() {
    KeycloakRealmResponse[] realms = {new KeycloakRealmResponse("backend-realm")};

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class)))
        .thenReturn(ResponseEntity.ok(new KeycloakTokenResponse("token", 1, "refresh_token", 2)));
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                               eq(KeycloakRealmResponse[].class)))
        .thenReturn(ResponseEntity.ok(realms));
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                               eq(KeycloakClientResponse[].class)))
        .thenReturn(ResponseEntity.ok(new KeycloakClientResponse[]{new KeycloakClientResponse("backend-client")}));
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
        .thenReturn(ResponseEntity.ok().build());

    keycloakAccessor.postConstruct();

    verify(restTemplate, never()).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
  }

  @Test
  void postConstruct_whenRealmDoesNotExist_thenCallsKeycloak() {
    KeycloakRealmResponse[] realms = {};

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class)))
        .thenReturn(ResponseEntity.ok(new KeycloakTokenResponse("token", 1, "refresh_token", 2)));
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                               eq(KeycloakRealmResponse[].class)))
        .thenReturn(ResponseEntity.ok(realms));
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
        .thenReturn(ResponseEntity.ok().build());
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                               eq(KeycloakClientResponse[].class)))
        .thenReturn(ResponseEntity.ok(new KeycloakClientResponse[]{new KeycloakClientResponse("backend-client")}));
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
        .thenReturn(ResponseEntity.ok().build());

    keycloakAccessor.postConstruct();

    verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
  }

  @Test
  void postConstruct_whenClientExist_thenDoesNotCallKeycloak() {
    KeycloakClientResponse[] clients = {new KeycloakClientResponse("backend-client")};

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class)))
        .thenReturn(ResponseEntity.ok(new KeycloakTokenResponse("token", 1, "refresh_token", 2)));
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                               eq(KeycloakRealmResponse[].class)))
        .thenReturn(ResponseEntity.ok(new KeycloakRealmResponse[]{new KeycloakRealmResponse("backend-realm")}));
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
        .thenReturn(ResponseEntity.ok().build());
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                               eq(KeycloakClientResponse[].class)))
        .thenReturn(ResponseEntity.ok(clients));

    keycloakAccessor.postConstruct();

    verify(restTemplate, never()).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
  }

  @Test
  void postConstruct_whenClientDoesNotExist_thenCallsKeycloak() {
    KeycloakClientResponse[] clients = {};

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class)))
        .thenReturn(ResponseEntity.ok(new KeycloakTokenResponse("token", 1, "refresh_token", 2)));
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                               eq(KeycloakRealmResponse[].class)))
        .thenReturn(ResponseEntity.ok(new KeycloakRealmResponse[]{new KeycloakRealmResponse("backend-realm")}));
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
        .thenReturn(ResponseEntity.ok().build());
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                               eq(KeycloakClientResponse[].class)))
        .thenReturn(ResponseEntity.ok(clients));
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
        .thenReturn(ResponseEntity.ok().build());

    keycloakAccessor.postConstruct();

    verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
  }

  @Test
  void postConstruct_disablesKeycloakVerifyProfile() {
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class)))
        .thenReturn(ResponseEntity.ok(new KeycloakTokenResponse("token", 1, "refresh_token", 2)));
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                               eq(KeycloakRealmResponse[].class)))
        .thenReturn(ResponseEntity.ok(new KeycloakRealmResponse[]{new KeycloakRealmResponse("backend-realm")}));
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                               eq(KeycloakClientResponse[].class)))
        .thenReturn(ResponseEntity.ok(new KeycloakClientResponse[]{new KeycloakClientResponse("backend-client")}));
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
        .thenReturn(ResponseEntity.ok().build());

    HttpHeaders headers = new HttpHeaders();

    Map<String, Object> verifyProfileMap = Map.of(
        "alias", "VERIFY_PROFILE",
        "name", "Verify Profile",
        "providerId", "VERIFY_PROFILE",
        "enabled", false,
        "defaultAction", false,
        "priority", 10,
        "config", Map.of()
    );

    when(restTemplate.exchange(
        anyString(),
        eq(HttpMethod.PUT),
        any(),
        eq(Void.class)
    )).thenReturn(ResponseEntity.noContent().build());

    keycloakAccessor.postConstruct();

    verify(restTemplate, times(1)).exchange(
        anyString(),
        eq(HttpMethod.PUT),
        argThat(entity -> entity.getBody() != null && entity.getBody().equals(verifyProfileMap)),
        eq(Void.class)
    );
  }
}