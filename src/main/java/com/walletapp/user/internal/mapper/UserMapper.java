package com.walletapp.user.internal.mapper;

import com.walletapp.user.internal.domain.User;
import com.walletapp.user.web.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserResponse toResponse(User user);
}
