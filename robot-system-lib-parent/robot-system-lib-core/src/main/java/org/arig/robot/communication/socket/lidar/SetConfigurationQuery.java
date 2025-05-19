package org.arig.robot.communication.socket.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.lidar.enums.LidarAction;
import org.arig.robot.model.lidar.ConfigurationInfos;

@Data
@EqualsAndHashCode(callSuper = true)
public class SetConfigurationQuery extends AbstractQueryWithData<LidarAction, ConfigurationInfos> {

  public SetConfigurationQuery() {
    super(LidarAction.SET_CONFIG);
  }

  public void setReversed(Boolean reversed) {
    if (reversed == null) {
      return;
    }

    if (!hasData()) {
      setData(new ConfigurationInfos());
    }
    getData().setReversed(reversed);
  }

  public void setAngleOffset(Integer angleOffset) {
    if (angleOffset == null) {
      return;
    }

    if (!hasData()) {
      setData(new ConfigurationInfos());
    }
    getData().setAngleOffset(angleOffset);
  }

  public void setExcludeLowerThan(Integer excludeLowerThan) {
    if (excludeLowerThan == null) {
      return;
    }

    if (!hasData()) {
      setData(new ConfigurationInfos());
    }
    getData().setExcludeLowerThanMm(excludeLowerThan);
  }

  public void setExcludeGreaterThan(Integer excludeGreaterThan) {
    if (excludeGreaterThan == null) {
      return;
    }

    if (!hasData()) {
      setData(new ConfigurationInfos());
    }
    getData().setExcludeGreaterThanMm(excludeGreaterThan);
  }
}
