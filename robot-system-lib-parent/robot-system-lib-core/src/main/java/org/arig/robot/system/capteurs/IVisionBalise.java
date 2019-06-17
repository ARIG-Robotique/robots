package org.arig.robot.system.capteurs;

import org.arig.robot.model.balise.StatutBalise;

public interface IVisionBalise {

    void startEtallonage();

    void startDetection();

    StatutBalise getStatut();

    byte[] getPhoto(int width);

    void openSocket() throws Exception;

    boolean isOpen();

    void end();

}
