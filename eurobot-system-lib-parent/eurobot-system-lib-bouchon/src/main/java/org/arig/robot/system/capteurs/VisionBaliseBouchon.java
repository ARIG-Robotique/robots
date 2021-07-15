package org.arig.robot.system.capteurs;

import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.model.balise.StatutBalise.DetectionResult;
import org.springframework.beans.factory.annotation.Autowired;

public class VisionBaliseBouchon extends AbstractVisionBaliseBouchon<StatutBalise> {

    @Autowired
    private EurobotStatus rs;

    private DetectionResult detectionResult = new DetectionResult();

    @Override
    public boolean startDetection() {
        detectionResult = new DetectionResult();
        return true;
    }

    @Override
    public StatutBalise getStatut() {
        StatutBalise status = new StatutBalise();
        status.setDetection(detectionResult);
        return status;
    }

}
