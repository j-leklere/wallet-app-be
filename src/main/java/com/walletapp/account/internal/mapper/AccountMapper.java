package com.walletapp.account.internal.mapper;

import com.walletapp.account.internal.domain.Account;
import com.walletapp.account.web.response.AccountResponse;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

  @Mapping(source = "account.currency.id", target = "currencyId")
  @Mapping(source = "account.currency.code", target = "currencyCode")
  @Mapping(source = "account.currency.symbol", target = "currencySymbol")
  @Mapping(source = "balance", target = "balance")
  AccountResponse toResponse(Account account, BigDecimal balance);
}
