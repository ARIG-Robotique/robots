package org.arig.robot.system.pathfinding;

import algorithms.AStar;
import algorithms.LazyThetaStar;
import algorithms.PathFindingAlgorithm;
import algorithms.anya16.Anya16;
import grid.GridGraph;
import lombok.extern.slf4j.Slf4j;
import main.AlgoFunction;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;
import org.arig.robot.utils.ImageUtils;
import org.arig.robot.utils.SimpleCircularList;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MultiPathFinderImpl extends AbstractPathFinder {

  private PathFinderAlgorithm algorithm;

  private AlgoFunction algoFunction = null;

  private boolean algoFiltered = false;

  private List<Shape> obstacles;
  private BufferedImage tableImage;
  private GridGraph workGraph;

  public MultiPathFinderImpl() {
    super();
  }

  @Override
  public Chemin findPath(Point from, Point to) throws NoPathFoundException {
    Assert.notNull(workGraph, "Le graph de la carte doit être initialisé");
    Assert.notNull(algoFunction, "L'algorithme doit être initialisé");

    log.info("Recherche de chemin de {} a {} avec l'algorithme {}", from.toString(), to.toString(), algorithm.toString());

    // Pour les stats de temps
    StopWatch sw = new StopWatch();
    sw.start();

    Point fromCorrige = null;

    if (workGraph.isBlocked((int) to.getX(), (int) to.getY())) {
      log.error("Impossible de trouver le noeud d'arrivée");
      saveImageForErrorPath(from, to);
      throw new NoPathFoundException(NoPathFoundException.ErrorType.END_NODE_DOES_NOT_EXIST);
    }

    if (workGraph.isBlocked((int) from.getX(), (int) from.getY())) {
      log.warn("Impossible de trouver le noeud de départ, tentative de trouver un autre point proche");

      fromCorrige = getNearestPoint(from, to);

      if (fromCorrige == null) {
        log.error("Toujours impossible de trouver le point départ");
        saveImageForErrorPath(from, to);
        throw new NoPathFoundException(NoPathFoundException.ErrorType.START_NODE_DOES_NOT_EXIST);
      }
    }

    PathFindingAlgorithm algoImpl = fromCorrige == null ?
      algoFunction.getAlgo(workGraph, (int) from.getX(), (int) from.getY(), (int) to.getX(), (int) to.getY()) :
      algoFunction.getAlgo(workGraph, (int) fromCorrige.getX(), (int) fromCorrige.getY(), (int) to.getX(), (int) to.getY());

    algoImpl.computePath();
    int[][] path = algoImpl.getPath();

    if (path == null || path.length == 0) {
      log.error("Impossible de trouver le chemin pour le trajet.");
      throw new NoPathFoundException(NoPathFoundException.ErrorType.NO_PATH_FOUND);
    }

    sw.split();
    log.info("Execution du pathfinding en {}", sw.toSplitString());

    // Transformation des nodes en points
    List<Point> points = Arrays.stream(path)
      .map(point -> new Point(point[0], point[1]))
      .collect(Collectors.toList());

    // Filtrage si necessaire
    if (algoFiltered) {
      points = filterPoints(points);
    }

    // Construction du chemin
    // Le premier point est le "from".
    // Le dernier point est le "to"
    Chemin c = new Chemin();

    if (fromCorrige != null) {
      c.addPoint(fromCorrige);
    }

    for (int i = 1; i < points.size(); i++) {
      c.addPoint(points.get(i));
    }

    sw.stop();
    log.info("Calcul du chemin en {}", sw.toString());

    saveImageForPath(from, c);

    return c;
  }

  @Override
  public boolean isBlocked(Point pointCm) {
    return workGraph.isBlocked((int) pointCm.getX(), (int) pointCm.getY());
  }

  @Override
  public boolean isBlockedByObstacle(Point pointCm) {
    if (CollectionUtils.isEmpty(obstacles)) {
      return false;
    }

    for (Shape obstacle : obstacles) {
      if (obstacle.contains((int) pointCm.getX(), (int) pointCm.getY(), 1, 1)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void setObstacles(final List<Shape> obstacles) {
    log.info("Ajout de {} obstacles", obstacles.size());

    this.obstacles = obstacles;

    if (CollectionUtils.isEmpty(obstacles)) {
      makeGraphFromBufferedImage(tableImage);
      return;
    }

    // Construction d'une image avec l'obstacle.
    BufferedImage obstacleImage = new BufferedImage(tableImage.getWidth(), tableImage.getHeight(), tableImage.getType());
    Graphics2D g = obstacleImage.createGraphics();
    g.setBackground(Color.WHITE);
    g.drawImage(tableImage, 0, 0, null);

    // On ajoute l'obstacle
    g.setColor(Color.BLACK);
    for (Shape p : obstacles) {
      g.fill(p);
    }

    // On termine le machin
    g.dispose();

    // On reconstruit le graph
    makeGraphFromBufferedImage(obstacleImage);
  }

  @Override
  public void construitGraphDepuisImageNoirEtBlanc(final InputStream is) {
    StopWatch sw = new StopWatch();
    sw.start();

    try {
      tableImage = ImageUtils.mirrorX(ImageIO.read(is));
    } catch (IOException e) {
      log.error("Impossible de lire l'image : " + e.toString());
      throw new RuntimeException(e);
    }
    sw.split();
    log.info("Lecture de l'image pour le graph en {}", sw.toSplitString());
    sw.stop();

    // Construction du graph
    makeGraphFromBufferedImage(tableImage);
  }

  private void makeGraphFromBufferedImage(BufferedImage img) {
    Assert.notNull(img, "L'image ne peut pas être null");

    // Ecriture sur disque de la map de path
    saveImageForWork(img);

    StopWatch sw = new StopWatch();
    sw.start();

    workGraph = new GridGraph(img.getWidth(), img.getHeight());

    for (int y = 0; y < workGraph.sizeY; y++) {
      for (int x = 0; x < workGraph.sizeX; x++) {
        // Calculate the cost
        int color = img.getRGB(x, y) & 0xFF;

        // Si la couleur n'est pas noir, on ajoute les noeuds et les liens.
        workGraph.setBlocked(x, y, color == 0);
      }
    }

    sw.split();
    log.info("Construction du graph en {}", sw.toSplitString());
    sw.stop();
  }

  private List<Point> filterPoints(final List<Point> points) {
    final List<Point> newPoints = new ArrayList<>();

    Double anglePrecedent = null;
    int i = 1, l = points.size();

    newPoints.add(points.get(0));

    for (; i < l - 1; i++) {
      Point ptPrec = points.get(i - 1);
      Point pt = points.get(i);
      // Calcul de l'angle anvec le point précédent
      // Si l'angle est différent de 0, alors c'est un point de passage
      double dX = pt.getX() - ptPrec.getX();
      double dY = pt.getY() - ptPrec.getY();
      double angle = Math.atan2(dY, dX);
      if (anglePrecedent != null && angle != anglePrecedent) {
        newPoints.add(ptPrec);
      }
      anglePrecedent = angle;
    }

    newPoints.add(points.get(l - 1));

    log.info("Chemin de {} point(s) de passage filtré en {} points", points.size(), newPoints.size());

    return newPoints;
  }

  private void definePathFinder() {
    switch (algorithm) {
      case A_STAR:
        algoFunction = AStar::new;
        algoFiltered = true;
        break;
      case DIJKSTRA:
        algoFunction = AStar::dijkstra;
        algoFiltered = true;
        break;
      case LAZY_THETA_STAR:
        algoFunction = LazyThetaStar::new;
        algoFiltered = false;
        break;
      case ANYA16:
        algoFunction = Anya16::new;
        algoFiltered = false;
        break;
      default:
        algoFunction = null;
    }

    Assert.notNull(algoFunction, "Le path finding ne peut être null après sa définition.");
  }

  public void setAlgorithm(PathFinderAlgorithm algorithm) {
    this.algorithm = algorithm;
    definePathFinder();
    log.info("Pathfinding algorithm changed to {}", algorithm);
  }

  private static final double ROOT_THREE_BY_TWO = Math.sqrt(3) / 2;

  // 1 point tous les 30°
  private static final List<Point> CHECKPOINTS = Arrays.asList(
    new Point(1, 0),
    new Point(ROOT_THREE_BY_TWO, 0.5),
    new Point(0.5, ROOT_THREE_BY_TWO),
    new Point(0, 1),
    new Point(-0.5, ROOT_THREE_BY_TWO),
    new Point(-ROOT_THREE_BY_TWO, 0.5),
    new Point(-1, 0),
    new Point(-ROOT_THREE_BY_TWO, -0.5),
    new Point(-0.5, -ROOT_THREE_BY_TWO),
    new Point(0, -1),
    new Point(0.5, -ROOT_THREE_BY_TWO),
    new Point(ROOT_THREE_BY_TWO, -0.5)
  );

  @Override
  public Point getNearestPoint(final Point from, final Point to) {
    double angle = Math.atan2(to.getX() - from.getX(), to.getY() - from.getY());
    if (angle < 0) {
      angle += Math.PI * 2;
    }

    // crée une liste de checkpoints dont le premier élément est celui dans la direction générale demandée
    final SimpleCircularList<Point> checkpoints = new SimpleCircularList<>(CHECKPOINTS);
    checkpoints.rotate(-(int) Math.round(angle / Math.PI / 2 * checkpoints.size()));

    int seuil = 8;
    int maxSeuil = seuil * 3;

    Point point;
    do {
      // itère les checkpoints en alternant à "gauche" et à "droite" du premier
      for (int i = 0; i < checkpoints.size(); i++) {
        int index;
        if (i == 0) {
          index = i;
        } else if (i % 2 == 0) {
          index = checkpoints.size() - i / 2;
        } else {
          index = (i + 1) / 2;
        }

        point = from.offsetted(checkpoints.get(index).getX() * seuil, checkpoints.get(index).getY() * seuil);
        if (!workGraph.isBlocked((int) point.getX(), (int) point.getY())) {
          return point;
        }
      }

      seuil += seuil;
    } while (seuil <= maxSeuil);

    return null;
  }
}
