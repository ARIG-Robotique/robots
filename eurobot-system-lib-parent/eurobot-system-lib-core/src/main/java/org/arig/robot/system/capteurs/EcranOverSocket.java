package org.arig.robot.system.capteurs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.AbstractResponseWithData;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.model.ecran.EcranConfig;
import org.arig.robot.model.ecran.EcranState;
import org.arig.robot.system.capteurs.socket.AbstractEcranOverSocket;

import java.io.File;

@Slf4j
public class EcranOverSocket extends AbstractEcranOverSocket<EcranConfig, EcranState> {

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class EcranConfigInfoResponse extends AbstractResponseWithData<EcranAction, EcranConfig> {

    }

    public EcranOverSocket(String hostname, Integer port) {
        super(hostname, port, EcranConfigInfoResponse.class);
    }

    public EcranOverSocket(File socketFile) {
        super(socketFile, EcranConfigInfoResponse.class);
    }

}
