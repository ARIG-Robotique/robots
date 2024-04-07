package org.arig.robot.services;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.ecran.AbstractEcranConfig;
import org.arig.robot.model.ecran.AbstractEcranState;
import org.arig.robot.model.ecran.EcranMatchInfo;
import org.arig.robot.model.ecran.EcranParams;
import org.arig.robot.model.ecran.EcranPhoto;
import org.arig.robot.strategy.StrategyManager;
import org.arig.robot.system.capteurs.socket.IEcran;
import org.arig.robot.system.capteurs.socket.IVisionBalise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;

@Slf4j
public abstract class AbstractEcranService<CONFIG extends AbstractEcranConfig, STATE extends AbstractEcranState> {

    @Autowired
    private IOService ioService;

    @Autowired
    private I2CManager i2CManager;

    @Autowired
    private StrategyManager strategyManager;

    @Autowired
    private LidarService lidarService;

    @Autowired
    private AbstractRobotStatus rs;

    @Autowired
    private AbstractEnergyService energyService;

    @Autowired(required = false)
    private IVisionBalise<?> balise;

    @Autowired
    private IEcran<CONFIG, STATE> ecran;

    @Getter
    @Accessors(fluent = true)
    private CONFIG config;

    private boolean matchHasRunned = false;
    private boolean paramsSend = false;

    private final STATE stateInfos;
    private final EcranMatchInfo matchInfos = new EcranMatchInfo();

    public AbstractEcranService(STATE stateInfos) {
        this.stateInfos = stateInfos;
    }

    protected abstract EcranParams getParams();

    public void process() {
        if (!paramsSend) {
            paramsSend = ecran.setParams(getParams());
        }
        if (!paramsSend) {
            return;
        }

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

    public void updateStateInfo(STATE stateInfos) {
        stateInfos.setI2c(i2CManager.scanStatus());
        stateInfos.setLidar(lidarService.isConnected());
        stateInfos.setAu(ioService.auOk());
        stateInfos.setAlimMoteurs(energyService.checkMoteurs(false));
        stateInfos.setAlimServos(energyService.checkServos(false));
        stateInfos.setTirette(ioService.tirette());
        stateInfos.setBalise(balise != null && balise.isOpen());
        stateInfos.setOtherRobot(rs.groupOk());
    }

    public void updatePhoto(EcranPhoto query) {
        ecran.updatePhoto(query);
    }

    private void updateMatch() {
        matchInfos.setScore(rs.calculerPoints());
        if (rs.matchEnabled()) {
            if (rs.groupOk()) {
                matchInfos.setMessage(String.format("%s / %s (%s restantes) - %ss",
                        ObjectUtils.firstNonNull(rs.currentAction(), "AUCUNE"),
                        ObjectUtils.firstNonNull(rs.otherCurrentAction(), "AUCUNE"),
                        strategyManager.actionsCount(),
                        rs.getRemainingTime() / 1000)
                );
            } else {
                matchInfos.setMessage(String.format("%s (%s restantes) - %ss",
                        ObjectUtils.firstNonNull(rs.currentAction(), "AUCUNE"),
                        strategyManager.actionsCount(),
                        rs.getRemainingTime() / 1000)
                );
            }
        }

        ecran.updateMatch(matchInfos);
    }

}
