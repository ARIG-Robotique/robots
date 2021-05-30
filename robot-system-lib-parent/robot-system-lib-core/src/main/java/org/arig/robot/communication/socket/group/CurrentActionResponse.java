package org.arig.robot.communication.socket.group;

import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractResponseWithData;
import org.arig.robot.communication.socket.group.enums.GroupAction;

@EqualsAndHashCode(callSuper = true)
public class CurrentActionResponse extends AbstractResponseWithData<GroupAction, String> {
    public CurrentActionResponse(String currentAction) {
        super(GroupAction.CURRENT_ACTION, currentAction);
    }
}
