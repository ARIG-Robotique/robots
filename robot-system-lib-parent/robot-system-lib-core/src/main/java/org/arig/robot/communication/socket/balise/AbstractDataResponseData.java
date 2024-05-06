package org.arig.robot.communication.socket.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractDataResponseData<DATA2D extends AbstractData2D, DATA3D extends AbstractData3D<?, ?, ?>> implements Serializable {

    protected List<DATA2D> data2D;
    protected List<DATA3D> data3D;

}
