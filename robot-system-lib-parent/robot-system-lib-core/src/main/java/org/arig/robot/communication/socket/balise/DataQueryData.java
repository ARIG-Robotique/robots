package org.arig.robot.communication.socket.balise;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataQueryData<FILTER extends Enum<FILTER>> implements Serializable {

    private List<FILTER> filters;

    @SafeVarargs
    public DataQueryData(FILTER... filters) {
        this.filters = List.of(filters);
    }

}
