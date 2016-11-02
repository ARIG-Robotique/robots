package org.arig.robot.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author gdepuille on 11/10/16.
 */
@Slf4j
public class MonitoringJsonWrapper extends AbstractMonitoringWrapper {

    private File saveDirectory;

    public MonitoringJsonWrapper(final String saveDirectoryPath) {
        Assert.hasText(saveDirectoryPath, "Le chemin d'enregistrement des points ne peut pas être vide");
        saveDirectory = new File(saveDirectoryPath);
        if (!saveDirectory.exists()) {
            log.info("Création du répertoire {} : {}", saveDirectory.getAbsolutePath(), saveDirectory.mkdirs());
        }
    }

    @Override
    public void save() {
        if (!hasPoints()) {
            log.info("Aucun point de monitoring a enregistrer");
            return;
        }

        try {
            final LocalDateTime date = LocalDateTime.now();
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            final String fileName = date.format(formatter) + ".json";
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
