package org.arig.robot.communication.socket.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractResponseWithData;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.model.ecran.AbstractEcranConfig;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetConfigResponse extends AbstractResponseWithData<EcranAction, AbstractEcranConfig> { }
