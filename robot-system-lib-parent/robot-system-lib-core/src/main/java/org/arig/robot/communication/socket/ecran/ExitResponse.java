package org.arig.robot.communication.socket.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractResponse;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExitResponse extends AbstractResponse<EcranAction> { }
