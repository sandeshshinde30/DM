import java.io.*;
import java.util.*;

public class TDWeightCalculator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter CSV file path: ");
        String csvFile = scanner.nextLine();

        List<String[]> data = new ArrayList<>();
        String[] headers = null;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                if (firstLine) {
                    headers = row;
                    firstLine = false;
                } else {
                    data.add(row);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
            return;
        }

        System.out.println("Columns in CSV:");
        for (int i = 0; i < headers.length; i++) {
            System.out.println(i + ": " + headers[i]);
        }

        System.out.print("Enter target column number: ");
        int targetCol = scanner.nextInt();

        System.out.print("Enter grouping column number: ");
        int groupCol = scanner.nextInt();

        Map<String, Map<String, Integer>> groupFreq = new HashMap<>();
        Map<String, Integer> groupTotal = new HashMap<>();
        Map<String, Integer> valueTotal = new HashMap<>();

        for (String[] row : data) {
            String groupKey = row[groupCol];
            String valueKey = row[targetCol];

            groupFreq.putIfAbsent(groupKey, new HashMap<>());
            Map<String, Integer> valueMap = groupFreq.get(groupKey);
            valueMap.put(valueKey, valueMap.getOrDefault(valueKey, 0) + 1);

            groupTotal.put(groupKey, groupTotal.getOrDefault(groupKey, 0) + 1);
            valueTotal.put(valueKey, valueTotal.getOrDefault(valueKey, 0) + 1);
        }

        try (PrintWriter pw = new PrintWriter(new File("t_weight.csv"))) {
            pw.println(headers[groupCol] + "," + headers[targetCol] + ",t_weight(%)");
            for (String group : groupFreq.keySet()) {
                Map<String, Integer> valueMap = groupFreq.get(group);
                int totalInGroup = groupTotal.get(group);
                for (String value : valueMap.keySet()) {
                    double tWeight = (valueMap.get(value) / (double) totalInGroup) * 100;
                    pw.println(group + "," + value + "," + String.format("%.2f", tWeight));
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing t_weight.csv: " + e.getMessage());
        }

        try (PrintWriter pw = new PrintWriter(new File("d_weight.csv"))) {
            pw.println(headers[groupCol] + "," + headers[targetCol] + ",d_weight(%)");
            for (String group : groupFreq.keySet()) {
                Map<String, Integer> valueMap = groupFreq.get(group);
                for (String value : valueMap.keySet()) {
                    double dWeight = (valueMap.get(value) / (double) valueTotal.get(value)) * 100;
                    pw.println(group + "," + value + "," + String.format("%.2f", dWeight));
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing d_weight.csv: " + e.getMessage());
        }

        System.out.println("t_weight.csv and d_weight.csv created successfully!");
    }
}
