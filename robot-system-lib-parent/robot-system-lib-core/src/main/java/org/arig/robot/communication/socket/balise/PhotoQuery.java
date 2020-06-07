package org.arig.robot.communication.socket.balise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class PhotoQuery extends AbstractQuery<BaliseAction> {

    public PhotoQuery() {
        super(BaliseAction.PHOTO);
    }

}
