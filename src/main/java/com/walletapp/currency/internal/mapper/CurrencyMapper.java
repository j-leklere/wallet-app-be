package com.walletapp.currency.internal.mapper;

import com.walletapp.currency.internal.domain.Currency;
import com.walletapp.currency.web.response.CurrencyResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {

  CurrencyResponse toResponse(Currency currency);
}
