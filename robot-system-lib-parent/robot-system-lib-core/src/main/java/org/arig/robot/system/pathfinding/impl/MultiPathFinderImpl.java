package org.arig.robot.system.pathfinding.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;
import org.arig.robot.system.pathfinding.AbstractPathFinder;
import org.arig.robot.system.pathfinding.PathFinderAlgorithm;
import org.arig.robot.utils.ImageUtils;
import org.springframework.util.Assert;
import pathfinder.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gdepuille on 29/12/13.
 */
@Slf4j
public class MultiPathFinderImpl extends AbstractPathFinder {

    @Getter(AccessLevel.PROTECTED)
    private PathFinderAlgorithm algorithm;

    @Setter
    private double maxDistanceDepart = 1.0;

    @Setter
    private double maxDistanceArrivee = 1.0;

    private IGraphSearch pf = null;

    /**
     * Facteur pour le calcul du cout des noeuds avec le A*
     */
    private double aStarCostFactor = 1.0;

    private BufferedImage tableImage;
    private Graph workGraph;

    public MultiPathFinderImpl() {
        super();
    }

    public MultiPathFinderImpl(PathFinderAlgorithm algorithm) {
        this();
        setAlgorithm(algorithm);
    }

    public MultiPathFinderImpl(PathFinderAlgorithm algorithm, double aStarCostFactor) {
        this(algorithm);
        setAStartCostFactor(aStarCostFactor);
    }

    @Override
    public Chemin findPath(Point from, Point to) throws NoPathFoundException {
        Assert.notNull(workGraph, "Le graph de la carte doit être initialisé");

        if (pf == null) {
            definePathFinder();
        }

        log.info("Recherche de chemin de {} a {} avec l'algorithme {}", from.toString(), to.toString(), getAlgorithm().toString());

        // Pour les stats de temps
        StopWatch sw = new StopWatch();
        sw.start();

        GraphNode startNode, endNode;
        // Choisir dans le quadrant vers la destination pour eviter de reculer.
        if ((startNode = workGraph.getNodeAt(from.getX(), from.getY(), 0, maxDistanceDepart)) == null) {
            log.error("Impossible de trouver le noeud de départ");
            throw new NoPathFoundException(NoPathFoundException.ErrorType.START_NODE_DOES_NOT_EXIST);
        }
        if ((endNode = workGraph.getNodeAt(to.getX(), to.getY(), 0, maxDistanceArrivee)) == null) {
            log.error("Impossible de trouver le noeud d'arrivée");
            throw new NoPathFoundException(NoPathFoundException.ErrorType.END_NODE_DOES_NOT_EXIST);
        }
        sw.split();
        log.info("Récupération des nodes en {}", sw.toSplitString());

        sw.unsplit();
        LinkedList<GraphNode> graphNodes = pf.search(startNode.id(), endNode.id(), true);
        if (graphNodes == null || graphNodes.isEmpty()) {
            log.error("Impossible de trouver le chemin pour le trajet.");
            throw new NoPathFoundException(NoPathFoundException.ErrorType.NO_PATH_FOUND);
        }
        sw.split();
        log.info("Calcul du chemin en {}", sw.toSplitString());

        // Transformation des nodes en points
        List<Point> points = graphNodes.parallelStream()
                .map(g -> new Point(g.x(), g.y()))
                .collect(Collectors.toList());

        // Le point 0 est le "from".
        // On exclus le dernier point qui est le "to"
        Chemin c = new Chemin();
        Double anglePrecedent = null;
        for (int i = 1; i < points.size() - 1; i++) {
            Point ptPrec = points.get(i - 1);
            Point pt = points.get(i);
            // Calcul de l'angle avec le point précédent
            // Si l'angle est différent de 0, alors c'est un point de passage
            double dX = pt.getX() - ptPrec.getX();
            double dY = pt.getY() - ptPrec.getY();
            double angle = Math.toDegrees(Math.atan2(dY, dX));
            if (anglePrecedent != null && angle != anglePrecedent) {
                c.addPoint(ptPrec);
            }
            anglePrecedent = angle;
        }

        // Ajout du dernier point.
        c.addPoint(to);

        sw.split();
        log.info("Chemin de {} point(s) de passage filtré en {}", c.nbPoints() - 1, sw.toSplitString());
        sw.stop();

        saveImageForPath(from, c);

        return c;
    }

    @Override
    public void addObstacles(Shape... obstacles) {
        log.info("Ajout de {} obstacles", obstacles.length);

        if (ArrayUtils.isEmpty(obstacles)) {
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

        int dx = img.getWidth() / getNbTileX();
        int dy = img.getHeight() / getNbTileY();
        int sx = dx / 2, sy = dy / 2;

        // Use deltaX to avoid horizontal wrap around edges
        int deltaX = getNbTileX() + 1; // must be > tilesX

        float hCost = dx, vCost = dy, dCost = (float) Math.sqrt(dx * dx + dy * dy);
        float cost;
        int px, py, nodeID, color;
        GraphNode aNode;

        // On initialise à 50 % du maillage pour gagner du temps sur les allocations mémoires.
        workGraph = new Graph(getNbTileX() * getNbTileY());

        py = sy;
        for (int y = 0; y < getNbTileY(); y++) {
            nodeID = deltaX * y + deltaX;
            px = sx;
            for (int x = 0; x < getNbTileX(); x++) {
                // Calculate the cost
                color = img.getRGB(px, py) & 0xFF;
                cost = 1;

                // Si la couleur n'est pas noir, on ajoute les noeuds et les liens.
                if (color != 0) {
                    aNode = new GraphNode(nodeID, px, py);
                    workGraph.addNode(aNode);
                    if (x > 0) {
                        workGraph.addEdge(nodeID, nodeID - 1, hCost * cost);
                        if (isAllowDiagonal()) {
                            workGraph.addEdge(nodeID, nodeID - deltaX - 1, dCost * cost);
                            workGraph.addEdge(nodeID, nodeID + deltaX - 1, dCost * cost);
                        }
                    }
                    if (x < getNbTileX() - 1) {
                        workGraph.addEdge(nodeID, nodeID + 1, hCost * cost);
                        if (isAllowDiagonal()) {
                            workGraph.addEdge(nodeID, nodeID - deltaX + 1, dCost * cost);
                            workGraph.addEdge(nodeID, nodeID + deltaX + 1, dCost * cost);
                        }
                    }
                    if (y > 0)
                        workGraph.addEdge(nodeID, nodeID - deltaX, vCost * cost);
                    if (y < getNbTileY() - 1)
                        workGraph.addEdge(nodeID, nodeID + deltaX, vCost * cost);
                }
                px += dx;
                nodeID++;
            }
            py += dy;
        }
        pf = null; // Reset path finder pour forcer a la prochaine demande la reconstruction par rapport au dernier graph

        sw.split();
        log.info("Construction du graph en {}", sw.toSplitString());
        sw.stop();
    }

    private void definePathFinder() {
        if (pf == null) {
            switch (getAlgorithm()) {
                case A_STAR_EUCLIDIAN:
                    pf = new GraphSearch_Astar(workGraph, new AshCrowFlight(aStarCostFactor));
                    break;
                case A_STAR_MANHATTAN:
                    pf = new GraphSearch_Astar(workGraph, new AshManhattan(aStarCostFactor));
                    break;
                case BREADTH_FIRST_SEARCH:
                    pf = new GraphSearch_BFS(workGraph);
                    break;
                case DEPTH_FIRST_SEARCH:
                    pf = new GraphSearch_DFS(workGraph);
                    break;
                case DIJKSTRA:
                    pf = new GraphSearch_Dijkstra(workGraph);
                    break;
            }
        }

        Assert.notNull(pf, "Le path finding ne peut être null après sa définition.");
    }

    public void setAlgorithm(PathFinderAlgorithm algorithm) {
        this.algorithm = algorithm;
        pf = null;
    }

    public void setAStartCostFactor(double factor) {
        this.aStarCostFactor = factor;
        pf = null;
    }
}
