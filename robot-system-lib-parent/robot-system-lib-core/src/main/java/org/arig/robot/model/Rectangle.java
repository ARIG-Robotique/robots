package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author gdepuille on 14/05/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Rectangle extends Shape {

    private final ShapeType type = ShapeType.RECTANGLE;

    private double x;
    private double y;
    private double w;
    private double h;

}
