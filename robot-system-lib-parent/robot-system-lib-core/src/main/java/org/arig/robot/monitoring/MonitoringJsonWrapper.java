package org.arig.robot.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
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

  private File saveDirectory;

  public MonitoringJsonWrapper() {
    saveDirectory = new File("./logs");
    if (!saveDirectory.exists()) {
      log.info("Création du répertoire {} : {}", saveDirectory.getAbsolutePath(), saveDirectory.mkdirs());
    }
  }

  @Override
  protected void saveMouvementPoints() {
    if (!hasMouvementPoints()) {
      log.info("Aucun point de monitoring de mouvement a enregistrer");
      return;
    }

    try {
      final String fileName = env.getRequiredProperty(ConstantesConfig.keyExecutionId) + "-mouvement.json";
      final File f = new File(saveDirectory, fileName);
      final ObjectMapper om = new ObjectMapper();
      log.info("Enregistrement de {} points de mouvement dans le fichier {}", monitorMouvementPoints().size(), f.getAbsolutePath());
      om.writeValue(new BufferedOutputStream(new FileOutputStream(f)), monitorMouvementPoints());
    } catch (IOException e) {
      log.error("Impossible d'enregistrer le JSON des points de monitoring", e);
      throw new RuntimeException("Erreur d'enregistrement du monitoring", e);
    }
  }

  @Override
  protected void saveTimeSeriePoints() {
    if (!hasTimeSeriePoints()) {
      log.info("Aucun point de monitoring time series a enregistrer");
      return;
    }

    try {
      final String fileName = env.getRequiredProperty(ConstantesConfig.keyExecutionId) + "-timeseries.json";
      final File f = new File(saveDirectory, fileName);
      final ObjectMapper om = new ObjectMapper();
      log.info("Enregistrement de {} points time serie dans le fichier {}", monitorTimeSeriePoints().size(), f.getAbsolutePath());
      om.writeValue(new BufferedOutputStream(new FileOutputStream(f)), monitorTimeSeriePoints());
    } catch (IOException e) {
      log.error("Impossible d'enregistrer le JSON des points de monitoring", e);
      throw new RuntimeException("Erreur d'enregistrement du monitoring", e);
    }
  }
}
