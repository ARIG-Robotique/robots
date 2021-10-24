package org.arig.robot.filters.pid;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author gdepuille on 15/03/15.
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { PidTestContext.class })
public class SimplePidIntegralLimitTest extends AbstractPidTest {

    @Autowired
    @Qualifier("simplePIDIntegralLimit")
    private SimplePidFilter pid;

    @Override
    protected IPidFilter pid() {
        return pid;
    }

    @BeforeEach
    public void before() {
        super.before();
        setHasIntegralLimiter(true);
    }
}
