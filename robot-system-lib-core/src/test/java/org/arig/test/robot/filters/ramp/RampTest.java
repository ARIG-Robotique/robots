package org.arig.test.robot.filters.ramp;

import com.csvreader.CsvWriter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePID;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.filters.ramp.Ramp;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by gdepuille on 15/03/15.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RampTestContext.class})
public class RampTest {

    @Autowired
    private IRampFilter filter;

    private static File rootCsvDir;

    @BeforeClass
    public static void initClass() {
        rootCsvDir = new File(System.getProperty("java.io.tmpdir") + "/arig/robot/ramp");
        if (!rootCsvDir.exists()) {
            rootCsvDir.mkdirs();
        }
    }

    @Test
    public void testFilter() throws Exception {
        File outputFile = new File(rootCsvDir, "ramp.csv");
        CsvWriter writer = new CsvWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false), "UTF-8"), ';');
        writer.writeRecord(new String[] {"vitesse", "consigne", "output"});

        double vitesse = 100;
        double output;
        for (int i = 200 ; i >= -200 ; i--) {
            if(i == 100) {
                vitesse = 150;
            }
            if (i == -100) {
                vitesse = 100;
            }
            output = filter.filter(vitesse, i, 0, true);
            log.info("Vitesse {}, consigne {}, output {}", vitesse, i, output);
            writer.writeRecord(new String[]{String.valueOf(vitesse / 10), String.valueOf(i / 10), String.valueOf(output)});
        }
        writer.flush();
        writer.close();
    }
}
