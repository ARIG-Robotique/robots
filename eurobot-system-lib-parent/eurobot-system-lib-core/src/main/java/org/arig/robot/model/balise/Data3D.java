package org.arig.robot.model.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.balise.AbstractData3D;
import org.arig.robot.model.balise.enums.Data3DName;
import org.arig.robot.model.balise.enums.Data3DTeam;
import org.arig.robot.model.balise.enums.Data3DType;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data3D
    extends AbstractData3D<Data3DName, Data3DType, Data3DTeam>
    implements Serializable {

    public Data3D() {
    }

    public Data3D(Data3DName name, Data3DType type, Data3DTeam team) {
        this.name = name;
        this.type = type;
        this.team = team;
    }

}
