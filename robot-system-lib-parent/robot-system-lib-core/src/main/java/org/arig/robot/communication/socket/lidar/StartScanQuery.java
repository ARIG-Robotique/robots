package org.arig.robot.communication.socket.lidar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.lidar.enums.LidarAction;
import org.arig.robot.model.lidar.SpeedInfos;

@Data
@EqualsAndHashCode(callSuper = true)
public class StartScanQuery extends AbstractQueryWithData<LidarAction, SpeedInfos> {

  public StartScanQuery() {
    this(LidarAction.START_SCAN);
  }

  protected StartScanQuery(LidarAction action) {
    super(action);
  }

  public void setSpeed(Short speed) {
    if (speed == null) {
      return;
    }

    if (!hasData()) {
      setData(new SpeedInfos());
    }
    getData().setSpeed(speed);
  }
}
