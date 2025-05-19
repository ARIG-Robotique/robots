package org.arig.robot.communication.socket.balise;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class DataQueryData<FILTER extends Enum<FILTER>> implements Serializable {

  private List<FILTER> filters;

  @SafeVarargs
  public DataQueryData(FILTER... filters) {
    this.filters = Stream.of(filters).filter(Objects::nonNull).collect(Collectors.toList());
  }

}
