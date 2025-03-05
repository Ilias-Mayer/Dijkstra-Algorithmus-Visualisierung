import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileReader {
    // Liest einen Graphen aus einer Datei und erstellt Knoten und Kanten
    public Graph readGraphFromFile(String filePath) {
        Graph graph = new Graph();
        Map<String, String[]> connections = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            // regex pattern erlaubt auch negative Koordinaten in der Datei
            Pattern pattern = Pattern.compile("([A-Za-z0-9]+)\\((-?\\d+),\\s*(-?\\d+)\\),\\s*(.*)");

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    String nodeName = matcher.group(1);
                    int x = Integer.parseInt(matcher.group(2));
                    int y = Integer.parseInt(matcher.group(3));
                    String neighborsList = matcher.group(4);

                    // Verbindungen speichern
                    String[] neighbors = neighborsList.split(",\\s*");
                    connections.put(nodeName, neighbors);

                    // Knoten erstellen und zum Graphen hinzufügen
                    Knoten node = new Knoten(nodeName, x, y);
                    graph.addNode(node);
                } else {
                    System.err.println("Ungültiges Zeilenformat: " + line);
                }
            }

            // Verbindungen herstellen
            for (Map.Entry<String, String[]> entry : connections.entrySet()) {
                Knoten from = graph.getNodeByName(entry.getKey());
                if (from == null) continue;

                for (String toName : entry.getValue()) {
                    Knoten to = graph.getNodeByName(toName);
                    if (to != null) {
                        graph.addEdge(from, to);
                    } else {
                        System.err.println("Knoten nicht gefunden: " + toName);
                    }
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Dateifehler: " + e.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Unerwarteter Fehler: " + e.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return graph;
    }
}