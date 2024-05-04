package com.ayed.booknetwork.auth;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationRequest {
  @NotEmpty(message = "Firstname is required")
  @NotBlank(message = "Firstname is required")
  private String firstname;

  @NotEmpty(message = "Lastname is required")
  @NotBlank(message = "Lastname is required")
  private String lastname;

  private LocalDate dateOfBirth;

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
