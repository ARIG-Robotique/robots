package org.arig.robot.csv;

import com.csvreader.CsvWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by gdepuille on 08/03/15.
 */
@Slf4j
public class CsvCollector implements InitializingBean {

    private List<CsvData> datas;

    private CsvData current;

    private File outputFile;

    @Override
    public void afterPropertiesSet() throws Exception {
        // Création du fichier si il n'existe pas
        final SimpleDateFormat sdf = new SimpleDateFormat("'CSV-'yyyyMMddHHmmssSSS", Locale.FRENCH);
        outputFile = new File("logs", sdf.format(new Date()) + ".csv");
    }

    public CsvData getCurrent() {
        if (current == null) {
            addNewItem();
        }

        return current;
    }

    public void addNewItem() {
        if (datas == null) {
            datas = new ArrayList<>();
        }

        current = new CsvData();
        datas.add(current);
    }

    public void exportToFile() {

        CsvWriter writer = null;
        try {
            log.info("Ecriture des datas dans le fichier {}", outputFile.getCanonicalPath());
            writer = new CsvWriter(new OutputStreamWriter(new FileOutputStream(outputFile, true), "UTF-8"), ';');

            // Ecriture des en tetes.
            writer.write("codeurGauche");
            writer.write("codeurDroit");
            writer.write("codeurDistance");
            writer.write("codeurOrientation");

            writer.write("modeAsserv");
            writer.write("typeOdom");

            writer.write("vitesseDistance");
            writer.write("vitesseOrient");
            writer.write("consigneDistance");
            writer.write("consigneOrient");

            writer.write("setPointDistance");
            writer.write("consigneDistance");
            writer.write("erreurDistance");
            writer.write("erreurSumDistance");
            writer.write("outputPidDistance");

            writer.write("setPointOrient");
            writer.write("consigneOrient");
            writer.write("erreurOrient");
            writer.write("erreurSumOrient");
            writer.write("outputPidOrient");

            writer.write("cmdMotGauche");
            writer.write("cmdMotDroit");

            writer.write("x");
            writer.write("y");
            writer.write("angle");

            writer.write("Trajet en approche");
            writer.write("Trajet atteint");

            writer.endRecord();

            for (CsvData d : datas) {
                writer.write(String.valueOf(d.getCodeurGauche()));
                writer.write(String.valueOf(d.getCodeurDroit()));
                writer.write(String.valueOf(d.getCodeurDistance()));
                writer.write(String.valueOf(d.getCodeurOrient()));

                writer.write(d.getModeAsserv());
                writer.write(d.getTypeOdometrie());

                writer.write(String.valueOf(d.getVitesseDistance()));
                writer.write(String.valueOf(d.getVitesseOrient()));
                writer.write(String.valueOf(d.getConsigneDistance()));
                writer.write(String.valueOf(d.getConsigneOrient()));

                writer.write(String.valueOf(d.getSetPointDistance()));
                writer.write(String.valueOf(d.getInputDistance()));
                writer.write(String.valueOf(d.getErreurDistance()));
                writer.write(String.valueOf(d.getSumErreurDistance()));
                writer.write(String.valueOf(d.getOutputPidDistance()));

                writer.write(String.valueOf(d.getSetPointOrient()));
                writer.write(String.valueOf(d.getInputOrient()));
                writer.write(String.valueOf(d.getErreurOrient()));
                writer.write(String.valueOf(d.getSumErreurOrient()));
                writer.write(String.valueOf(d.getOutputPidOrient()));

                writer.write(String.valueOf(d.getCmdMoteurGauche()));
                writer.write(String.valueOf(d.getCmdMoteurDroit()));

                writer.write(String.valueOf(d.getX()));
                writer.write(String.valueOf(d.getY()));
                writer.write(String.valueOf(d.getAngle()));

                writer.write(Boolean.toString(d.isTrajetEnApproche()));
                writer.write(Boolean.toString(d.isTrajetAtteint()));

                writer.endRecord();
            }
        } catch (IOException e) {
            log.error("Erreur lors de la génération du fichier CSV.", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}

