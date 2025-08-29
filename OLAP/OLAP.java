import java.util.*;
import java.io.*;

public class OLAP {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter CSV file path: ");
        String filePath = scanner.nextLine();

        List<Map<String, String>> data = CSVReaderUtil.readFromCSV(filePath);
        if (data.isEmpty()) {
            System.out.println("No data found or error reading file.");
            return;
        }

        List<String> headers = new ArrayList<>(data.get(0).keySet());
        System.out.println("Columns detected: " + headers);

        while (true) {
            System.out.println("\n=== OLAP Menu ===");
            System.out.println("1. Slice");
            System.out.println("2. Dice");
            System.out.println("3. Roll-up");
            System.out.println("4. Drill-down");
            System.out.println("5. Cube");
            System.out.println("6. Exit");
            System.out.print("Select an option (1-6): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> {
                    String col = selectColumn(scanner, headers);
                    String val = selectValue(scanner, data, col);
                    List<Map<String, String>> slice = OLAPOperations.slice(data, col, val);
                    CSVWriterUtil.writeRecords(slice, "slice.csv");
                }
                case 2 -> {
                    String col1 = selectColumn(scanner, headers);
                    System.out.print("Enter min value for " + col1 + ": ");
                    String minVal = scanner.nextLine();
                    System.out.print("Enter max value for " + col1 + ": ");
                    String maxVal = scanner.nextLine();

                    String col2 = selectColumn(scanner, headers);
                    String val2 = selectValue(scanner, data, col2);

                    List<Map<String, String>> dice = OLAPOperations.dice(data, col1, minVal, maxVal, col2, val2);
                    CSVWriterUtil.writeRecords(dice, "dice.csv");
                }
                case 3 -> {
                    String col = selectColumn(scanner, headers);
                    Map<String, Double> rollup = OLAPOperations.rollup(data, col);
                    CSVWriterUtil.writeAggregatedData(rollup, "rollup.csv", col, "Total Amount");
                }
                case 4 -> {
                    String higherCol = selectColumn(scanner, headers);
                    String filterValue = selectValue(scanner, data, higherCol);

                    // Drill-down always by Month
                    Map<String, Double> drill = OLAPOperations.drillDownByMonth(data, higherCol, filterValue);
                    CSVWriterUtil.writeAggregatedData(drill, "drilldown.csv", "Month", "Total Amount");
                }
                case 5 -> {
                    System.out.println("Enter columns for Cube separated by comma: ");
                    String input = scanner.nextLine();
                    List<String> dimensions = Arrays.stream(input.split(",")).map(String::trim).toList();
                    Map<String, Double> cube = OLAPOperations.cube(data, dimensions);
                    CSVWriterUtil.writeAggregatedData(cube, "cube.csv", "Dimension Combination", "Total Amount");
                }
                case 6 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    // Helper: select column
    private static String selectColumn(Scanner scanner, List<String> headers) {
        System.out.println("Available columns: " + headers);
        System.out.print("Select a column: ");
        String col = scanner.nextLine();
        while (!headers.contains(col)) {
            System.out.print("Invalid column. Select again: ");
            col = scanner.nextLine();
        }
        return col;
    }

    // Helper: show unique values and select one
    private static String selectValue(Scanner scanner, List<Map<String, String>> data, String col) {
        Set<String> unique = new TreeSet<>();
        for (Map<String, String> r : data) unique.add(r.getOrDefault(col, "Unknown"));
        System.out.println("Possible values for " + col + ": " + unique);
        System.out.print("Select a value: ");
        String val = scanner.nextLine();
        while (!unique.contains(val)) {
            System.out.print("Invalid value. Select again: ");
            val = scanner.nextLine();
        }
        return val;
    }
}
