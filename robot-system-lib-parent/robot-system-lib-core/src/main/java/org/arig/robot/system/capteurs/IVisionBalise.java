package org.arig.robot.system.capteurs;

import org.arig.robot.communication.socket.balise.DetectionResponse;
import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.PhotoResponse;
import org.arig.robot.model.balise.StatutBalise;

public interface IVisionBalise {

    StatutBalise getStatut();
    PhotoResponse getPhoto();
    DetectionResponse startDetection();
    EtalonnageResponse etalonnage();
    void openSocket() throws Exception;
    boolean isOpen();
    void end();
    void idle();
    void heartbeat();

}
