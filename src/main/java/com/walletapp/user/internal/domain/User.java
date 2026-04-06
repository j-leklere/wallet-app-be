package com.walletapp.user.internal.domain;

import com.walletapp.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class User extends BaseEntity implements UserDetails {

  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false)
  private String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  public static User create(String username, String email, String encodedPassword) {
    User user = new User();
    user.username = username;
    user.email = email;
    user.password = encodedPassword;
    return user;
  }
}
