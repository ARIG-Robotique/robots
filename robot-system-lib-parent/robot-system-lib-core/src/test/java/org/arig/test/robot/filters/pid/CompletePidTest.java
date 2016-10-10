package org.arig.test.robot.filters.pid;

import com.csvreader.CsvWriter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.pid.CompletePID;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePID;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by gdepuille on 15/03/15.
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class CompletePidTest {

    private static File rootCsvDir;

    @BeforeClass
    public static void initClass() {
        rootCsvDir = new File(System.getProperty("java.io.tmpdir") + "/arig/robot/pid");
        if (!rootCsvDir.exists()) {
            rootCsvDir.mkdirs();
        }
    }

    @Test
    public void testP() throws Exception {
        File outputFile = new File(rootCsvDir, "complete-pid-P.csv");
        CsvWriter writer = new CsvWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false), "UTF-8"), ';');
        writer.writeRecord(new String[] {"consigne", "input", "output"});

        CompletePID pid = getPid();
        pid.setTunings(1, 0, 0);

        double consigne = 100;
        double input = 0, output = 0;
        for (int i = 0 ; i < 100 ; i++) {
            if (i > 10) {
                input = (i * consigne) / 100;
            }
            output = pid.compute(consigne, input);
            log.info("Test P : consigne {}, input {}, output {}", consigne, input, output);
            Assert.assertEquals(consigne - input, output, 1);
            writer.writeRecord(new String[] {String.valueOf(consigne), String.valueOf(input), String.valueOf(output)});
        }
        writer.flush();
        writer.close();
    }

    @Test
    public void testPI() throws Exception {
        File outputFile = new File(rootCsvDir, "complete-pid-PI.csv");
        CsvWriter writer = new CsvWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false), "UTF-8"), ';');
        writer.writeRecord(new String[] {"consigne", "input", "output"});

        CompletePID pid = getPid();
        pid.setTunings(1, 1, 0);

        double consigne = 100;
        double input = 0, output = 0;
        for (int i = 0 ; i < 100 ; i++) {
            if (i > 10) {
                input = (i * consigne) / 100;
            }
            output = pid.compute(consigne, input);
            log.info("Test P : consigne {}, input {}, output {}", consigne, input, output);
            writer.writeRecord(new String[] {String.valueOf(consigne), String.valueOf(input), String.valueOf(output)});
        }
        writer.flush();
        writer.close();
    }

    @Test
    public void testPID() throws Exception {
        File outputFile = new File(rootCsvDir, "complete-pid-PID.csv");
        CsvWriter writer = new CsvWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false), "UTF-8"), ';');
        writer.writeRecord(new String[] {"consigne", "input", "output"});

        CompletePID pid = getPid();
        pid.setTunings(1, 1, 1);

        double consigne = 100;
        double input = 0, output = 0;
        for (int i = 0 ; i < 100 ; i++) {
            if (i > 10) {
                input = (i * consigne) / 100;
            }
            output = pid.compute(consigne, input);
            log.info("Test P : consigne {}, input {}, output {}", consigne, input, output);
            writer.writeRecord(new String[] {String.valueOf(consigne), String.valueOf(input), String.valueOf(output)});
        }
        writer.flush();
        writer.close();
    }

    private CompletePID getPid() {
        CompletePID pid = new CompletePID();
        pid.setControllerDirection(IPidFilter.PidType.DIRECT);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        pid.setSampleTime(1);
        pid.reset();
        pid.initialise();


        return pid;
    }
}
