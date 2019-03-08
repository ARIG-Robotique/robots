package org.arig.robot.model.communication.balise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.communication.AbstractQuery;
import org.arig.robot.model.communication.balise.enums.BaliseAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class EtallonageQuery extends AbstractQuery<BaliseAction> {

    public EtallonageQuery() {
        super(BaliseAction.ETALLONAGE);
    }

}
