package org.arig.robot.services;

import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.balise.EtalonnageBalise;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.model.balise.StatutBalise.DetectionResult;
import org.arig.robot.model.communication.balise.enums.DirectionGirouette;
import org.arig.robot.system.capteurs.IVisionBalise;
import org.arig.robot.utils.EcueilUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

public class VisionBaliseBouchon implements IVisionBalise {

    @Autowired
    private NerellRobotStatus rs;

    private DetectionResult detectionResult = new DetectionResult();

    @Override
    public boolean startDetection() {
        Random random = new Random();
        int randomGirouette = random.nextInt(3);
        int randomEcueil = random.nextInt(3) + 1;

        detectionResult = new DetectionResult();
        detectionResult.setDirection(DirectionGirouette.values()[randomGirouette]);
        detectionResult.setEcueil(EcueilUtils.couleurDetectees(EcueilUtils.tirageCommunAdverse(rs.getTeam(), randomEcueil)));

        return true;
    }

    @Override
    public EtalonnageBalise etalonnage(int[][] ecueil, int[][] bouees) {
        return null;
    }

    @Override
    public StatutBalise getStatut() {
        StatutBalise status = new StatutBalise();
        status.setDetection(detectionResult);
        return status;
    }

    @Override
    public String getPhoto() {
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

    @Override
    public void idle() {
    }
}
