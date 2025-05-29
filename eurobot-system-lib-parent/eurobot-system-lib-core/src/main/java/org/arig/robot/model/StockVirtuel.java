package org.arig.robot.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class StockVirtuel {

  private final Deque<StockPosition> avant = new ArrayDeque<>();
  private final Deque<StockPosition> arriere = new ArrayDeque<>();

  public StockVirtuel(int avantSize, int arriereSize) {
    if (avantSize >= 1) avant.add(StockPosition.TOP);
    if (avantSize == 2) avant.add(StockPosition.BOTTOM);
    if (arriereSize >= 1) arriere.add(StockPosition.TOP);
    if (arriereSize == 2) arriere.add(StockPosition.BOTTOM);
  }

  public ConstructionElementSource takeElementFrom(Face face) {
    Deque<StockPosition> stack = face == Face.AVANT ? avant : arriere;
    if (!stack.isEmpty()) {
      return ConstructionElementSource.builder().face(face).stockPosition(stack.removeFirst()).build();
    }
    return null;
  }

  public List<ConstructionElementSource> takeElements(int expected) {
    List<ConstructionElementSource> result = new ArrayList<>();
    List<Face> faces = new ArrayList<>();

    if (hasAtLeast(Face.AVANT, expected)) {
      faces.add(Face.AVANT);
      faces.add(Face.ARRIERE);
    } else {
      faces.add(Face.ARRIERE);
      faces.add(Face.AVANT);
    }

    for (Face face : faces) {
      while (expected > 0 && hasAtLeast(face, 1)) {
        result.add(takeElementFrom(face));
        expected--;
      }
    }

    return result;
  }

  public int totalSize() {
    return avant.size() + arriere.size();
  }

  public Face emptyFace() {
    if (avant.isEmpty()) {
      return Face.AVANT;
    } else if (arriere.isEmpty()) {
      return Face.ARRIERE;
    }
    return null;
  }

  private int count(Face face) {
    return (face == Face.AVANT ? avant : arriere).size();
  }

  private boolean hasAtLeast(Face face, int n) {
    return count(face) >= n;
  }
}
