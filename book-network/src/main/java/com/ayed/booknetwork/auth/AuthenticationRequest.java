package com.ayed.booknetwork.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationRequest {
  @NotEmpty(message = "Email is required")
  @NotBlank(message = "Email is required")
  @Email(message = "Email is not valid")
  private String email;

  @NotEmpty(message = "Password is required")
  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password should be 8 chars long min")
  @Size(max = 16, message = "Password should be 16 chars long max")
  private String password;
}
