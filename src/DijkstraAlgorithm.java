import java.util.*;

public class DijkstraAlgorithm {
    private Graph graph;
    private Map<String, Double> trackedCosts;
    private List<String> processedNodes;
    private Map<String, String> trackedParents;
    private List<String> optimalPath;
    private List<Knoten> visitedNodes;

    public DijkstraAlgorithm(Graph graph) {
        this.graph = graph;
        this.trackedCosts = new HashMap<>();
        this.processedNodes = new ArrayList<>();
        this.trackedParents = new HashMap<>();
        this.optimalPath = new ArrayList<>();
        this.visitedNodes = new ArrayList<>();
    }

    public void calculateShortestPath(String startName, String endName) {
        graph.reset();
        trackedCosts.clear();
        processedNodes.clear();
        trackedParents.clear();
        optimalPath.clear();
        visitedNodes.clear();

        // Setze Startknoten
        Knoten startNode = graph.getNodeByName(startName);
        if (startNode == null) return;

        startNode.setDistance(0);
        trackedCosts.put(startName, 0.0);

        PriorityQueue<Knoten> queue = new PriorityQueue<>(Comparator.comparingDouble(Knoten::getDistance));
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Knoten current = queue.poll();
            visitedNodes.add(current);

            if (current.getName().equals(endName)) {
                break;
            }

            if (current.isProcessed()) continue;
            current.setProcessed(true);
            processedNodes.add(current.getName());

            for (Knoten neighbor : current.getNeighbors()) {
                double distance = current.getDistance() + calculateDistance(current, neighbor);

                if (distance < neighbor.getDistance()) {
                    neighbor.setDistance(distance);
                    neighbor.setPrevious(current);
                    trackedCosts.put(neighbor.getName(), distance);
                    trackedParents.put(neighbor.getName(), current.getName());
                    queue.add(neighbor);
                }
            }
        }

        calculateOptimalPath(endName);
        markShortestPath();
    }

    private double calculateDistance(Knoten from, Knoten to) {
        // Euklidische Distanz
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void calculateOptimalPath(String endName) {
        optimalPath.clear();
        String currentNodeName = endName;

        while (currentNodeName != null) {
            optimalPath.add(0, currentNodeName);
            currentNodeName = trackedParents.get(currentNodeName);
        }
    }

    private void markShortestPath() {
        for (String nodeName : optimalPath) {
            Knoten node = graph.getNodeByName(nodeName);
            if (node != null) {
                node.setShortestPath(true);
            }
        }
    }

    public List<String> getOptimalPath() {
        return optimalPath;
    }

    public double getPathDistance() {
        if (optimalPath.isEmpty()) return 0;
        String endNode = optimalPath.get(optimalPath.size() - 1);
        return trackedCosts.getOrDefault(endNode, 0.0);
    }

    public List<Knoten> getVisitedNodes() {
        return visitedNodes;
    }
}