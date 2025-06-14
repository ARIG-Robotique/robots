package org.arig.robot.system.pathfinding;

import lombok.SneakyThrows;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.TestRobotStatus;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

/**
 * @author gdepuille on 16/11/16.
 */
@Configuration
public class PathFindingTestContext {

  @Bean
  public MonitoringWrapper monitoringWrapper() {
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

  @Bean
  public AbstractRobotStatus robotStatus() {
    return new TestRobotStatus();
  }
}
