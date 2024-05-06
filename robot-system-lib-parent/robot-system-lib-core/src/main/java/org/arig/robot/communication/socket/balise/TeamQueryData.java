package org.arig.robot.communication.socket.balise;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TeamQueryData implements Serializable {

    private String team;

}
