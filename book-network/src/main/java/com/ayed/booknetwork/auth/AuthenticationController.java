package com.ayed.booknetwork.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  /**
   * Endpoint to register a new user.
   *
   * @param registrationRequest The registration request containing user information.
   * @return ResponseEntity with status ACCEPTED if registration is successful.
   * @throws MessagingException If an error occurs during the registration process.
   */
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @Operation(summary = "Register a new user", description = "Endpoint to register a new user.")
  @ApiResponse(responseCode = "202", description = "Registration request accepted")
  public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest registrationRequest)
      throws MessagingException {
    authenticationService.register(registrationRequest);
    return ResponseEntity.accepted().build();
  }

  /**
   * Authenticates a user.
   *
   * @param authenticationRequest The authentication request containing user credentials.
   * @return ResponseEntity containing the authentication response.
   */
  @Operation(summary = "Authenticate user", description = "Endpoint to authenticate a user.")
  @ApiResponse(
      responseCode = "200",
      description = "Successfully authenticated",
      content = @Content(schema = @Schema(implementation = AuthenticationResponse.class)))
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody @Valid AuthenticationRequest authenticationRequest) {
    return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
  }

  @GetMapping("/activate-account")
  public void confirm(@RequestParam String token) throws MessagingException {
    authenticationService.activateAccount(token);
  }
}
