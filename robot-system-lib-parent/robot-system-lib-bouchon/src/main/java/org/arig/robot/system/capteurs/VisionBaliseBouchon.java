package org.arig.robot.system.capteurs;

import org.arig.robot.model.balise.StatutBalise;

public class VisionBaliseBouchon implements IVisionBalise {

    @Override
    public void startDetection() {
    }

    @Override
    public StatutBalise getStatut() {
        return new StatutBalise();
    }

    @Override
    public byte[] getPhoto(final int width) {
        return new byte[0];
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
