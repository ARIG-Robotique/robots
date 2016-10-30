package org.arig.robot.model;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author gdepuille on 30/10/16.
 */
@Data
public class MonitorPoint implements Serializable {

    private String tableName;
    private Long time;
    private TimeUnit precision = TimeUnit.MILLISECONDS;
    private Map<String, Object> fields = new LinkedHashMap<>();

    public MonitorPoint tableName(String tableName) {
        setTableName(tableName);
        return this;
    }

    public MonitorPoint time(Long time, TimeUnit precision) {
        setTime(time);
        setPrecision(precision);
        return this;
    }

    public MonitorPoint addField(String name, Object value) {
        fields.put(name, value);
        return this;
    }
}
