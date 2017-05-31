package org.arig.robot.model;

/**
 * @author gdepuille on 24/05/17.
 */
public abstract class Shape {

    public abstract ShapeType getType();

    protected enum ShapeType {
        RECTANGLE, CIRCLE
    }
}
