package com.walletapp.transaction.internal.mapper;

import com.walletapp.transaction.internal.domain.Transaction;
import com.walletapp.transaction.web.response.TransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  @Mapping(source = "originalCurrency.id", target = "originalCurrencyId")
  @Mapping(source = "originalCurrency.code", target = "originalCurrencyCode")
  @Mapping(source = "originalCurrency.symbol", target = "originalCurrencySymbol")
  @Mapping(source = "referenceCurrency.id", target = "referenceCurrencyId")
  @Mapping(source = "referenceCurrency.code", target = "referenceCurrencyCode")
  @Mapping(source = "referenceCurrency.symbol", target = "referenceCurrencySymbol")
  @Mapping(source = "account.id", target = "accountId")
  @Mapping(source = "account.name", target = "accountName")
  @Mapping(source = "category.id", target = "categoryId")
  @Mapping(source = "category.name", target = "categoryName")
  TransactionResponse toResponse(Transaction transaction);
}
