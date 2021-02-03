package org.arig.robot.services;

import org.arig.robot.communication.socket.balise.DetectionResponse;
import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.PhotoResponse;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;
import org.arig.robot.communication.socket.enums.StatusResponse;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.model.balise.StatutBalise.DetectionResult;
import org.arig.robot.model.communication.balise.enums.EDirectionGirouette;
import org.arig.robot.system.capteurs.IVisionBalise;
import org.arig.robot.utils.EcueilUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

public class VisionBaliseBouchon implements IVisionBalise {

    @Autowired
    private NerellRobotStatus rs;

    private DetectionResult detectionResult = new DetectionResult();

    @Override
    public DetectionResponse startDetection() {
        Random random = new Random();
        int randomGirouette = random.nextInt(3);
        int randomEcueil = random.nextInt(3) + 1;

        detectionResult = new DetectionResult();
        detectionResult.setGirouette(EDirectionGirouette.values()[randomGirouette]);
        detectionResult.setEcueilEquipe(EcueilUtils.couleurDetectees(EcueilUtils.tirageCommunEquipe(rs.team(), randomEcueil)));
        detectionResult.setEcueilAdverse(EcueilUtils.couleurDetectees(EcueilUtils.tirageCommunAdverse(rs.team(), randomEcueil)));

        DetectionResponse response = new DetectionResponse();
        response.setStatus(StatusResponse.OK);
        response.setAction(BaliseAction.DETECTION);

        return response;
    }

    @Override
    public EtalonnageResponse etalonnage() {
        EtalonnageResponse response = new EtalonnageResponse();
        response.setStatus(StatusResponse.OK);
        response.setAction(BaliseAction.ETALONNAGE);
        response.setDatas("");
        return response;
    }

    @Override
    public StatutBalise getStatut() {
        StatutBalise status = new StatutBalise();
        status.setDetection(detectionResult);
        return status;
    }

    @Override
    public PhotoResponse getPhoto() {
        PhotoResponse response = new PhotoResponse();
        response.setStatus(StatusResponse.OK);
        response.setAction(BaliseAction.PHOTO);
        response.setDatas("");
        return response;
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

    @Override
    public void heartbeat() {
    }
}
