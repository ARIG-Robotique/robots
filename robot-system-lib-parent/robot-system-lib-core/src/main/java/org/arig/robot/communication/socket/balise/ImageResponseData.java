package org.arig.robot.communication.socket.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageResponseData implements Serializable {

  private List<Camera> cameras;

  @Getter
  @EqualsAndHashCode
  @RequiredArgsConstructor
  public static class Camera implements Serializable {

    private String name;
    private String data;

  }

}
