package com.walletapp.recurringtransaction.internal.mapper;

import com.walletapp.recurringtransaction.internal.domain.RecurringTransaction;
import com.walletapp.recurringtransaction.web.response.RecurringTransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecurringTransactionMapper {

  @Mapping(source = "originalCurrency.id", target = "originalCurrencyId")
  @Mapping(source = "originalCurrency.code", target = "originalCurrencyCode")
  @Mapping(source = "originalCurrency.symbol", target = "originalCurrencySymbol")
  @Mapping(source = "account.id", target = "accountId")
  @Mapping(source = "account.name", target = "accountName")
  @Mapping(source = "category.id", target = "categoryId")
  @Mapping(source = "category.name", target = "categoryName")
  RecurringTransactionResponse toResponse(RecurringTransaction recurringTransaction);
}
