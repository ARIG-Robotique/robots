package org.arig.robot.stats.impl;

import com.csvreader.CsvWriter;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.stats.AbstractFileExporter;
import org.arig.robot.stats.IStatsObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.charset.UnsupportedCharsetException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mythril on 04/01/14.
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class StatsToCSVExporter extends AbstractFileExporter {

    /** The Constant UTF8. */
    public static final String UTF8 = "UTF-8";

    /** The Constant CP1252. */
    public static final String CP1252 = "CP1252";

    /** The Constant COMMA_SEPARATOR. */
    public static final char COMMA_SEPARATOR = ';';

    /** The Constant DEFAULT_SEPARATOR. */
    public static final char DEFAULT_SEPARATOR = StatsToCSVExporter.COMMA_SEPARATOR;

    /** The Constant DEFAULT_CHARSET. */
    public static final String DEFAULT_CHARSET = StatsToCSVExporter.UTF8;

    /** The separator. */
    private final char separator;

    /** The charset. */
    private final String charset;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private CsvWriter writer = null;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Set<String> keys = null;

    @Override
    public void process() {
        // Création du fichier si il n'existe pas
        if (getFile() == null) {
            final SimpleDateFormat sdf = new SimpleDateFormat("'CSV-'yyyyMMddHHmmssSSS", Locale.FRENCH);
            setFile(new File("./graph", sdf.format(new Date()) + ".csv"));
        }

        try {

            Map<String, String> values = new HashMap<>();
            for (IStatsObject o : getStatsObjectList()) {
                values.putAll(o.getValues());
            }

            // Sauvegarde des clés
            if (keys == null) {
                keys = values.keySet();
            }

            // Création du Writer et des headers la première fois
            if (writer == null) {
                writer = new CsvWriter(new OutputStreamWriter(new FileOutputStream(getFile(), true), charset), separator);
                writeHeader();
            }

            writeValues(values);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            log.error("Le writer n'as pas pu être créé : " + e.toString());
        } catch (IOException e) {
            log.error("Erreur lors de l'ecriture : " + e.toString());
        }
    }

    @Override
    public void end() {
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }

    private void writeHeader() throws IOException {
        for (String k : keys) {
            writer.write(k);
        }
        writer.endRecord();
    }

    private void writeValues(Map<String, String> values) throws IOException {
        for (String k : keys) {
            writer.write(values.get(k));
        }
        writer.endRecord();
    }
}
