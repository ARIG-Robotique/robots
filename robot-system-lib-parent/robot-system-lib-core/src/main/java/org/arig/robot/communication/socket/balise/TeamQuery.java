package org.arig.robot.communication.socket.balise;

import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
public class TeamQuery
    extends AbstractQueryWithData<BaliseAction, TeamQueryData>
    implements Serializable {

    public TeamQuery() {
        super(BaliseAction.TEAM);
    }

    public TeamQuery(TeamQueryData data) {
        super(BaliseAction.TEAM, data);
    }

}
