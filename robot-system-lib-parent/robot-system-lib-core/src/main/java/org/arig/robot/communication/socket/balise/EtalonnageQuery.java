package org.arig.robot.communication.socket.balise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithDatas;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class EtalonnageQuery extends AbstractQueryWithDatas<BaliseAction, EtalonnageQuery.EtalonnageQueryParams> {

    @Data
    @AllArgsConstructor
    static class EtalonnageQueryParams implements Serializable {
        int[][] ecueil;
        int[][] bouees;
    }

    public EtalonnageQuery(int[][] ecueil, int[][] bouees) {
        super(BaliseAction.ETALONNAGE);
        setDatas(new EtalonnageQueryParams(ecueil, bouees));
    }

}
