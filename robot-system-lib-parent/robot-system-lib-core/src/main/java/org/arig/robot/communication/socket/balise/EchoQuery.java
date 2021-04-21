package org.arig.robot.communication.socket.balise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class EchoQuery extends AbstractQueryWithData<BaliseAction, String> {

    public EchoQuery(final String msg) {
        super(BaliseAction.ECHO);
        setData(msg);
    }

}
