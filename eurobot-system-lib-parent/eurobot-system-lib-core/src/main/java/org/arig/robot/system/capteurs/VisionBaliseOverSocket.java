package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.StatutResponse;
import org.arig.robot.model.balise.StatutBalise;

import java.io.File;

@Slf4j
public class VisionBaliseOverSocket extends AbstractVisionBaliseOverSocket<StatutBalise> {

    public VisionBaliseOverSocket(String hostname, Integer port) {
        super(hostname, port, StatutResponse.class);
    }

    public VisionBaliseOverSocket(File socketFile) {
        super(socketFile, StatutResponse.class);
    }

}
