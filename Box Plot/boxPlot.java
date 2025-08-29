import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import javax.swing.*;

public class boxPlot extends JPanel {
    private java.util.List<Double> data;
    private double min, q1, median, q3, max;

    private int padding = 60;
    private int labelPadding = 40;
    private int boxWidth = 80;
    private int numberYDivisions = 10;

    public boxPlot(java.util.List<Double> data) {
        this.data = data;
        Collections.sort(this.data);
        computeFiveNumberSummary();
        setPreferredSize(new Dimension(500, 600));
    }

    private void computeFiveNumberSummary() {
        min = data.get(0);
        max = data.get(data.size() - 1);
        median = computeMedian(data, 0, data.size() - 1);
        q1 = computeMedian(data, 0, (data.size() - 1) / 2);
        q3 = computeMedian(data, (data.size() + 1) / 2, data.size() - 1);
    }

    private double computeMedian(java.util.List<Double> d, int start, int end) {
        int length = end - start + 1;
        int mid = start + length / 2;
        if (length % 2 == 0) {
            return (d.get(mid - 1) + d.get(mid)) / 2.0;
        } else {
            return d.get(mid);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;

     
        g2.setColor(Color.BLACK);
        g2.drawLine(padding, padding, padding, height - padding); 
        g2.drawLine(padding, height - padding, width - padding, height - padding); 

        
        for (int i = 0; i <= numberYDivisions; i++) {
            double val = min + i * (max - min) / numberYDivisions;
            int y = valueToY(val, height);
            g2.setColor(Color.GRAY);
            g2.drawLine(padding - 5, y, width - padding, y); 
            g2.setColor(Color.BLACK);
            String label = String.format("%.2f", val);
            g2.drawString(label, padding - labelPadding, y + 5);
        }

        
        int q1Y = valueToY(q1, height);
        int q3Y = valueToY(q3, height);
        int medianY = valueToY(median, height);
        int minY = valueToY(min, height);
        int maxY = valueToY(max, height);

        int centerXBox = centerX;


        g2.setColor(Color.BLACK);
        g2.drawLine(centerXBox, minY, centerXBox, q1Y);
        g2.drawLine(centerXBox, q3Y, centerXBox, maxY);

        
        g2.setColor(new Color(100, 149, 237, 180)); 
        g2.fillRect(centerXBox - boxWidth / 2, q3Y, boxWidth, q1Y - q3Y);
        g2.setColor(Color.BLACK);
        g2.drawRect(centerXBox - boxWidth / 2, q3Y, boxWidth, q1Y - q3Y);

        
        g2.drawLine(centerXBox - boxWidth / 2, medianY, centerXBox + boxWidth / 2, medianY);

        
        g2.drawLine(centerXBox - 20, minY, centerXBox + 20, minY);
        g2.drawLine(centerXBox - 20, maxY, centerXBox + 20, maxY);

        
        g2.drawString("Min", centerXBox + boxWidth / 2 + 10, minY + 5);
        g2.drawString("Q1", centerXBox + boxWidth / 2 + 10, q1Y + 5);
        g2.drawString("Median", centerXBox + boxWidth / 2 + 10, medianY + 5);
        g2.drawString("Q3", centerXBox + boxWidth / 2 + 10, q3Y + 5);
        g2.drawString("Max", centerXBox + boxWidth / 2 + 10, maxY + 5);

        
        g2.drawString("Values", padding - labelPadding, padding - 10); 
        g2.drawString("Box Plot", centerXBox - 20, height - padding + 30); 
    }

    private int valueToY(double val, int height) {
        return (int) (height - padding - ((val - min) / (max - min)) * (height - 2 * padding));
    }

    
    public static Map<String, java.util.List<Double>> readCSVColumns(String filepath) {
        Map<String, java.util.List<Double>> columns = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String headerLine = br.readLine();
            if (headerLine == null) return columns;

            String[] headers = headerLine.split(",");
            for (String h : headers) columns.put(h.trim(), new ArrayList<>());

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                for (int i = 0; i < parts.length; i++) {
                    try {
                        double val = Double.parseDouble(parts[i].trim());
                        columns.get(headers[i].trim()).add(val);
                    } catch (Exception e) {
                        
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
        return columns;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter CSV file path: ");
        String csvFile = scanner.nextLine().trim();

        Map<String, java.util.List<Double>> columns = readCSVColumns(csvFile);

        if (columns.isEmpty()) {
            System.out.println("No numeric data found.");
            System.exit(0);
        }

        
        System.out.println("\nAvailable numeric columns:");
        java.util.List<String> keys = new ArrayList<>(columns.keySet());
        for (int i = 0; i < keys.size(); i++) {
            if (!columns.get(keys.get(i)).isEmpty()) {
                System.out.println((i + 1) + ". " + keys.get(i));
            }
        }

        System.out.print("\nEnter column number to plot: ");
        int choice = scanner.nextInt();
        if (choice < 1 || choice > keys.size() || columns.get(keys.get(choice - 1)).isEmpty()) {
            System.out.println("Invalid choice.");
            System.exit(0);
        }

        java.util.List<Double> data = columns.get(keys.get(choice - 1));
        JFrame frame = new JFrame("Box Plot - " + keys.get(choice - 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new boxPlot(data));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
