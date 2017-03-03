package org.arig.robot.model.lidar.communication;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.lidar.ScanInfos;

/**
 * @author gdepuille on 03/03/17.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GrabDataResponse extends AbstractResponseWithDatas<ScanInfos> { }
