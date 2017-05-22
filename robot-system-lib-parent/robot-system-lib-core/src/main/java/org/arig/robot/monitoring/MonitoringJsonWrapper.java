package org.arig.robot.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.MonitorPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author gdepuille on 11/10/16.
 */
@Slf4j
public class MonitoringJsonWrapper extends AbstractMonitoringWrapper {

    @Autowired
    private Environment env;

    @Autowired(required = false)
    private AbstractRobotStatus robotStatus = null;

    private File saveDirectory;

    public MonitoringJsonWrapper() {
        saveDirectory = new File("./logs");
        if (!saveDirectory.exists()) {
            log.info("Création du répertoire {} : {}", saveDirectory.getAbsolutePath(), saveDirectory.mkdirs());
        }
    }

    @Override
    public void addPoint(MonitorPoint point) {
        if (robotStatus == null || robotStatus.isMatchEnabled()) {
            forceAddPoint(point);
        }
    }

    @Override
    public void save() {
        if (!hasPoints()) {
            log.info("Aucun point de monitoring a enregistrer");
            return;
        }

        try {
            final String fileName = env.getRequiredProperty(IConstantesConfig.keyExecutionId) + "-timeseries.json";
            final File f = new File(saveDirectory, fileName);
            final ObjectMapper om = new ObjectMapper();
            log.info("Enregistrement de {} points dans le fichier {}", getPoints().size(), f.getAbsolutePath());
            om.writeValue(new BufferedOutputStream(new FileOutputStream(f)), getPoints());

            clean();
        } catch (IOException e) {
            log.error("Impossible d'enregistrer le JSON des points de monitoring", e);
            throw new RuntimeException("Erreur d'enregistrement du monitoring", e);
        }
    }
}
