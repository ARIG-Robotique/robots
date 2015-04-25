package org.arig.robot.system.pathfinding.impl;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Created by mythril on 29/12/13.
 */
@Slf4j
@NoArgsConstructor
public class MultiPathFinderImpl extends AbstractPathFinder<PathFinderAlgorithm> {

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

        // Démarrage
        long start = System.currentTimeMillis();

        GraphNode startNode;
        GraphNode endNode;

        if ((startNode = graph.getNodeAt(from.getX(), from.getY(), 0, maxDistance)) == null) {
            log.error("Impossible de trouver le noeud de départ");
            throw new NoPathFoundException(NoPathFoundException.ErrorType.START_NODE_DOES_NOT_EXIST);
        }
        if ((endNode = graph.getNodeAt(to.getX(), to.getY(), 0, maxDistance)) == null) {
            log.error("Impossible de trouver le noeud d'arrivé");
            throw new NoPathFoundException(NoPathFoundException.ErrorType.END_NODE_DOES_NOT_EXIST);
        }
        log.info("Récupération des nodes : " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        LinkedList<GraphNode> graphNodes = pf.search(startNode.id(), endNode.id(), true);
        if (graphNodes.isEmpty()) {
            log.error("Impossible de trouver le chemin pour le trajet.");
            throw new NoPathFoundException(NoPathFoundException.ErrorType.NO_PATH_FOUND);
        }
        log.info("Calcul du chemin : " + (System.currentTimeMillis() - start) + " ms");

        Chemin c = new Chemin();
        // TODO : Limiter le nombre de points a uniquement les changement de direction
        for (GraphNode gn : graphNodes) {
            c.addPoint(new Point(gn.x(), gn.y()));
        }
        log.info("Chemin de {} point(s)", c.nbPoints());

        return c;
    }

    @Override
    public void makeGraphFromBWImage(File file) {
        if (!file.exists() && !file.canRead()) {
            String errorMessage = String.format("Impossible d'acceder au fichier %s (Existe : %s ; Readable : %s)", file.getAbsolutePath(), file.exists(), file.canRead());
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        BufferedImage img;
        try {
           img = ImageUtils.mirrorX(ImageIO.read(file));
        } catch (IOException e) {
            log.error("Impossible de lire l'image : " + e.toString());
            throw new RuntimeException(e);
        }

        Assert.notNull(img, "L'image ne peut pas être null");

        int dx = img.getWidth() / getNbTileX();
        int dy = img.getHeight() / getNbTileY();
        int sx = dx / 2, sy = dy / 2;

        // Use deltaX to avoid horizontal wrap around edges
        int deltaX = getNbTileX() + 1; // must be > tilesX

        float hCost = dx, vCost = dy, dCost = (float) Math.sqrt(dx * dx + dy * dy);
        float cost;
        int px, py, nodeID, color;
        GraphNode aNode;
        graph = new Graph();

        py = sy;
        for(int y = 0; y < getNbTileY() ; y++){
            nodeID = deltaX * y + deltaX;
            px = sx;
            for(int x = 0; x < getNbTileX(); x++){
                // Calculate the cost
                color = img.getRGB(px, py) & 0xFF;
                cost = 1;

                // If color is not black then create the node and edges
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
    }

    @Override
    public void setAlgorithm(PathFinderAlgorithm algorithm) {
        super.setAlgorithm(algorithm);
        pf = null;
    }

    public void setAStartCostFactor(float factor) {
        this.aStarCostFactor = factor;
        pf = null;
    }
}
