import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private List<Knoten> nodes;
    private Map<String, Knoten> nodeMap; // schnell einen Knoten anhand seines Namens finden (Schnellzugriffs-Tabelle)

    public Graph() {
        this.nodes = new ArrayList<>();
        this.nodeMap = new HashMap<>();
    }

    public void addNode(Knoten node) {
        nodes.add(node);
        nodeMap.put(node.getName(), node);
    }

    public void addEdge(Knoten a, Knoten b) {
        a.addNeighbor(b);
    }

    public List<Knoten> getNodes() {
        return nodes;
    }

    public Knoten getNodeByName(String name) {
        return nodeMap.get(name);
    }

    public void reset() {
        for (Knoten node : nodes) {
            node.setDistance(Double.POSITIVE_INFINITY);
            node.setPrevious(null);
            node.setProcessed(false);
            node.setShortestPath(false);
        }
    }

    public int getMinX() {
        if (nodes.isEmpty()) return 0;
        return nodes.stream().mapToInt(Knoten::getX).min().getAsInt();
    }

    public int getMaxX() {
        if (nodes.isEmpty()) return 0;
        return nodes.stream().mapToInt(Knoten::getX).max().getAsInt();
    }

    public int getMinY() {
        if (nodes.isEmpty()) return 0;
        return nodes.stream().mapToInt(Knoten::getY).min().getAsInt();
    }

    public int getMaxY() {
        if (nodes.isEmpty()) return 0;
        return nodes.stream().mapToInt(Knoten::getY).max().getAsInt();
    }
}