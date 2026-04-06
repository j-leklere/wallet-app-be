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
  @Mapping(source = "category.id", target = "categoryId")
  RecurringTransactionResponse toResponse(RecurringTransaction recurringTransaction);
}
