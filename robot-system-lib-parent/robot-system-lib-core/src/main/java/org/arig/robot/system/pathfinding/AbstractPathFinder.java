package org.arig.robot.system.pathfinding;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;
import org.arig.robot.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public abstract class AbstractPathFinder implements IPathFinder {

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private boolean saveImages = true;

    private BufferedImage workImage;
    private File pathDir;
    private final DateTimeFormatter dteFormat = DateTimeFormatter.ISO_TIME;

    public AbstractPathFinder() {
        pathDir = new File("./logs/path/" + System.getProperty(IConstantesConfig.keyExecutionId));
    }

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

    protected void saveImageForErrorPath(Point from, Point to) {
        if (isSaveImages()) {
            saveImageForPath(Arrays.asList(from, to), true);
        }
    }

    protected void saveImageForPath(Point from, Chemin c) {
        if (isSaveImages()) {
            List<Point> pts = new ArrayList<>();
            pts.add(from);
            pts.addAll(c.getPoints());
            saveImageForPath(pts, false);
        }
    }

    private void saveImageForPath(List<Point> pts, boolean isError) {
        try {
            final int l = pts.size();

            BufferedImage img = new BufferedImage(workImage.getWidth(), workImage.getHeight(), workImage.getType());
            Graphics2D g = img.createGraphics();

            g.drawImage(workImage, 0, 0, null);

            g.setBackground(Color.WHITE);

            Point precedentPoint = null;
            for (int i = 0; i < l; i++) {
                Point currentPoint = pts.get(i);

                if (i == l - 1) {
                    g.setColor(Color.RED);
                } else if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.CYAN);
                }

                if (precedentPoint != null && !isError) {
                    Color back = g.getColor();

                    g.setColor(Color.BLUE);
                    g.drawLine((int) precedentPoint.getX(), (int) precedentPoint.getY(), (int) currentPoint.getX(), (int) currentPoint.getY());
                    g.setColor(back);
                }

                g.fillOval((int) currentPoint.getX() - 5, (int) currentPoint.getY() - 5, 10, 10);

                precedentPoint = currentPoint;
            }

            if (isError || pts.size() > 2) {
                g.setColor(isError ? Color.RED : Color.BLUE);
                g.setStroke(new BasicStroke(isError ? 2 : 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{5}, 0));
                g.drawLine((int) pts.get(0).getX(), (int) pts.get(0).getY(), (int) pts.get(l - 1).getX(), (int) pts.get(l - 1).getY());
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
