package org.arig.robot.communication.socket.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataResponseMetadata {

  private Boolean contacting;
  private Boolean intact;
  private Long lastContactStartAge;
  private Long lastContactEndAge;
  private Long timeSpentNear;

}
