package org.arig.robot.filters.pid;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { PidTestContext.class })
public class DerivateInputPidTest extends AbstractPidTest {

    @Autowired
    private DerivateInputPidFilter pid;

    @Override
    protected PidFilter pid() {
        return pid;
    }

    @Override
    public void testPID() {
        pid().setTunings(1, 1, 1);

        double consigne = 100;
        double input = 0, inputPrec = 0, output, error, errorSum = 0;
        for (int i = 0 ; i < 100 ; i++) {
            if (i > 10) {
                input = (i * consigne) / 100;
            }
            pid().consigne(consigne);
            error = consigne - input;
            errorSum += error;
            output = pid().filter(input);
            log.info("Test PID : consigne {}, mesure {}, output {}", consigne, input, output);

            double expected = error + errorSum - (input - inputPrec);
            inputPrec = input;
            Assertions.assertEquals(expected, output, 0);
        }
    }
}
