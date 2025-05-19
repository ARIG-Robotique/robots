package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.AbstractBaliseResponseWithData;
import org.arig.robot.communication.socket.balise.DataQueryData;
import org.arig.robot.communication.socket.balise.DataResponse;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.balise.BaliseData;
import org.arig.robot.model.balise.Data2D;
import org.arig.robot.model.balise.Data3D;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class VisionBaliseBouchon extends AbstractVisionBaliseBouchon<BaliseData> {

  @Autowired
  private EurobotStatus rs;

  private final Random random = new Random();

  @Override
  public AbstractBaliseResponseWithData<BaliseData> getData(DataQueryData<?> queryData) {
    DataResponse response = new DataResponse();

    response.setIndex(0);
    response.setAction(BaliseAction.IMAGE);
    response.setStatus(org.arig.robot.communication.socket.enums.StatusResponse.OK);

    List<Data2D> data2D = List.of(new Data2D());
    List<Data3D> data3D = new ArrayList<>();

    BaliseData data = new BaliseData(data2D, data3D);

    response.setData(data);

    return response;
  }

}
