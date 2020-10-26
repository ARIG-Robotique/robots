package org.arig.robot.communication.socket.balise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class IdleQuery extends AbstractQuery<BaliseAction> {

    public IdleQuery() {
        super(BaliseAction.IDLE);
    }

}
