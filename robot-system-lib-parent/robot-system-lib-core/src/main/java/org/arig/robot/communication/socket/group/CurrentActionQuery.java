package org.arig.robot.communication.socket.group;

import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.group.enums.GroupAction;

@EqualsAndHashCode(callSuper = true)
public class CurrentActionQuery extends AbstractQuery<GroupAction> {
    public CurrentActionQuery() {
        super(GroupAction.CURRENT_ACTION);
    }
}
