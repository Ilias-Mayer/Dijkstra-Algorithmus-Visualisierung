import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GUI extends JFrame {
    private Graph graph;
    private JComboBox<String> startNodeComboBox;
    private JComboBox<String> targetNodeComboBox;
    private JButton runAlgorithmButton;
    private JTextArea resultTextArea;
    private GraphPanel graphPanel;
    private DijkstraAlgorithm dijkstraAlgorithm;
    private ExecutorService executorService;

    public GUI(Graph graph) {
        this.graph = graph;
        this.dijkstraAlgorithm = new DijkstraAlgorithm(graph);
        this.executorService = Executors.newSingleThreadExecutor();
        initializeUI();
        updateNodeComboBoxes();
    }

    private void initializeUI() {
        setTitle("Routenplaner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Layout setup
        setLayout(new BorderLayout());

        // Control Panel (left side)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.setPreferredSize(new Dimension(300, 600));

        // Startpunkt Auswahl
        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startPanel.add(new JLabel("Startpunkt:"));
        startNodeComboBox = new JComboBox<>();
        for (Knoten node : graph.getNodes()) {
            startNodeComboBox.addItem(node.getName());
        }
        startPanel.add(startNodeComboBox);
        controlPanel.add(startPanel);

        // Zielpunkt Auswahl
        JPanel targetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        targetPanel.add(new JLabel("Zielpunkt:"));
        targetNodeComboBox = new JComboBox<>();
        for (Knoten node : graph.getNodes()) {
            targetNodeComboBox.addItem(node.getName());
        }
        targetPanel.add(targetNodeComboBox);
        controlPanel.add(targetPanel);

        // Algorithmus Start Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        runAlgorithmButton = new JButton("Starte Route Berechnung");
        runAlgorithmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRunAlgorithmButtonClicked();
            }
        });
        buttonPanel.add(runAlgorithmButton);
        controlPanel.add(buttonPanel);

        // Ergebnis Textfeld
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        resultTextArea = new JTextArea(10, 25);
        resultTextArea.setEditable(false);
        resultPanel.add(new JLabel("Ergebnis:"), BorderLayout.NORTH);
        resultPanel.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);
        controlPanel.add(resultPanel);

        // Graph Panel (right side)
        graphPanel = new GraphPanel(graph);

        // Add panels to frame
        add(controlPanel, BorderLayout.WEST);
        add(graphPanel, BorderLayout.CENTER);

        // Initial size
        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        if (graph.getNodes().isEmpty()) {
            runAlgorithmButton.setEnabled(false);
            resultTextArea.setText("Fehler: Keine Knoten geladen!");
        }
    }

    private void updateNodeComboBoxes() {
        startNodeComboBox.removeAllItems();
        targetNodeComboBox.removeAllItems();

        for (Knoten node : graph.getNodes()) {
            startNodeComboBox.addItem(node.getName());
            targetNodeComboBox.addItem(node.getName());
        }
    }

    private void onRunAlgorithmButtonClicked() {
        String startNodeName = (String) startNodeComboBox.getSelectedItem();
        String targetNodeName = (String) targetNodeComboBox.getSelectedItem();

        if (startNodeName.equals(targetNodeName)) {
            resultTextArea.setText("Startpunkt und Zielpunkt sind identisch!");
            return;
        }

        // Reset visualization before starting a new calculation
        graphPanel.resetAnimation();

        // UI-Elemente deaktivieren während der Berechnung
        runAlgorithmButton.setEnabled(false);
        resultTextArea.setText("Berechnung läuft...");

        // Animation in separatem Thread durchführen
        executorService.submit(() -> {
            try {
                // Dijkstra ausführen
                dijkstraAlgorithm.calculateShortestPath(startNodeName, targetNodeName);
                List<String> optimalPath = dijkstraAlgorithm.getOptimalPath();
                double pathDistance = dijkstraAlgorithm.getPathDistance();

                // Visualisierung aktualisieren
                List<Knoten> visitedNodes = dijkstraAlgorithm.getVisitedNodes();
                for (Knoten node : visitedNodes) {
                    graphPanel.addVisitedNode(node);
                    graphPanel.repaint();
                    Thread.sleep(100); // Animationspause
                }

                // Ergebnis anzeigen
                SwingUtilities.invokeLater(() -> {
                    StringBuilder result = new StringBuilder();
                    result.append("Kürzester Weg von ").append(startNodeName)
                            .append(" nach ").append(targetNodeName).append(":\n");

                    if (optimalPath.isEmpty() || optimalPath.size() == 1) {
                        result.append("Kein Weg gefunden!");
                    } else {
                        result.append("Route: ");
                        for (int i = 0; i < optimalPath.size(); i++) {
                            result.append(optimalPath.get(i));
                            if (i < optimalPath.size() - 1) {
                                result.append(" -> ");
                            }
                        }
                        result.append("\n");
                        result.append("Entfernung: ").append(String.format("%.2f", pathDistance));
                    }

                    resultTextArea.setText(result.toString());
                    graphPanel.setOptimalPath(optimalPath);
                    graphPanel.repaint();

                    // UI-Elemente wieder aktivieren
                    runAlgorithmButton.setEnabled(true);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    resultTextArea.setText("Fehler bei der Berechnung: " + e.getMessage());
                    runAlgorithmButton.setEnabled(true);
                });
            }
        });
    }
    public class GraphPanel extends JPanel {
        private Graph graph;
        private List<String> optimalPath;
        private List<Knoten> visitedNodes;
        private int padding = 50;

        public GraphPanel(Graph graph) {
            this.graph = graph;
            this.optimalPath = new ArrayList<>();
            this.visitedNodes = new ArrayList<>();
            setBackground(Color.WHITE);
        }

        public void setOptimalPath(List<String> optimalPath) {
            this.optimalPath = new ArrayList<>(optimalPath); // Create a new list to avoid reference issues
        }

        public void addVisitedNode(Knoten node) {
            this.visitedNodes.add(node);
        }

        public void resetAnimation() {
            this.visitedNodes.clear();
            this.optimalPath.clear(); // Also clear the optimal path
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (graph.getNodes().isEmpty()) return;

            // Koordinatentransformation - mit korrekter Behandlung negativer Werte
            int minX = graph.getMinX();
            int maxX = graph.getMaxX();
            int minY = graph.getMinY();
            int maxY = graph.getMaxY();

            // Die tatsächliche Breite und Höhe berücksichtigen, nicht die Differenz
            // Dies ist wichtig für den Umgang mit negativen Koordinaten
            int graphWidth = maxX - minX;
            int graphHeight = maxY - minY;

            double scaleX = (getWidth() - 2 * padding) / (double) graphWidth;
            double scaleY = (getHeight() - 2 * padding) / (double) graphHeight;
            double scale = Math.min(scaleX, scaleY);

            AffineTransform transform = new AffineTransform();
            transform.translate(padding, padding);
            transform.scale(scale, scale);
            transform.translate(-minX, -minY);

            // Zeichne Kanten
            for (Knoten node : graph.getNodes()) {
                Point2D.Double p1 = new Point2D.Double(node.getX(), node.getY());

                for (Knoten neighbor : node.getNeighbors()) {
                    Point2D.Double p2 = new Point2D.Double(neighbor.getX(), neighbor.getY());

                    // Prüfe, ob es eine Zweiweg-Straße ist
                    boolean isTwoWay = neighbor.getNeighbors().contains(node);

                    // Überprüfe, ob die Kante Teil des besuchten Pfades ist
                    boolean isVisitedEdge = false;
                    for (int i = 0; i < visitedNodes.size() - 1; i++) {
                        Knoten current = visitedNodes.get(i);
                        Knoten next = visitedNodes.get(i + 1);
                        if (current == node && next == neighbor) {
                            isVisitedEdge = true;
                            break;
                        }
                    }

                    // Überprüfe, ob die Kante Teil des optimalen Pfades ist
                    boolean isOptimalEdge = false;
                    for (int i = 0; i < optimalPath.size() - 1; i++) {
                        String currentName = optimalPath.get(i);
                        String nextName = optimalPath.get(i + 1);
                        if (node.getName().equals(currentName) && neighbor.getName().equals(nextName)) {
                            isOptimalEdge = true;
                            break;
                        }
                    }

                    // Farbe basierend auf Status der Kante festlegen
                    if (isOptimalEdge) {
                        g2d.setColor(Color.GREEN);
                        g2d.setStroke(new BasicStroke(3));
                    } else if (isVisitedEdge) {
                        g2d.setColor(Color.RED);
                        g2d.setStroke(new BasicStroke(2));
                    } else {
                        g2d.setColor(Color.BLACK);
                        g2d.setStroke(new BasicStroke(1));
                    }

                    // Kante zeichnen
                    Line2D.Double line = new Line2D.Double(p1, p2);
                    g2d.draw(transform.createTransformedShape(line));

                    // Pfeile zeichnen
                    drawArrow(g2d, transform, p1, p2);
                    if (isTwoWay) {
                        drawArrow(g2d, transform, p2, p1);
                    }
                }
            }

            // Zeichne Knoten
            g2d.setStroke(new BasicStroke(1));
            for (Knoten node : graph.getNodes()) {
                Point2D.Double p = new Point2D.Double(node.getX(), node.getY());
                Point2D transformed = transform.transform(p, null);

                // Knoten basierend auf Status einfärben
                if (optimalPath.contains(node.getName())) {
                    g2d.setColor(Color.GREEN);
                } else if (visitedNodes.contains(node)) {
                    g2d.setColor(Color.RED);
                } else {
                    g2d.setColor(Color.YELLOW);
                }

                int nodeSize = 20;
                g2d.fillOval((int) transformed.getX() - nodeSize/2,
                        (int) transformed.getY() - nodeSize/2,
                        nodeSize, nodeSize);

                // Knotenname
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(node.getName(),
                        (int) transformed.getX() - fm.stringWidth(node.getName()) / 2,
                        (int) transformed.getY() + 5);
            }
        }

        private void drawArrow(Graphics2D g2d, AffineTransform transform, Point2D.Double from, Point2D.Double to) {
            double dx = to.x - from.x;
            double dy = to.y - from.y;
            double length = Math.sqrt(dx * dx + dy * dy);
            if (length == 0) return; // Vermeide Division durch Null

            // Normalisierte Richtung
            double unitDx = dx / length;
            double unitDy = dy / length;

            // Pfeilgröße anpassen (unabhängig vom Zoom)
            double arrowSize = 10 / transform.getScaleX();
            double arrowAngle = Math.PI / 6; // 30 Grad Winkel

            // Position der Pfeilspitze (leicht vor dem Endpunkt)
            double offset = 15 / transform.getScaleX();
            double tipX = to.x - unitDx * offset;
            double tipY = to.y - unitDy * offset;

            // Berechnung der Pfeilspitzen
            double angle = Math.atan2(dy, dx);
            double x1 = tipX - arrowSize * Math.cos(angle - arrowAngle);
            double y1 = tipY - arrowSize * Math.sin(angle - arrowAngle);
            double x2 = tipX - arrowSize * Math.cos(angle + arrowAngle);
            double y2 = tipY - arrowSize * Math.sin(angle + arrowAngle);

            // Pfeil zeichnen
            Path2D.Double arrow = new Path2D.Double();
            arrow.moveTo(tipX, tipY);
            arrow.lineTo(x1, y1);
            arrow.lineTo(x2, y2);
            arrow.closePath();

            g2d.fill(transform.createTransformedShape(arrow));
        }
    }
}