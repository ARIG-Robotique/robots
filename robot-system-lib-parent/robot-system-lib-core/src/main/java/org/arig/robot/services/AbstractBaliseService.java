package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.ConfigQueryData;
import org.arig.robot.communication.socket.balise.EmptyResponse;
import org.arig.robot.communication.socket.balise.IdleQueryData;
import org.arig.robot.communication.socket.balise.IdleResponse;
import org.arig.robot.communication.socket.balise.ImageQueryData;
import org.arig.robot.communication.socket.balise.ImageResponse;
import org.arig.robot.communication.socket.balise.ImageResponseData;
import org.arig.robot.communication.socket.balise.StatusResponse;
import org.arig.robot.communication.socket.balise.TeamQueryData;
import org.arig.robot.communication.socket.balise.enums.BaliseMode;
import org.arig.robot.system.capteurs.socket.IVisionBalise;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Slf4j
public abstract class AbstractBaliseService<DATA extends Serializable> {

    @Autowired
    protected IVisionBalise<DATA> balise;

    protected boolean isOK = false;

    protected boolean idle = true;

    protected BaliseMode mode = BaliseMode.MILLIMETER_2D;

    protected String team = "";

    public boolean startDetection() {
        if (isOK && !isIdling()) return true;

        if (updateStatus() &&
            (!isIdling() || exitIdle())
        ) {
            log.info("Initialisation de la balise réussie");
            return true;
        } else {
            return false;
        }
    }

    public void stopDetection() {
        balise.end();
    }

    public boolean configure(BaliseMode mode) {
        EmptyResponse response = balise.setConfig(new ConfigQueryData(mode));

        if (response == null) {
            isOK = false;
            return false;
        }

        return response.isOk();
    }

    public boolean isOK() {
        return isOK;
    }

    public boolean updateStatus() {
        StatusResponse response = balise.getStatus();

        if (response == null || !response.isOk() || response.getData() == null) {
            isOK = false;
            return false;
        }

        idle = response.getData().getIdle();
        team = response.getData().getTeam();
        mode = response.getData().getMode();

        isOK = response.getData().isAllOK();
        return isOK;
    }

    public boolean setTeam(String newTeam) {
        EmptyResponse response = balise.setTeam(new TeamQueryData(newTeam));

        if (response == null) {
            isOK = false;
            return false;
        }

        return response.isOk();
    }

    abstract public void updateData();

    public ImageResponseData getImage() {
        ImageResponse response = balise.getImage(new ImageQueryData());

        if (response == null) {
            isOK = false;
            return null;
        }

        if (!response.isOk() || response.getData() == null) {
            return null;
        }

        return response.getData();
    }

    public boolean isIdling() {
        return idle;
    }

    public void idle() {
        idle = true;
        balise.setIdle(new IdleQueryData(true));
    }

    public boolean exitIdle() {
        IdleResponse response = balise.setIdle(new IdleQueryData(false));

        if (response == null) {
            isOK = false;
            return false;
        }

        idle = !response.isOk();
        return idle;
    }

    public boolean sendKeepAlive() {
        EmptyResponse response = balise.keepAlive();

        if (response == null) {
            isOK = false;
            return false;
        }

        return response.isOk();
    }

}
