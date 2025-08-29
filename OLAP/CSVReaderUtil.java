import java.io.*;
import java.util.*;

public class CSVReaderUtil {

    public static List<Map<String, String>> readFromCSV(String filePath) {
        List<Map<String, String>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            if (line == null) return data;

            String[] headers = line.split(",");

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);
                Map<String, String> record = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    String val = i < values.length ? values[i].trim() : "";
                    record.put(headers[i].trim(), val);
                }
                data.add(record);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
