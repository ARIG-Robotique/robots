package org.arig.robot.services;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.ecran.GetConfigInfos;
import org.arig.robot.model.ecran.UpdateMatchInfos;
import org.arig.robot.model.ecran.UpdateStateInfos;
import org.arig.robot.strategy.StrategyManager;
import org.arig.robot.system.ILidarService;
import org.arig.robot.system.capteurs.IEcran;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EcranService {

    @Autowired
    private IIOService ioService;

    @Autowired
    private II2CManager ii2CManager;

    @Autowired
    private StrategyManager strategyManager;

    @Autowired
    private BaliseService baliseService;

    @Autowired
    private ILidarService lidarService;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IEcran ecran;

    @Getter
    @Accessors(fluent = true)
    private GetConfigInfos config;

    private boolean matchHasRunned = false;

    private final UpdateStateInfos stateInfos = new UpdateStateInfos();
    private final UpdateMatchInfos matchInfos = new UpdateMatchInfos();

    public void process() {
        if (rs.isMatchEnabled() && !matchHasRunned) {
            matchHasRunned = true;
        }

        if (matchHasRunned) {
            updateMatch();
        } else {
            updateStatus();
        }
    }

    public void displayMessage(String message) {
        if (!StringUtils.equals(stateInfos.getMessage(), message)) {
            displayMessage(message, LogLevel.INFO);
        }
    }

    public void displayMessage(String message, LogLevel logLevel) {
        if (logLevel == LogLevel.INFO) log.info(message);
        else if (logLevel == LogLevel.WARN) log.warn(message);
        else if (logLevel == LogLevel.ERROR) log.error(message);

        stateInfos.setMessage(message);
        matchInfos.setMessage(message);
    }

    private void updateStatus() {
        stateInfos.setI2c(ii2CManager.status());
        stateInfos.setLidar(lidarService.isConnected());
        stateInfos.setBalise(baliseService.isConnected());
        stateInfos.setPhare(false); // TODO
        stateInfos.setAu(ioService.auOk());
        stateInfos.setAlim12v(ioService.alimPuissance12VOk());
        stateInfos.setAlim5vp(ioService.alimPuissance5VOk());
        stateInfos.setTirette(ioService.tirette());

        ecran.updateState(stateInfos);
        config = ecran.configInfos();
    }

    private void updateMatch() {
        matchInfos.setScore(rs.calculerPoints());
        if (rs.isMatchEnabled()) {
            matchInfos.setMessage(String.format("%s (%s restantes) - %s s", strategyManager.getCurrentAction(), strategyManager.actionsCount(), rs.getRemainingTime() / 1000));
        }

        ecran.updateMatch(matchInfos);
    }
}
