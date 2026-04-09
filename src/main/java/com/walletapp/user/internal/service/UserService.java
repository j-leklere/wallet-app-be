package com.walletapp.user.internal.service;

import com.walletapp.shared.exception.InvalidPasswordException;
import com.walletapp.shared.exception.ResourceNotFoundException;
import com.walletapp.shared.exception.UserAlreadyExistsException;
import com.walletapp.user.internal.domain.User;
import com.walletapp.user.internal.mapper.UserMapper;
import com.walletapp.user.internal.repository.UserRepository;
import com.walletapp.user.web.request.UpdateUserRequest;
import com.walletapp.user.web.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserResponse findById(Long id) {
    return userMapper.toResponse(getOrThrow(id));
  }

  public UserResponse update(Long id, UpdateUserRequest request) {
    User user = getOrThrow(id);

    if (request.username() != null && !request.username().equals(user.getUsername())) {
      if (userRepository.existsByUsername(request.username())) {
        throw new UserAlreadyExistsException("Username already exists");
      }
      user.setUsername(request.username());
    }

    if (request.email() != null && !request.email().equals(user.getEmail())) {
      if (userRepository.existsByEmail(request.email())) {
        throw new UserAlreadyExistsException("Email already exists");
      }
      user.setEmail(request.email());
    }

    if (request.newPassword() != null && !request.newPassword().isBlank()) {
      if (request.currentPassword() == null
          || !passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
        throw new InvalidPasswordException("La contraseña actual es incorrecta");
      }
      user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    return userMapper.toResponse(userRepository.save(user));
  }

  private User getOrThrow(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
  }
}
