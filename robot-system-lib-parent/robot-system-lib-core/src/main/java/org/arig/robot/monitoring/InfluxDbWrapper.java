package org.arig.robot.monitoring;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author gdepuille on 11/10/16.
 */
@Slf4j
public class InfluxDbWrapper implements IMonitoringWrapper, InitializingBean {

    @Setter
    private String url;

    @Setter
    private String username;

    @Setter
    private String password;

    @Setter
    private String dbName;

    @Setter
    private String retentionPolicy;

    private final List<Point> points = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(url, "L'URL de connexion est obligatoire");
        Assert.hasText(dbName, "Le nom de la BDD est obligatoire");
        Assert.hasText(retentionPolicy, "La politique de retention est obligatoire");
    }

    @Override
    public void addPoint(Point point) {
        points.add(point);
    }

    @Override
    public void sendToDb() {
        final InfluxDB influxDB = InfluxDBFactory.connect(url, username, password);
        log.info("Connecté avec InfluxDB {}", influxDB.version());

        final List<String> databases = influxDB.describeDatabases();
        log.info("Database disponible : {}", StringUtils.join(databases, ", "));

        if (!databases.contains(dbName)) {
            log.info("Database {} non présente, on la crée", dbName);
            influxDB.createDatabase(dbName);
        }

        // Flush every 2000 Points, at least every 100ms
        influxDB.enableBatch(2000, 100, TimeUnit.MILLISECONDS);

        log.info("Enregistrement de {} point en base", points.size());
        points.forEach((p) -> influxDB.write(dbName, retentionPolicy, p));
    }
}
