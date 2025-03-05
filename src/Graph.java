import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private List<Knoten> nodes;
    private Map<String, Knoten> nodeMap; // schnell einen Knoten anhand seines Namens finden (Schnellzugriffs-Tabelle)

    // Initialisiert eine neue Instanz des Graphen mit einer leeren Knotenliste und einer leeren Schnellzugriffs-Tabelle
    public Graph() {
        this.nodes = new ArrayList<>();
        this.nodeMap = new HashMap<>();
    }

    // Fügt einen Knoten zum Graphen hinzu und speichert ihn in der Schnellzugriffs-Tabelle
    public void addNode(Knoten node) {
        nodes.add(node);
        nodeMap.put(node.getName(), node);
    }

    // Fügt eine Kante zwischen den Knoten a und b hinzu
    public void addEdge(Knoten a, Knoten b) {
        a.addNeighbor(b);
    }

    public List<Knoten> getNodes() {
        return nodes;
    }

    public Knoten getNodeByName(String name) {
        return nodeMap.get(name);
    }

    // Setzt alle Knoten im Graphen zurück (Entfernt Distanzen, Vorgängerknoten, markiert sie als nicht verarbeitet und löscht den kürzesten Pfad)
    public void reset() {
        for (Knoten node : nodes) {
            node.setDistance(Double.POSITIVE_INFINITY);
            node.setPrevious(null);
            node.setProcessed(false);
            node.setShortestPath(false);
        }
    }

    // Gibt den minimalen x-Wert aller Knoten zurück
    public int getMinX() {
        if (nodes.isEmpty()) return 0;
        return nodes.stream().mapToInt(Knoten::getX).min().getAsInt();
    }

    // Gibt den maximalen x-Wert aller Knoten zurück
    public int getMaxX() {
        if (nodes.isEmpty()) return 0;
        return nodes.stream().mapToInt(Knoten::getX).max().getAsInt();
    }

    // Gibt den minimalen y-Wert aller Knoten zurück
    public int getMinY() {
        if (nodes.isEmpty()) return 0;
        return nodes.stream().mapToInt(Knoten::getY).min().getAsInt();
    }

    // Gibt den maximalen y-Wert aller Knoten zurück
    public int getMaxY() {
        if (nodes.isEmpty()) return 0;
        return nodes.stream().mapToInt(Knoten::getY).max().getAsInt();
    }
}