package com.walletapp.dashboard.web;

import com.walletapp.auth.internal.service.AuthService;
import com.walletapp.dashboard.internal.service.DashboardService;
import com.walletapp.dashboard.web.response.DashboardAnalyticsResponse;
import com.walletapp.dashboard.web.response.DashboardSnapshotResponse;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;
  private final AuthService authService;

  /**
   * Datos dependientes del período seleccionado: summary, gráfico e ingresos/gastos por categoría.
   * dateFrom y dateTo son opcionales; por defecto se usan el primer y último día del mes actual.
   */
  @GetMapping("/analytics")
  public ResponseEntity<DashboardAnalyticsResponse> getAnalytics(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo,
      @RequestParam(required = false) Long currencyId,
      @RequestParam(required = false, defaultValue = "false") boolean consolidate) {

    LocalDate today = LocalDate.now();
    LocalDate from = dateFrom != null ? dateFrom : today.with(TemporalAdjusters.firstDayOfMonth());
    LocalDate to = dateTo != null ? dateTo : today.with(TemporalAdjusters.lastDayOfMonth());

    return ResponseEntity.ok(
        dashboardService.getAnalytics(
            authService.getCurrentUserId(), from, to, currencyId, consolidate));
  }

  /**
   * Datos estáticos del período: cuentas con balance, últimas transacciones y próximos recurrentes.
   * No depende de ningún filtro de fecha.
   */
  @GetMapping("/snapshot")
  public ResponseEntity<DashboardSnapshotResponse> getSnapshot() {
    return ResponseEntity.ok(dashboardService.getSnapshot(authService.getCurrentUserId()));
  }
}
