package com.ayed.booknetwork.user;

import com.ayed.booknetwork.book.Book;
import com.ayed.booknetwork.history.BookTransactionHistory;
import com.ayed.booknetwork.role.Role;
import jakarta.persistence.*;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {

  @Id @GeneratedValue private Long id;

  private String firstname;
  private String lastname;
  private LocalDate dateOfBirth;

  @Column(unique = true)
  private String email;

  private String password;
  private boolean accountLocked;
  private boolean enabled;

  @ManyToMany(fetch = FetchType.EAGER)
  private List<Role> roles;

  @OneToMany(mappedBy = "owner")
  private List<Book> books;

  @OneToMany(mappedBy = "user")
  private List<BookTransactionHistory> histories;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(insertable = false)
  private LocalDateTime lastModifiedDate;

  @Override
  public String getName() {
    return email;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).toList();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !accountLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public String fullName() {
    return firstname + " " + lastname;
  }
}
