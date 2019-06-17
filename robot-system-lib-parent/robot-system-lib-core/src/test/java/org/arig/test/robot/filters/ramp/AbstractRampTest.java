package org.arig.test.robot.filters.ramp;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.filters.ramp.AbstractRampFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RampTestContext.class})
public abstract class AbstractRampTest {

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    protected abstract AbstractRampFilter getFiltre();

    @Before
    public void before() {
        System.setProperty(IConstantesConfig.keyExecutionId, UUID.randomUUID().toString());
        monitoringWrapper.cleanAllPoints();

        getFiltre().reset();
    }

    @After
    public void after() {
        monitoringWrapper.save();
    }

    @Test
    @DirtiesContext
    public void testChangeSampleTime() {
        getFiltre().setSampleTime(1000);
        Assert.assertEquals(1, getFiltre().getSampleTimeS(), 0);

        getFiltre().setSampleTime(1500);
        Assert.assertEquals(1.5, getFiltre().getSampleTimeS(), 0);

        getFiltre().setSampleTime(10, TimeUnit.SECONDS);
        Assert.assertEquals(10, getFiltre().getSampleTimeS(), 0);
    }

}
