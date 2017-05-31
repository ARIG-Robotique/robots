package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author gdepuille on 24/05/17.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cercle extends Shape {

    private final ShapeType type = ShapeType.CIRCLE;

    private Point centre;
    private Integer rayon;
}
