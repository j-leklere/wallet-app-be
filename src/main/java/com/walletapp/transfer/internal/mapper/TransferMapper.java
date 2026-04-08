package com.walletapp.transfer.internal.mapper;

import com.walletapp.transfer.internal.domain.Transfer;
import com.walletapp.transfer.web.response.TransferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferMapper {

  @Mapping(source = "fromCurrency.id", target = "fromCurrencyId")
  @Mapping(source = "fromCurrency.code", target = "fromCurrencyCode")
  @Mapping(source = "fromCurrency.symbol", target = "fromCurrencySymbol")
  @Mapping(source = "toCurrency.id", target = "toCurrencyId")
  @Mapping(source = "toCurrency.code", target = "toCurrencyCode")
  @Mapping(source = "toCurrency.symbol", target = "toCurrencySymbol")
  @Mapping(source = "fromAccount.id", target = "fromAccountId")
  @Mapping(source = "fromAccount.name", target = "fromAccountName")
  @Mapping(source = "toAccount.id", target = "toAccountId")
  @Mapping(source = "toAccount.name", target = "toAccountName")
  TransferResponse toResponse(Transfer transfer);
}
