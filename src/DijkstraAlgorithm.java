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

        // Markiere Startstadt rot, Kennzahl 0
        Knoten startNode = graph.getNodeByName(startName);
        if (startNode == null) return;

        startNode.setDistance(0);
        trackedCosts.put(startName, 0.0);
        processedNodes.add(startName);

        PriorityQueue<Knoten> queue = new PriorityQueue<>(Comparator.comparingDouble(Knoten::getDistance));
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Knoten currentNode = queue.poll();
            visitedNodes.add(currentNode);

            // Prüfe, ob Zielstadt erreicht
            if (currentNode.getName().equals(endName)) {
                break;
            }

            // Gehe durch alle Nachbarstädte
            for (Knoten neighbor : currentNode.getNeighbors()) {
                // Wenn Nachbarstadt noch nicht rot markiert
                if (!processedNodes.contains(neighbor.getName())) {
                    // Berechne Kennzahl: bisherige Kennzahl + Streckenlänge
                    double newDistance = currentNode.getDistance() + calculateDistance(currentNode, neighbor);

                    // Wenn neue Kennzahl kleiner als bisherige
                    if (newDistance < neighbor.getDistance()) {
                        neighbor.setDistance(newDistance);
                        neighbor.setPrevious(currentNode);
                        trackedCosts.put(neighbor.getName(), newDistance);
                        trackedParents.put(neighbor.getName(), currentNode.getName());
                        queue.add(neighbor);
                    }
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