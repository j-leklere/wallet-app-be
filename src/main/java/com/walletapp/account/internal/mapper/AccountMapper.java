package com.walletapp.account.internal.mapper;

import com.walletapp.account.internal.domain.Account;
import com.walletapp.account.web.response.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

  @Mapping(source = "currency.id", target = "currencyId")
  @Mapping(source = "currency.code", target = "currencyCode")
  @Mapping(source = "currency.symbol", target = "currencySymbol")
  AccountResponse toResponse(Account account);
}
