package org.arig.robot.communication.socket.group;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractResponseWithData;
import org.arig.robot.communication.socket.group.enums.GroupAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class CurrentActionResponse extends AbstractResponseWithData<GroupAction, String> { }
