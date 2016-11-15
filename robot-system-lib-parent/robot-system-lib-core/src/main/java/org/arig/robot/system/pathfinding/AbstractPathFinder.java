package org.arig.robot.system.pathfinding;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author gdepuille on 30/12/13.
 */
@Slf4j
public abstract class AbstractPathFinder implements IPathFinder {

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
    public void construitGraphDepuisImageNoirEtBlanc(final String filePath) {
        construitGraphDepuisImageNoirEtBlanc(new File(filePath));
    }

    @Override
    public void construitGraphDepuisImageNoirEtBlanc(final File file) {
        if (!file.exists() && !file.canRead()) {
            String errorMessage = String.format("Impossible d'acceder au fichier %s (Existe : %s ; Readable : %s)", file.getAbsolutePath(), file.exists(), file.canRead());
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        try {
            construitGraphDepuisImageNoirEtBlanc(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            log.error("Fichier introuvable.", e);
        }
    }
}
