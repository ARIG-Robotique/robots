package org.arig.robot.model.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gdepuille on 30/10/16.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorTimeSerie extends AbstractMonitor {

    private String tableName;
    private Map<String, Number> fields = new LinkedHashMap<>();

    public MonitorTimeSerie tableName(String tableName) {
        setTableName(tableName);
        return this;
    }

    public MonitorTimeSerie addField(String name, Number value) {
        fields.put(name, value);
        return this;
    }
}
