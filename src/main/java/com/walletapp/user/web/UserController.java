package com.walletapp.user.web;

import com.walletapp.auth.internal.service.AuthService;
import com.walletapp.user.internal.service.UserService;
import com.walletapp.user.web.request.UpdateUserRequest;
import com.walletapp.user.web.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final AuthService authService;

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getMe() {
    return ResponseEntity.ok(userService.findById(authService.getCurrentUserId()));
  }

  @PatchMapping("/me")
  public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateUserRequest request) {
    return ResponseEntity.ok(userService.update(authService.getCurrentUserId(), request));
  }
}
