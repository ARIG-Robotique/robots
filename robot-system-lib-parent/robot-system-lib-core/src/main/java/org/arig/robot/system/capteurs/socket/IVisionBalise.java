package org.arig.robot.system.capteurs.socket;

import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.PhotoResponse;

import java.io.Serializable;

public interface IVisionBalise<STATUT extends Serializable> {

    STATUT getStatut();
    PhotoResponse getPhoto();
    EtalonnageResponse etalonnage();
    boolean startDetection();
    void openSocket() throws Exception;
    boolean isOpen();
    void end();
    void idle();
    void heartbeat();

}
