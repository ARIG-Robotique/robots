package org.arig.robot.monitoring;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * @author gregorydepuille@sglk.local on 11/10/16.
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

    @Setter(AccessLevel.NONE)
    private InfluxDB influxDB;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(url, "L'URL de connexion est obligatoire");
        Assert.hasText(dbName, "Le nom de la BDD est obligatoire");
        Assert.hasText(retentionPolicy, "La politique de retention est obligatoire");

        influxDB = InfluxDBFactory.connect(url, username, password);
        influxDB.createDatabase(dbName);

        // Flush every 2000 Points, at least every 100ms
        influxDB.enableBatch(2000, 100, TimeUnit.MILLISECONDS);

        log.info("Connecté avec InfluxDB {}", influxDB.version());
        log.info("Database disponible : {}", StringUtils.join(influxDB.describeDatabases(), ", "));
    }

    @Override
    public void write(Point point) {
        influxDB.write(dbName, retentionPolicy, point);
    }
}
