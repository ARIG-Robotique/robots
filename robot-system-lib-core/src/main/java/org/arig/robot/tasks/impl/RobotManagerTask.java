package org.arig.robot.tasks.impl;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.stats.IStatsExporter;
import org.arig.robot.system.RobotManager;
import org.arig.robot.tasks.AbstractRobotTask;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by mythril on 04/01/14.
 */
@Slf4j
public class RobotManagerTask extends AbstractRobotTask {

    @Autowired
    private RobotManager manager;

    @Autowired(required = false)
    private List<IStatsExporter> statsExporterList;

    @Override
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("Execution Robot Manager task");
        }
        manager.process();

        if (statsExporterList != null) {
            for (IStatsExporter e : statsExporterList) {
                e.export();
            }
        }
    }
}
