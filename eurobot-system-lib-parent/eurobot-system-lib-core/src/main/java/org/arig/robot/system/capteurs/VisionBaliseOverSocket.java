package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.DataResponse;
import org.arig.robot.model.balise.BaliseData;
import org.arig.robot.system.capteurs.socket.AbstractVisionBaliseOverSocket;

import java.io.File;

@Slf4j
public class VisionBaliseOverSocket extends AbstractVisionBaliseOverSocket<BaliseData> {

    public VisionBaliseOverSocket(String hostname, Integer port) {
      super(hostname, port, DataResponse.class);
    }

    public VisionBaliseOverSocket(File socketFile) {
      super(socketFile, DataResponse.class);
    }

}
