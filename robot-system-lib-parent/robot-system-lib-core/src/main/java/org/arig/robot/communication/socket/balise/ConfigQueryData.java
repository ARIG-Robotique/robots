package org.arig.robot.communication.socket.balise;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.arig.robot.communication.socket.balise.enums.BaliseMode;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ConfigQueryData implements Serializable {

    private BaliseMode mode;

}
