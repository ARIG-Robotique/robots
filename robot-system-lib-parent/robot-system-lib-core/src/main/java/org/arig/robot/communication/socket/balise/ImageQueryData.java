package org.arig.robot.communication.socket.balise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageQueryData implements Serializable {

    private float reduction;

}
