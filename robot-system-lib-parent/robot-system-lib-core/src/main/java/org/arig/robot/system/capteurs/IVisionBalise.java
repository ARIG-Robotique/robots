package org.arig.robot.system.capteurs;

import org.arig.robot.model.balise.EtalonnageBalise;
import org.arig.robot.model.balise.StatutBalise;

public interface IVisionBalise {

    StatutBalise getStatut();
    String getPhoto(int width);
    boolean startDetection();
    EtalonnageBalise etalonnage(int[][] ecueil, int[][] bouees);
    void openSocket() throws Exception;
    boolean isOpen();
    void end();

}
