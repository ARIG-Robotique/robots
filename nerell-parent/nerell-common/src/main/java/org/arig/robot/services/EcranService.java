package org.arig.robot.services;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.PhotoResponse;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.ecran.GetConfigInfos;
import org.arig.robot.model.ecran.UpdateMatchInfos;
import org.arig.robot.model.ecran.UpdatePhotoInfos;
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
    private NerellRobotStatus rs;

    @Autowired
    private IEcran ecran;

    @Getter
    @Accessors(fluent = true)
    private GetConfigInfos config;

    private boolean matchHasRunned = false;

    private final UpdateStateInfos stateInfos = new UpdateStateInfos();
    private final UpdateMatchInfos matchInfos = new UpdateMatchInfos();
    private final SignalEdgeFilter updatePhotoFilter = new SignalEdgeFilter(false, Type.RISING);
    private final SignalEdgeFilter doEtalonnageFilter = new SignalEdgeFilter(false, Type.RISING);

    public void process() {
        if (rs.matchEnabled() && !matchHasRunned) {
            matchHasRunned = true;
        }

        if (matchHasRunned) {
            updateMatch();

        } else {
            updateStatus();

            if (baliseService.isConnected()) {
                if (updatePhotoFilter.filter(config.isUpdatePhoto())) {
                    // sur front montant de "updatePhoto" on prend une photo et l'envoie à l'écran
                    PhotoResponse photo = baliseService.getPhoto();
                    UpdatePhotoInfos query = new UpdatePhotoInfos();
                    query.setMessage(photo == null ? "Erreur inconnue" : photo.getErrorMessage());
                    query.setPhoto(photo == null ? null : photo.getDatas());
                    ecran.updatePhoto(query);

                } else if (doEtalonnageFilter.filter(config.isEtalonnageBalise())) {
                    // sur front montant de "etalonnageBalise" on lance l'étalonnage et envoie le résultat à l'écran
                    EtalonnageResponse etalonnage = baliseService.etalonnage();
                    UpdatePhotoInfos query = new UpdatePhotoInfos();
                    query.setMessage(etalonnage == null ? "Erreur inconnue" : etalonnage.getErrorMessage());
                    query.setPhoto(etalonnage == null ? null : etalonnage.getDatas());
                    ecran.updatePhoto(query);
                }
            }
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

    private void updateStatus() {
        stateInfos.setI2c(ii2CManager.status());
        stateInfos.setLidar(lidarService.isConnected());
        stateInfos.setBalise(baliseService.isConnected());
        stateInfos.setPhare(true); // TODO
        stateInfos.setAu(ioService.auOk());
        stateInfos.setAlim12v(ioService.alimPuissance12VOk());
        stateInfos.setAlim5vp(ioService.alimPuissance5VOk());
        stateInfos.setTirette(ioService.tirette());

        ecran.updateState(stateInfos);
        config = ecran.configInfos();
    }

    private void updateMatch() {
        matchInfos.setScore(rs.calculerPoints());
        if (rs.matchEnabled()) {
            matchInfos.setMessage(String.format("%s (%s restantes) - %s s", strategyManager.getCurrentAction(), strategyManager.actionsCount(), rs.getRemainingTime() / 1000));
        }

        ecran.updateMatch(matchInfos);
    }

}
