package org.arig.robot.utils;

import java.util.Collection;

public enum ArigCollectionUtils {
  ;

  public static <C> boolean addAllIgnoreNull(final Collection<C> collection, final C... elements) {
    boolean changed = false;
    for (final C element : elements) {
      if (element != null) {
        changed |= collection.add(element);
      }
    }
    return changed;
  }
}
