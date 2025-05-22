package org.arig.robot.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(fluent = true)
@RequiredArgsConstructor
public class ConstructionElementSource {

  private final Face face;
  private final StockPosition stockPosition;

}
