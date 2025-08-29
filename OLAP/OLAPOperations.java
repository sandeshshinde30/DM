import java.util.*;

public class OLAPOperations {

    public static List<Map<String, String>> slice(List<Map<String, String>> data, String column, String value) {
        List<Map<String, String>> result = new ArrayList<>();
        for (Map<String, String> r : data) {
            if (r.getOrDefault(column, "").equalsIgnoreCase(value)) result.add(r);
        }
        return result;
    }

    public static List<Map<String, String>> dice(List<Map<String, String>> data,
                                                 String col1, String minVal, String maxVal,
                                                 String col2, String val2) {
        List<Map<String, String>> result = new ArrayList<>();
        for (Map<String, String> r : data) {
            String v1 = r.getOrDefault(col1, "");
            String v2 = r.getOrDefault(col2, "");
            boolean inRange = false;
            try {
                double num = Double.parseDouble(v1);
                double min = Double.parseDouble(minVal);
                double max = Double.parseDouble(maxVal);
                inRange = num >= min && num <= max;
            } catch (NumberFormatException e) {
                inRange = v1.equalsIgnoreCase(minVal);
            }
            if (inRange && v2.equalsIgnoreCase(val2)) result.add(r);
        }
        return result;
    }

    public static Map<String, Double> rollup(List<Map<String, String>> data, String column) {
        Map<String, Double> result = new HashMap<>();
        for (Map<String, String> r : data) {
            String key = r.getOrDefault(column, "Unknown");
            double val = 0;
            try {
                val = Double.parseDouble(r.getOrDefault("Total Amount", "0"));
            } catch (NumberFormatException ignored) {}
            result.put(key, result.getOrDefault(key, 0.0) + val);
        }
        return result;
    }

    // Drill-down: automatically by Month (yyyy-MM) for Date column
    public static Map<String, Double> drillDownByMonth(List<Map<String, String>> data, String higherCol, String filterValue) {
        Map<String, Double> result = new TreeMap<>();
        for (Map<String, String> r : data) {
            if (r.getOrDefault(higherCol, "").equalsIgnoreCase(filterValue)) {
                String date = r.getOrDefault("Date", "");
                String month = "Unknown";
                if (date.length() >= 7) month = date.substring(0, 7); // yyyy-MM
                double val = 0;
                try { val = Double.parseDouble(r.getOrDefault("Total Amount", "0")); } catch (NumberFormatException ignored) {}
                result.put(month, result.getOrDefault(month, 0.0) + val);
            }
        }
        return result;
    }

    public static Map<String, Double> cube(List<Map<String, String>> data, List<String> dimensions) {
        Map<String, Double> result = new HashMap<>();
        int n = dimensions.size();
        for (Map<String, String> r : data) {
            for (int mask = 1; mask < (1 << n); mask++) {
                List<String> keys = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    if ((mask & (1 << i)) != 0) keys.add(r.getOrDefault(dimensions.get(i), "Unknown"));
                }
                String key = String.join(" | ", keys);
                double val = 0;
                try { val = Double.parseDouble(r.getOrDefault("Total Amount", "0")); } catch (NumberFormatException ignored) {}
                result.put(key, result.getOrDefault(key, 0.0) + val);
            }
        }
        return result;
    }
}
