package org.arig.robot.communication.socket.group;

import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.group.enums.GroupAction;

@EqualsAndHashCode(callSuper = true)
public class EventLogQuery extends AbstractQueryWithData<GroupAction, byte[]> {
    public EventLogQuery(byte[] data) {
        super(GroupAction.EVENT_LOG, data);
    }
}
