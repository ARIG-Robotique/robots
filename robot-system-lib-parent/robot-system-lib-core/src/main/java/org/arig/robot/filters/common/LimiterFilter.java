package org.arig.robot.filters.common;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.filters.Filter;
import org.springframework.util.Assert;

public class LimiterFilter implements Filter<Double, Double> {

  public enum LimiterType {
    SIMPLE, MIRROR
  }

  public static final String MIN_VALUE_NULL_MESSAGE = "%s Min ne peut être null";
  public static final String MAX_VALUE_NULL_MESSAGE = "%s Max ne peut être null";
  public static final String POSITIVE_MESSAGE = "Doit être positif";
  public static final String NEGATIVE_MESSAGE = "Doit être négatif";
  public static final String MAX_GREATER_MIN_MESSAGE = "Positive max doit être supérieur a positive min";
  public static final String MAX_SMALLER_MIN_MESSAGE = "Negative max doit être inférieur à  negative min";

  private final Double positiveMin, positiveMax;
  private Double negativeMin, negativeMax;
  private final LimiterType type;

  @Getter
  @Accessors(fluent = true)
  private Double lastResult;

  public LimiterFilter(Double min, Double max) {
    this(min, max, LimiterType.SIMPLE);
  }

  public LimiterFilter(Double min, Double max, LimiterType type) {
    Assert.notNull(min, String.format(MIN_VALUE_NULL_MESSAGE, StringUtils.EMPTY));
    Assert.notNull(max, String.format(MAX_VALUE_NULL_MESSAGE, StringUtils.EMPTY));
    Assert.isTrue(max > min, MAX_GREATER_MIN_MESSAGE);

    positiveMin = min;
    positiveMax = max;
    this.type = type;
    if (type == LimiterType.MIRROR) {
      negativeMin = -min;
      negativeMax = -max;
    }
  }

  public LimiterFilter(Double positiveMin, Double positiveMax, Double negativeMin, Double negativeMax) {
    super();

    Assert.notNull(positiveMin, String.format(MIN_VALUE_NULL_MESSAGE, "Positive"));
    Assert.notNull(positiveMax, String.format(MAX_VALUE_NULL_MESSAGE, "Positive"));
    Assert.isTrue(positiveMax >= 0 && positiveMin >= 0, POSITIVE_MESSAGE);
    Assert.isTrue(positiveMax > positiveMin, MAX_GREATER_MIN_MESSAGE);

    Assert.notNull(negativeMin, String.format(MIN_VALUE_NULL_MESSAGE, "Negative"));
    Assert.notNull(negativeMax, String.format(MAX_VALUE_NULL_MESSAGE, "Negative"));
    Assert.isTrue(negativeMax < 0 && negativeMin < 0, NEGATIVE_MESSAGE);
    Assert.isTrue(negativeMax < negativeMin, MAX_SMALLER_MIN_MESSAGE);

    this.type = LimiterType.MIRROR;
    this.positiveMin = positiveMin;
    this.positiveMax = positiveMax;
    this.negativeMin = negativeMin;
    this.negativeMax = negativeMax;
  }

  @Override
  public Double filter(Double value) {
    Assert.notNull(value, FILTER_VALUE_NULL_MESSAGE);
    if (type == LimiterType.SIMPLE || (type == LimiterType.MIRROR && value >= 0)) {
      lastResult = value < positiveMin ? positiveMin : (value > positiveMax) ? positiveMax : value;
    } else {
      lastResult = value > negativeMin ? negativeMin : (value < negativeMax) ? negativeMax : value;
    }
    return lastResult;
  }
}
