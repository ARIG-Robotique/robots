package org.arig.robot.filters.ramp;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RampTestContext.class})
public abstract class AbstractRampTest {

    @Autowired
    private MonitoringWrapper monitoringWrapper;

    protected abstract AbstractRampFilter getFiltre();

    @BeforeEach
    public void before() {
        System.setProperty(ConstantesConfig.keyExecutionId, UUID.randomUUID().toString());
        monitoringWrapper.cleanAllPoints();

        getFiltre().reset();
    }

    @AfterEach
    public void after() {
        monitoringWrapper.save();
    }

    @Test
    @DirtiesContext
    public void testChangeSampleTime() {
        getFiltre().setSampleTimeMs(1000);
        Assertions.assertEquals(1, getFiltre().getSampleTimeS(), 0);

        getFiltre().setSampleTimeMs(1500);
        Assertions.assertEquals(1.5, getFiltre().getSampleTimeS(), 0);

        getFiltre().setSampleTime(10, TimeUnit.SECONDS);
        Assertions.assertEquals(10, getFiltre().getSampleTimeS(), 0);
    }

}
