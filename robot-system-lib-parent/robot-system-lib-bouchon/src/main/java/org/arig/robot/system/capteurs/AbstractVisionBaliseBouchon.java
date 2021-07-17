package org.arig.robot.system.capteurs;

import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.PhotoResponse;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;
import org.arig.robot.communication.socket.enums.StatusResponse;

import java.io.Serializable;

public abstract class AbstractVisionBaliseBouchon<STATUT extends Serializable> implements IVisionBalise<STATUT> {

    @Override
    public boolean startDetection() {
        return true;
    }

    @Override
    public EtalonnageResponse etalonnage() {
        EtalonnageResponse response = new EtalonnageResponse();
        response.setStatus(StatusResponse.OK);
        response.setAction(BaliseAction.ETALONNAGE);
        response.setData("");
        return response;
    }

    @Override
    public PhotoResponse getPhoto() {
        PhotoResponse response = new PhotoResponse();
        response.setStatus(StatusResponse.OK);
        response.setAction(BaliseAction.PHOTO);
        response.setData("");
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
