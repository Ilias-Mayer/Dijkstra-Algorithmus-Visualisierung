import java.util.ArrayList;
import java.util.List;

public class Knoten {
    private String name;
    private int x;
    private int y;
    private List<Knoten> neighbors;
    private double distance;
    private Knoten previous;
    private boolean processed;
    private boolean shortestPath;

    public Knoten(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.neighbors = new ArrayList<>();
        this.distance = Double.POSITIVE_INFINITY;
        this.previous = null;
        this.processed = false;
        this.shortestPath = false;
    }

    public void addNeighbor(Knoten neighbor) {
        if (!neighbors.contains(neighbor)) {
            neighbors.add(neighbor);
        }
    }

    public List<Knoten> getNeighbors() {
        return neighbors;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Knoten getPrevious() {
        return previous;
    }

    public void setPrevious(Knoten previous) {
        this.previous = previous;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public boolean isShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(boolean shortestPath) {
        this.shortestPath = shortestPath;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return name;
    }
}