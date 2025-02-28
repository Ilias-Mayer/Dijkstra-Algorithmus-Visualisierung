import javax.swing.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Dateiauswahl-Dialog anzeigen
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Kartendatei auswählen");

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();

                // Graph aus Datei einlesen
                FileReader fileReader = new FileReader();
                Graph graph = fileReader.readGraphFromFile(filePath);

                if (graph.getNodes().isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Fehler: Die Datei enthält keine gültigen Knoten!",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // GUI erstellen und anzeigen
                GUI gui = new GUI(graph);
                gui.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}