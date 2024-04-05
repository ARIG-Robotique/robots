package org.arig.robot.strategy;

import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.TestRobotStatus;
import org.arig.robot.services.LidarService;
import org.arig.robot.strategy.actions.InvalidWhenNotRunAfter10SecondsAndCompletedAfter2ExecutionAction;
import org.arig.robot.strategy.actions.OneShotAction;
import org.arig.robot.system.capteurs.LidarTelemeterMock;
import org.arig.robot.system.capteurs.socket.ILidarTelemeter;
import org.arig.robot.system.group.RobotGroup;
import org.arig.robot.system.group.RobotGroupTest;
import org.arig.robot.system.pathfinding.NoPathFinderImpl;
import org.arig.robot.system.pathfinding.PathFinder;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdepuille on 15/03/15.
 */
@Configuration
public class StrategyManagerTestContext {

    @Bean
    public StrategyManager strategyManager() {
        return new StrategyManager();
    }

    @Bean
    public AbstractRobotStatus robotStatus() {
        return new TestRobotStatus();
    }

    @Bean
    public RobotGroup robotGroup() {
        return new RobotGroupTest();
    }

    @Bean
    public ConvertionRobotUnit conv() {
        return new ConvertionRobotUnit(1, 1);
    }

    @Bean
    public TableUtils tableUtils() {
        return new TableUtils(3000, 2000, 150);
    }

    @Bean(name = "currentPosition")
    public Position currentPosition()  {
        return new Position();
    }

    @Bean
    public ILidarTelemeter lidarTelemeter() {
        return new LidarTelemeterMock();
    }

    @Bean
    public RobotConfig robotConfig() {
        return new RobotConfig();
    }

    @Bean
    public PathFinder pathFinder() {
        return new NoPathFinderImpl();
    }

    @Bean
    public LidarService lidarService() {
        return new LidarService();
    }

    @Bean
    public Action oneShotAction() {
        return new OneShotAction();
    }

    @Bean
    public Action invalidDuring10Seconds() {
        return new InvalidWhenNotRunAfter10SecondsAndCompletedAfter2ExecutionAction();
    }
}
