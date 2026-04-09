package com.walletapp.category.internal.domain;

import java.util.Set;

public final class CategoryConstants {

  public static final Set<String> ALLOWED_ICONS =
      Set.of(
          "wallet",
          "credit-card",
          "piggy-bank",
          "trending-up",
          "coins",
          "banknote",
          "utensils",
          "coffee",
          "shopping-cart",
          "pizza",
          "apple",
          "wine",
          "car",
          "bus",
          "plane",
          "bike",
          "fuel",
          "train",
          "home",
          "tv",
          "zap",
          "wrench",
          "lightbulb",
          "heart",
          "activity",
          "pill",
          "stethoscope",
          "music",
          "gamepad-2",
          "film",
          "book",
          "dumbbell",
          "briefcase",
          "laptop",
          "building-2",
          "pen-line",
          "graduation-cap",
          "shopping-bag",
          "baby",
          "shirt",
          "tag");

  public static final Set<String> ALLOWED_COLORS =
      Set.of(
          "violet", "purple", "indigo", "blue", "cyan", "teal", "emerald", "green", "lime",
          "yellow", "amber", "orange", "red", "rose", "pink", "slate");

  public static final String DEFAULT_ICON = "tag";
  public static final String DEFAULT_COLOR = "violet";

  private CategoryConstants() {}
}
