package org.arig.robot.services;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.ecran.GetConfigInfos;
import org.arig.robot.model.ecran.UpdateMatchInfos;
import org.arig.robot.model.ecran.UpdatePhotoInfos;
import org.arig.robot.model.ecran.UpdateStateInfos;
import org.arig.robot.strategy.StrategyManager;
import org.arig.robot.system.capteurs.IEcran;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;

@Slf4j
public class AbstractEcranService {

    @Autowired
    private IIOService ioService;

    @Autowired
    private II2CManager ii2CManager;

    @Autowired
    private StrategyManager strategyManager;

    @Autowired
    private LidarService lidarService;

    @Autowired
    private AbstractRobotStatus rs;

    @Autowired(required = false)
    private AbstractBaliseService baliseService;

    @Autowired
    private AbstractEnergyService energyService;

    @Autowired
    private IEcran ecran;

    @Getter
    @Accessors(fluent = true)
    private GetConfigInfos config;

    private boolean matchHasRunned = false;

    private final UpdateStateInfos stateInfos = new UpdateStateInfos();
    private final UpdateMatchInfos matchInfos = new UpdateMatchInfos();

    public void process() {
        if (rs.matchEnabled() && !matchHasRunned) {
            matchHasRunned = true;
        }

        if (matchHasRunned) {
            updateMatch();

        } else {
            updateStateInfo(stateInfos);
            ecran.updateState(stateInfos);
            config = ecran.configInfos();
        }
    }

    public void displayMessage(String message) {
        displayMessage(message, LogLevel.INFO);
    }

    public void displayMessage(String message, LogLevel logLevel) {
        if (!StringUtils.equals(stateInfos.getMessage(), message)) {
            if (logLevel == LogLevel.INFO) log.info(message);
            else if (logLevel == LogLevel.WARN) log.warn(message);
            else if (logLevel == LogLevel.ERROR) log.error(message);

            stateInfos.setMessage(message);
            matchInfos.setMessage(message);
        }
    }

    public void updateStateInfo(UpdateStateInfos stateInfos) {
        stateInfos.setI2c(ii2CManager.status());
        stateInfos.setLidar(lidarService.isConnected());
        stateInfos.setPhare(true); // TODO
        stateInfos.setAu(ioService.auOk());
        stateInfos.setAlim12v(energyService.checkMoteurs(false));
        stateInfos.setAlim5vp(energyService.checkServos(false));
        stateInfos.setTirette(ioService.tirette());
        stateInfos.setBalise(baliseService != null && baliseService.isConnected());
        stateInfos.setOtherRobot(rs.groupOk());
    }

    public void updatePhoto(UpdatePhotoInfos query) {
        ecran.updatePhoto(query);
    }

    private void updateMatch() {
        matchInfos.setScore(rs.calculerPoints());
        if (rs.matchEnabled()) {
            matchInfos.setMessage(String.format("%s (%s restantes) - %s s", rs.currentAction(), strategyManager.actionsCount(), rs.getRemainingTime() / 1000));
        }

        ecran.updateMatch(matchInfos);
    }

}
