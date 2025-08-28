import java.io.*;
import java.util.*;

public class normalization {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get CSV file path
        System.out.print("Enter path to CSV file: ");
        String csvFile = scanner.nextLine();

        // Menu
        System.out.println("Choose normalization method:");
        System.out.println("1. Min-Max Scaling (custom range)");
        System.out.println("2. Z-Score Normalization");
        System.out.println("3. Decimal Scaling Normalization");
        System.out.print("Your choice (1/2/3): ");
        int choice = scanner.nextInt();

        double userMin = 0, userMax = 0;
        if (choice == 1) {
            System.out.print("Enter desired minimum value: ");
            userMin = scanner.nextDouble();
            System.out.print("Enter desired maximum value: ");
            userMax = scanner.nextDouble();
        }

        scanner.nextLine(); // clear buffer
        String outputCsv = "scaled_output.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.out.println("Empty or invalid CSV.");
                return;
            }

            String[] headers = headerLine.split(",");

            // Show columns
            System.out.println("\nAvailable columns:");
            for (int i = 0; i < headers.length; i++) {
                System.out.println((i + 1) + ". " + headers[i].trim());
            }

            System.out.print("Enter column name to normalize: ");
            String columnName = scanner.nextLine().trim();

            int columnIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex == -1) {
                System.out.println("Column not found.");
                return;
            }

            // Read all rows
            ArrayList<String[]> allRows = new ArrayList<>();
            ArrayList<Double> selectedColumn = new ArrayList<>();

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // skip blank lines

                String[] row = line.split(",");
                if (row.length <= columnIndex) continue; // skip broken rows

                allRows.add(row);
                try {
                    selectedColumn.add(Double.parseDouble(row[columnIndex].trim()));
                } catch (NumberFormatException e) {
                    selectedColumn.add(0.0); // e.g., "Species"
                }
            }

            double[] original = new double[selectedColumn.size()];
            for (int i = 0; i < selectedColumn.size(); i++) {
                original[i] = selectedColumn.get(i);
            }

            // Normalize
            double[] normalized;
            if (choice == 1) {
                normalized = minMaxScale(original, userMin, userMax);
            } else if (choice == 2) {
                normalized = zScoreNormalize(original);
            } else {
                normalized = decimalScaling(original);
            }

            // Write output CSV
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputCsv))) {
                bw.write(headers[columnIndex] + "," +
                        (choice == 1 ? "MinMax_" + userMin + "_" + userMax
                                : choice == 2 ? "Z_Score"
                                : "Decimal_Scaling"));
                bw.newLine();

                for (int i = 0; i < original.length; i++) {
                    bw.write(original[i] + "," + String.format("%.5f", normalized[i]));
                    bw.newLine();
                }
            }

            System.out.println("Output saved to: " + outputCsv);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        scanner.close();
    }

    // Min-Max Scaling
    public static double[] minMaxScale(double[] data, double minVal, double maxVal) {
        double min = Arrays.stream(data).min().getAsDouble();
        double max = Arrays.stream(data).max().getAsDouble();
        double[] scaled = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            if (max - min == 0) scaled[i] = minVal;
            else scaled[i] = minVal + ((data[i] - min) / (max - min)) * (maxVal - minVal);
        }
        return scaled;
    }

    // Z-Score Normalization
    public static double[] zScoreNormalize(double[] data) {
        int n = data.length;
        double mean = Arrays.stream(data).average().orElse(0.0);
        double variance = 0.0;
        for (double d : data) variance += (d - mean) * (d - mean);
        double stdDev = Math.sqrt(variance / n);

        double[] z = new double[n];
        for (int i = 0; i < n; i++) {
            if (stdDev == 0) z[i] = 0;
            else z[i] = (data[i] - mean) / stdDev;
        }
        return z;
    }

    // Decimal Scaling Normalization
    public static double[] decimalScaling(double[] data) {
        double maxAbs = Arrays.stream(data).map(Math::abs).max().orElse(1.0);
        int j = 0;
        while (maxAbs >= 1) {
            maxAbs /= 10;
            j++;
        }
        double[] scaled = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            scaled[i] = data[i] / Math.pow(10, j);
        }
        return scaled;
    }
}
