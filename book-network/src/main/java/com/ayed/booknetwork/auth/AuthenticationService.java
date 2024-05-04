package com.ayed.booknetwork.auth;

import com.ayed.booknetwork.email.EmailService;
import com.ayed.booknetwork.email.EmailTemplateName;
import com.ayed.booknetwork.role.RoleRepository;
import com.ayed.booknetwork.security.JwtService;
import com.ayed.booknetwork.user.Token;
import com.ayed.booknetwork.user.TokenRepository;
import com.ayed.booknetwork.user.User;
import com.ayed.booknetwork.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final EmailService emailService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @Value("${application.mailing.frontend.activation-url}")
  private String activationUrl;

  public void register(RegistrationRequest request) throws MessagingException {
    var userRole =
        roleRepository
            .findByName("USER")
            .orElseThrow(() -> new IllegalArgumentException("ROLE USER was not found"));

    var user =
        User.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .accountLocked(false)
            .enabled(false)
            .roles(List.of(userRole))
            .build();

    userRepository.save(user);
    sendValidationEmail(user);
  }

  private void sendValidationEmail(User user) throws MessagingException {
    var newToken = generateAndSaveActivationToken(user);
    String EMAIL_SUBJECT = "account activation";
    emailService.sendEmail(
        user.getEmail(),
        user.getUsername(),
        EmailTemplateName.ACTIVATE_ACCOUNT,
        activationUrl,
        newToken,
        EMAIL_SUBJECT);
  }

  private String generateAndSaveActivationToken(User user) {
    String generatedToken = generateActivationToken(6);
    var token =
        Token.builder()
            .token(generatedToken)
            .createdAt(LocalDateTime.now())
            .expiredAt(LocalDateTime.now().plusMinutes(15))
            .user(user)
            .build();
    tokenRepository.save(token);
    return generatedToken;
  }

  private String generateActivationToken(int length) {
    String characters = "0123456789";
    StringBuilder codeBuilder = new StringBuilder();
    SecureRandom secureRandom = new SecureRandom();
    for (int i = 0; i < length; i++) {
      int randomIndex = secureRandom.nextInt(characters.length());
      codeBuilder.append(characters.charAt(randomIndex));
    }
    return codeBuilder.toString();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    var authenticate =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    var claims = new HashMap<String, Object>();
    var user = ((User) authenticate.getPrincipal());
    claims.put("fullName", user.fullName());
    var jwtToken = jwtService.generateToken(claims, user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }

  // @Transactional
  public void activateAccount(String token) throws MessagingException {
    Token savedToken =
        tokenRepository
            .findByToken(token)
            .orElseThrow(() -> new RuntimeException("Token not found"));
    if (LocalDateTime.now().isAfter(savedToken.getExpiredAt())) {
      sendValidationEmail(savedToken.getUser());
      throw new RuntimeException("Activation token has expired. A new token has been send!");
    }

    var user =
        userRepository
            .findById(savedToken.getUser().getId())
            .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    user.setEnabled(true);
    userRepository.save(user);
    savedToken.setValidatedAt(LocalDateTime.now());
    tokenRepository.save(savedToken);
  }
}
