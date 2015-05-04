package org.arig.robot.system.pathfinding;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.utils.ImageUtils;
import org.arig.robot.vo.Point;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by mythril on 30/12/13.
 */
@Slf4j
public abstract class AbstractPathFinder implements IPathFinder, InitializingBean {

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private int nbTileX = 20;

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private int nbTileY = 20;

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private boolean allowDiagonal = true;

    @Setter(AccessLevel.PROTECTED)
    private File mapSource;

    @Setter
    private File pathDir;

    private final DateTimeFormatter dteFormat = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (pathDir == null) {
            pathDir = new File("./path");
        }
    }

    @Override
    public void construitGraphDepuisImageNoirEtBlanc(String filePath) {
        File f = new File(filePath);
        if (!f.exists() && !f.canRead()) {
            String errorMessage = String.format("Impossible d'acceder au fichier %s (Existe : %s ; Readable : %s)", filePath, f.exists(), f.canRead());
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        construitGraphDepuisImageNoirEtBlanc(f);
    }

    /**
     * Enregistre le chemin calculer dans le répertoire './path'.
     * Le nom du fichier sera à la date de création du fichier.
     *
     * @param pts La liste des points du chemin
     */
    @Async
    public void saveImagePath(List<Point> pts) {
        Assert.notNull(mapSource, "La map source doit être définie");

        try {
            BufferedImage img = ImageUtils.mirrorX(ImageIO.read(mapSource));
            Graphics2D g = img.createGraphics();
            g.setBackground(Color.WHITE);
            org.arig.robot.vo.Point currentPoint = null;
            org.arig.robot.vo.Point precedencePoint = null;
            for (int i = 0 ; i < pts.size() ; i++) {
                // Couleur du premier et des autres points
                g.setColor((currentPoint == null) ? Color.GREEN : Color.BLACK);
                currentPoint = pts.get(i);

                // Couleur du dernier point
                if (i == pts.size() - 1) {
                    g.setColor(Color.RED);
                }
                if (precedencePoint != null) {
                    Color back = g.getColor();

                    g.setColor(Color.BLUE);
                    g.drawLine((int) precedencePoint.getX(), (int) precedencePoint.getY(),
                            (int) currentPoint.getX(), (int) currentPoint.getY());

                    g.setColor(back);
                }

                g.fillOval((int) currentPoint.getX() - 5, (int) currentPoint.getY() - 5, 10, 10);

                precedencePoint = currentPoint;
            }
            g.dispose();

            if (!pathDir.exists()) {
                pathDir.mkdirs();
            }
            ImageIO.write(ImageUtils.mirrorX(img), "png", new File(pathDir, dteFormat.format(LocalDateTime.now()) + suffixResultImageName() + ".png"));
        } catch (Exception e) {
            log.error("Impossible d'enregistrer le chemin dans une image : {}", e.toString());
        }
    }

    protected String suffixResultImageName() {
        return StringUtils.EMPTY;
    }
}
