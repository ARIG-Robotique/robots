package org.arig.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.List;

@Slf4j
public class GameMultiPathFinderImpl extends MultiPathFinderImpl {

    @Autowired
    private EurobotStatus rs;

    @Autowired
    private TableUtils tableUtils;

    @Override
    public void setObstacles(final List<Shape> obstacles) {

        super.setObstacles(obstacles);
    }
}
