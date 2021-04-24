package org.arig.robot.system.capteurs;

import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.model.balise.StatutBalise.DetectionResult;
import org.arig.robot.model.communication.balise.enums.EDirectionGirouette;
import org.arig.robot.utils.EcueilUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

public class VisionBaliseBouchon extends AbstractVisionBaliseBouchon<StatutBalise> {

    @Autowired
    private EurobotStatus rs;

    private DetectionResult detectionResult = new DetectionResult();

    @Override
    public boolean startDetection() {
        Random random = new Random();
        int randomGirouette = random.nextInt(3);
        int randomEcueil = random.nextInt(3) + 1;

        detectionResult = new DetectionResult();
        detectionResult.setGirouette(EDirectionGirouette.values()[randomGirouette]);
        detectionResult.setEcueilEquipe(EcueilUtils.couleurDetectees(EcueilUtils.tirageCommunEquipe(rs.team(), randomEcueil)));
        detectionResult.setEcueilAdverse(EcueilUtils.couleurDetectees(EcueilUtils.tirageCommunAdverse(rs.team(), randomEcueil)));

        return true;
    }

    @Override
    public StatutBalise getStatut() {
        StatutBalise status = new StatutBalise();
        status.setDetection(detectionResult);
        return status;
    }

}
