package org.arig.robot.communication.socket.group;

import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractResponseWithData;
import org.arig.robot.communication.socket.group.enums.GroupAction;

@EqualsAndHashCode(callSuper = true)
public class EventLogResponse extends AbstractResponseWithData<GroupAction, byte[]> {
    public EventLogResponse(byte[] data) {
        super(GroupAction.EVENT_LOG, data);
    }
}
