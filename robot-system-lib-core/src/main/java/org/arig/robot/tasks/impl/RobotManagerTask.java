package org.arig.robot.tasks.impl;

import org.arig.robot.stats.IStatsExporter;
import org.arig.robot.system.RobotManager;
import org.arig.robot.tasks.AbstractRobotTask;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by mythril on 04/01/14.
 */
public class RobotManagerTask extends AbstractRobotTask {

    @Autowired
    private RobotManager manager;

    @Autowired(required = false)
    private List<IStatsExporter> statsExporterList;

    @Override
    protected void init() {
        // NOP
    }

    @Override
    protected void process() {
        manager.process();

        if (statsExporterList != null) {
            for (IStatsExporter e : statsExporterList) {
                e.process();
            }
        }
    }

    @Override
    protected void end() {
        manager.stop();
        if (statsExporterList != null) {
            for (IStatsExporter e : statsExporterList) {
                e.end();
            }
        }
    }
}
