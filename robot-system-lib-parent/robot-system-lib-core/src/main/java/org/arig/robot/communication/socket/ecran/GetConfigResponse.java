package org.arig.robot.communication.socket.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractResponseWithDatas;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.model.ecran.GetConfigInfos;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetConfigResponse extends AbstractResponseWithDatas<EcranAction, GetConfigInfos> { }
