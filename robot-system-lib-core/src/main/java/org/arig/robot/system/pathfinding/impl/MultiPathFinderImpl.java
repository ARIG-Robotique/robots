package org.arig.robot.system.pathfinding.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.system.pathfinding.AbstractPathFinder;
import org.arig.robot.system.pathfinding.PathFinderAlgorithm;
import org.arig.robot.utils.ImageUtils;
import org.arig.robot.vo.Chemin;
import org.arig.robot.vo.Point;
import org.springframework.util.Assert;
import pathfinder.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mythril on 29/12/13.
 */
@Slf4j
@NoArgsConstructor
public class MultiPathFinderImpl extends AbstractPathFinder {

    @Getter(AccessLevel.PROTECTED)
    private PathFinderAlgorithm algorithm;

    /** The PathFinder */
    private IGraphSearch pf = null;

    /** Facteur pour le calcul du cout des noeuds avec le A* */
    private float aStarCostFactor = 1.0f;

    /** The graph */
    private Graph graph;

    public MultiPathFinderImpl(PathFinderAlgorithm algorithm) {
        super();
        setAlgorithm(algorithm);
    }

    public MultiPathFinderImpl(PathFinderAlgorithm algorithm, float aStarCostFactor) {
        super();
        setAlgorithm(algorithm);
        setAStartCostFactor(aStarCostFactor);
    }

    @Override
    public Chemin findPath(Point from, Point to) throws NoPathFoundException {
        return findPath(from, to, 1.0f);
    }

    @Override
    public Chemin findPath(Point from, Point to, float maxDistance) throws NoPathFoundException {
        Assert.notNull(graph, "Le graph de la carte doit être initialisé");

        if (pf == null) {
            definePathFinder();
        }

        log.info("Recherche de chemin de {} a {} avec l'algorithme {}", from.toString(), to.toString(), getAlgorithm().toString());

        // Pour les stats de temps
        StopWatch sw = new StopWatch();
        sw.start();

        GraphNode startNode, endNode;
        if ((startNode = graph.getNodeAt(from.getX(), from.getY(), 0, maxDistance)) == null) {
            log.error("Impossible de trouver le noeud de départ");
            throw new NoPathFoundException(NoPathFoundException.ErrorType.START_NODE_DOES_NOT_EXIST);
        }
        if ((endNode = graph.getNodeAt(to.getX(), to.getY(), 0, maxDistance)) == null) {
            log.error("Impossible de trouver le noeud d'arrivé");
            throw new NoPathFoundException(NoPathFoundException.ErrorType.END_NODE_DOES_NOT_EXIST);
        }
        sw.split();
        log.info("Récupération des nodes en {}", sw.toSplitString());

        sw.unsplit();
        LinkedList<GraphNode> graphNodes = pf.search(startNode.id(), endNode.id(), true);
        if (graphNodes.isEmpty()) {
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
        for (int i = 1 ; i < points.size() - 1 ; i++) {
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

        // Ecriture d'une image pour le path finding
        LinkedList<Point> pts = new LinkedList<>();
        pts.add(from);
        pts.addAll(c.getPoints());
        saveImagePath(pts);

        return c;
    }

    @Override
    public void construitGraphDepuisImageNoirEtBlanc(File file) {
        if (!file.exists() && !file.canRead()) {
            String errorMessage = String.format("Impossible d'acceder au fichier %s (Existe : %s ; Readable : %s)", file.getAbsolutePath(), file.exists(), file.canRead());
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        super.setMapSource(file);

        StopWatch sw = new StopWatch();
        sw.start();

        BufferedImage img;
        try {
           img = ImageUtils.mirrorX(ImageIO.read(file));
        } catch (IOException e) {
            log.error("Impossible de lire l'image : " + e.toString());
            throw new RuntimeException(e);
        }

        Assert.notNull(img, "L'image ne peut pas être null");

        sw.split();
        log.info("Lecture de l'image pour le graph en {}", sw.toSplitString());


        int dx = img.getWidth() / getNbTileX();
        int dy = img.getHeight() / getNbTileY();
        int sx = dx / 2, sy = dy / 2;

        // Use deltaX to avoid horizontal wrap around edges
        int deltaX = getNbTileX() + 1; // must be > tilesX

        float hCost = dx, vCost = dy, dCost = (float) Math.sqrt(dx * dx + dy * dy);
        float cost;
        int px, py, nodeID, color;
        GraphNode aNode;
        graph = new Graph(getNbTileX() * getNbTileY()); // On initialise à 50 % du maillage pour gagner du temps sur les allocations mémoires.

        py = sy;
        for(int y = 0; y < getNbTileY() ; y++){
            nodeID = deltaX * y + deltaX;
            px = sx;
            for(int x = 0; x < getNbTileX(); x++){
                // Calculate the cost
                color = img.getRGB(px, py) & 0xFF;
                cost = 1;

                // Si la couleur n'est pas noir, on ajoute les noeuds et les liens.
                if(color != 0){
                    aNode = new GraphNode(nodeID, px, py);
                    graph.addNode(aNode);
                    if(x > 0){
                        graph.addEdge(nodeID, nodeID - 1, hCost * cost);
                        if(isAllowDiagonal()){
                            graph.addEdge(nodeID, nodeID - deltaX - 1, dCost * cost);
                            graph.addEdge(nodeID, nodeID + deltaX - 1, dCost * cost);
                        }
                    }
                    if(x < getNbTileX() -1){
                        graph.addEdge(nodeID, nodeID + 1, hCost * cost);
                        if(isAllowDiagonal()){
                            graph.addEdge(nodeID, nodeID - deltaX + 1, dCost * cost);
                            graph.addEdge(nodeID, nodeID + deltaX + 1, dCost * cost);
                        }
                    }
                    if(y > 0)
                        graph.addEdge(nodeID, nodeID - deltaX, vCost * cost);
                    if(y < getNbTileY() - 1)
                        graph.addEdge(nodeID, nodeID + deltaX, vCost * cost);
                }
                px += dx;
                nodeID++;
            }
            py += dy;
        }

        sw.split();
        log.info("Construction du graph en {}", sw.toSplitString());
        sw.stop();
    }

    private void definePathFinder() {
        if (pf == null) {
            switch (getAlgorithm()) {
                case A_STAR_EUCLIDIAN:
                    pf = new GraphSearch_Astar(graph, new AshCrowFlight(aStarCostFactor));
                    break;
                case A_STAR_MANHATTAN:
                    pf = new GraphSearch_Astar(graph, new AshManhattan(aStarCostFactor));
                    break;
                case BREADTH_FIRST_SEARCH:
                    pf = new GraphSearch_BFS(graph);
                    break;
                case DEPTH_FIRST_SEARCH:
                    pf = new GraphSearch_DFS(graph);
                    break;
                case DIJKSTRA:
                    pf = new GraphSearch_Dijkstra(graph);
                    break;
            }
        }

        Assert.notNull(pf, "Le path finding ne peut être null après sa définition.");
    }

    @Override
    protected String suffixResultImageName() {
        return "-" + algorithm.name();
    }

    public void setAlgorithm(PathFinderAlgorithm algorithm) {
        this.algorithm = algorithm;
        pf = null;
    }

    public void setAStartCostFactor(float factor) {
        this.aStarCostFactor = factor;
        pf = null;
    }
}
