package com.walletapp.dashboard.web.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardAnalyticsResponse(
    Summary summary, List<ChartPoint> chart, List<CategoryExpense> categoryBreakdown) {

  public record Summary(BigDecimal income, BigDecimal expenses, BigDecimal netSavings) {}

  public record ChartPoint(int year, int month, BigDecimal income, BigDecimal expenses) {}

  public record CategoryExpense(String name, BigDecimal amount, BigDecimal percentage) {}
}
