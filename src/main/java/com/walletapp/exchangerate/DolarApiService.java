package com.walletapp.exchangerate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class DolarApiService {

  private static final String DOLAR_API_URL = "https://dolarapi.com/v1/dolares/oficial";
  private static final Duration TTL = Duration.ofMinutes(30);

  private final RestClient restClient;

  private volatile BigDecimal cachedRate;
  private volatile Instant cacheExpiry = Instant.EPOCH;

  public DolarApiService(RestClient.Builder builder) {
    this.restClient = builder.build();
  }

  /**
   * Returns the USD → ARS midpoint rate for the given date. Currently falls back to the live cached
   * rate (historical endpoint not implemented).
   */
  public BigDecimal getUsdToArsRate(java.time.LocalDate date) {
    return getUsdToArsRate();
  }

  /** Returns the current USD → ARS midpoint rate (oficial). Cached for 30 minutes. */
  public BigDecimal getUsdToArsRate() {
    if (Instant.now().isAfter(cacheExpiry)) {
      refresh();
    }
    return cachedRate;
  }

  private synchronized void refresh() {
    if (Instant.now().isBefore(cacheExpiry)) return; // double-checked under lock

    try {
      DolarApiResponse resp =
          restClient.get().uri(DOLAR_API_URL).retrieve().body(DolarApiResponse.class);

      if (resp == null || resp.compra() == null || resp.venta() == null) {
        throw new IllegalStateException("DolarAPI returned empty response");
      }

      cachedRate = resp.compra().add(resp.venta()).divide(BigDecimal.TWO, 4, RoundingMode.HALF_UP);
      cacheExpiry = Instant.now().plus(TTL);

      log.info("DolarAPI rate refreshed: 1 USD = {} ARS", cachedRate);

    } catch (Exception e) {
      log.error("Failed to fetch DolarAPI rate: {}", e.getMessage());
      if (cachedRate == null) {
        throw new RuntimeException("Could not obtain USD/ARS exchange rate", e);
      }
      // Keep stale rate, retry next call
      cacheExpiry = Instant.now().plus(Duration.ofMinutes(5));
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record DolarApiResponse(BigDecimal compra, BigDecimal venta) {}
}
