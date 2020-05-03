package org.arig.robot.system.capteurs;

import org.arig.robot.model.balise.EtalonnageBalise;
import org.arig.robot.model.balise.StatutBalise;

public class VisionBaliseBouchon implements IVisionBalise {

    @Override
    public boolean startDetection() {
        return true;
    }

    @Override
    public EtalonnageBalise etalonnage(int[][] ecueil, int[][] bouees) {
        return null;
    }

    @Override
    public StatutBalise getStatut() {
        return new StatutBalise();
    }

    @Override
    public String getPhoto(int width) {
        return "";
    }

    @Override
    public void openSocket() throws Exception {
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void end() {
    }
}
