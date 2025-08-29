import java.io.*;
import java.util.*;

public class CSVWriterUtil {

    public static void writeRecords(List<Map<String, String>> records, String filename) {
        if (records.isEmpty()) {
            System.out.println("No records to write.");
            return;
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            Set<String> headers = records.get(0).keySet();
            pw.println(String.join(",", headers));

            for (Map<String, String> r : records) {
                List<String> line = new ArrayList<>();
                for (String h : headers) line.add(r.getOrDefault(h, ""));
                pw.println(String.join(",", line));
            }
            System.out.println("Data written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeAggregatedData(Map<String, Double> data, String filename, String keyHeader, String valueHeader) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println(keyHeader + "," + valueHeader);
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                pw.println(entry.getKey() + "," + entry.getValue());
            }
            System.out.println("Aggregated data written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
