package org.arig.robot.system.pathfinding;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;
import org.arig.robot.utils.ImageUtils;
import org.springframework.scheduling.annotation.Async;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gdepuille on 30/12/13.
 */
@Slf4j
public abstract class AbstractPathFinder implements IPathFinder {

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private boolean saveImages = true;

    private BufferedImage workImage;
    private File pathDir = new File("./logs/path");
    private final DateTimeFormatter dteFormat = DateTimeFormatter.ISO_DATE_TIME;

    @Async
    public void saveImageForWork(BufferedImage workImage) {
        this.workImage = workImage;
        try {
            if (!pathDir.exists()) {
                log.info("Création du répertoire {} : {}", pathDir.getAbsolutePath(), pathDir.mkdirs());
            }
            ImageIO.write(ImageUtils.mirrorX(workImage), "png", new File(pathDir, dteFormat.format(LocalDateTime.now()) + "-work.png"));
        } catch (Exception e) {
            log.error("Impossible d'enregistrer l'obstacle dans une image : {}", e.toString());
        }
    }

    @Async
    public void saveImageForPath(Point from, Chemin c) {
        try {
            BufferedImage img = new BufferedImage(workImage.getWidth(), workImage.getHeight(), workImage.getType());
            Graphics2D g = img.createGraphics();
            g.drawImage(workImage, 0, 0, null);

            g.setBackground(Color.WHITE);
            Point currentPoint = null;
            Point precedentPoint = null;
            List<Point> pts = new ArrayList<>();
            pts.add(from);
            pts.addAll(c.getPoints());
            for (int i = 0; i < pts.size(); i++) {
                // Couleur du premier et des autres points
                g.setColor((currentPoint == null) ? Color.GREEN : Color.BLACK);
                currentPoint = pts.get(i);

                // Couleur du dernier point
                if (i == pts.size() - 1) {
                    g.setColor(Color.RED);
                }
                if (precedentPoint != null) {
                    Color back = g.getColor();

                    g.setColor(Color.BLUE);
                    g.drawLine((int) precedentPoint.getX(), (int) precedentPoint.getY(),
                            (int) currentPoint.getX(), (int) currentPoint.getY());

                    g.setColor(back);
                }

                g.fillOval((int) currentPoint.getX() - 5, (int) currentPoint.getY() - 5, 10, 10);

                precedentPoint = currentPoint;
            }
            g.dispose();

            if (!pathDir.exists()) {
                log.info("Création du répertoire {} : {}", pathDir.getAbsolutePath(), pathDir.mkdirs());
            }
            ImageIO.write(ImageUtils.mirrorX(img), "png", new File(pathDir, dteFormat.format(LocalDateTime.now()) + "-path.png"));
        } catch (Exception e) {
            log.error("Impossible d'enregistrer le chemin dans une image : {}", e.toString());
        }
    }
}
