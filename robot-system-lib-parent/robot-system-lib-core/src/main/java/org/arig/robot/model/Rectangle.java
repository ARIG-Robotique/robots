package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gdepuille on 14/05/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rectangle {

    private double x;
    private double y;
    private double w;
    private double h;

}
