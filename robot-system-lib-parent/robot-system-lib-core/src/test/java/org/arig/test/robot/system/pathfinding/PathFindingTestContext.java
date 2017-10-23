package org.arig.test.robot.system.pathfinding;

import lombok.SneakyThrows;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.system.pathfinding.PathFinderAlgorithm;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
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
    @SneakyThrows
    public MultiPathFinderImpl multiPathLabyrinthe() {
        final MultiPathFinderImpl pf = new MultiPathFinderImpl();

        URL url = getClass().getResource("/assets/labyrinthe.png");
        pf.construitGraphDepuisImageNoirEtBlanc(url.openStream());

        return pf;
    }

    @Bean
    @SneakyThrows
    public MultiPathFinderImpl multiPathTableEssai() {
        final MultiPathFinderImpl pf = new MultiPathFinderImpl();

        final URL url = getClass().getResource("/assets/table-test-obstacle.png");
        pf.construitGraphDepuisImageNoirEtBlanc(url.openStream());

        return pf;
    }

    @Bean
    @SneakyThrows
    public MultiPathFinderImpl multiPathTableJaune() {
        final MultiPathFinderImpl pf = new MultiPathFinderImpl();

        URL url = getClass().getResource("/assets/jaune.png");
        pf.construitGraphDepuisImageNoirEtBlanc(url.openStream());

        return pf;
    }
}
