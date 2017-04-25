package org.arig.test.robot.system.pathfinding;

import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.system.pathfinding.PathFinderAlgorithm;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.net.URL;

/**
 * @author gdepuille on 16/11/16.
 */
@Configuration
public class PathFindingTestContext {

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        return new MonitoringJsonWrapper();
    }

    @Bean
    public MultiPathFinderImpl multiPathLabyrinthe() {
        final MultiPathFinderImpl pf = new MultiPathFinderImpl();
        pf.setNbTileX(40);
        pf.setNbTileY(40);
        pf.setAllowDiagonal(true);

        URL url = getClass().getResource("/assets/labyrinthe.png");
        pf.construitGraphDepuisImageNoirEtBlanc(new File(url.getPath()));

        return pf;
    }

    @Bean
    public MultiPathFinderImpl multiPathTableEssai() {
        final MultiPathFinderImpl pf = new MultiPathFinderImpl();
        pf.setNbTileX(118);
        pf.setNbTileY(180);
        pf.setAllowDiagonal(true);
        pf.setAlgorithm(PathFinderAlgorithm.DIJKSTRA);

        final URL url = TableEssaiManhatanTest.class.getClass().getResource("/assets/table-test-obstacle.png");
        pf.construitGraphDepuisImageNoirEtBlanc(new File(url.getPath()));

        return pf;
    }

    @Bean
    public MultiPathFinderImpl multiPathTableJaune() {
        final MultiPathFinderImpl pf = new MultiPathFinderImpl();
        pf.setNbTileX(200);
        pf.setNbTileY(300);
        pf.setAllowDiagonal(true);
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR_MANHATTAN);

        URL url = TableJauneTest.class.getClass().getResource("/assets/jaune.png");
        pf.construitGraphDepuisImageNoirEtBlanc(new File(url.getPath()));

        return pf;
    }
}
