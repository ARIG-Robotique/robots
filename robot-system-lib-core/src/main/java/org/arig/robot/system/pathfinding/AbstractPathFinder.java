package org.arig.robot.system.pathfinding;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Created by mythril on 30/12/13.
 */
@Slf4j
public abstract class AbstractPathFinder<A> implements IPathFinder<A> {

    @Getter
    private A algorithm;

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private int nbTileX = 20;

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private int nbTileY = 20;

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private boolean allowDiagonal = true;

    @Override
    public void makeGraphFromBWImage(String filePath) {
        File f = new File(filePath);
        if (!f.exists() && !f.canRead()) {
            String errorMessage = String.format("Impossible d'acceder au fichier %s (Existe : %s ; Readable : %s)", filePath, f.exists(), f.canRead());
            AbstractPathFinder.log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        makeGraphFromBWImage(f);
    }

    @Override
    public void setAlgorithm(A algorithm) {
        this.algorithm = algorithm;
    }


}
