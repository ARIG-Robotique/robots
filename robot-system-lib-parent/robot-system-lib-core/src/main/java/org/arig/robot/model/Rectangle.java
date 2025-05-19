package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

  public boolean contains(Point pt) {
    return pt.getX() >= x && pt.getX() < x + w && pt.getY() >= y && pt.getY() < y + h;
  }

  public Point center() {
    return new Point(x + w / 2, y + h / 2);
  }

  public static Rectangle byCenter(double x, double y, double w, double h) {
    return new Rectangle(x - w / 2, y - h / 2, w, h);
  }

}
