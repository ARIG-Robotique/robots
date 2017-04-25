package org.arig.robot.monitoring;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author gdepuille on 01/11/16.
 *
 * @deprecated En attendant le superviseur / reader
 */
@Slf4j
@Deprecated
public class MonitoringInfluxDBWrapper extends MonitoringJsonWrapper implements InitializingBean {

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

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(url, "L'URL de connexion est obligatoire");
        Assert.hasText(dbName, "Le nom de la BDD est obligatoire");
        Assert.hasText(retentionPolicy, "La politique de retention est obligatoire");
    }

    @Override
    protected void saveTimeSeriePoints() {
        super.saveTimeSeriePoints();

        if (!hasTimeSeriePoints()) {
            log.info("Aucun point de monitoring time serie a enregistrer");
            return;
        }

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

        List<Point> points = getMonitorTimeSeriePoints().parallelStream()
                .map(this::transform)
                .collect(Collectors.toList());

        log.info("Enregistrement de {} point en base", points.size());
        points.forEach((p) -> influxDB.write(dbName, retentionPolicy, p));
    }

    private Point transform(MonitorTimeSerie mp) {
        Builder b = Point.measurement(mp.getTableName())
                        .time(mp.getTime(), mp.getPrecision());

        mp.getFields().entrySet().forEach(e -> b.addField(e.getKey(), e.getValue()));

        return b.build();
    }
 }