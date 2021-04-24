package org.arig.robot.system.capteurs;

import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.PhotoResponse;

import java.io.Serializable;

public interface IVisionBalise<T extends Serializable> {

    T getStatut();
    PhotoResponse getPhoto();
    EtalonnageResponse etalonnage();
    boolean startDetection();
    void openSocket() throws Exception;
    boolean isOpen();
    void end();
    void idle();
    void heartbeat();

}
