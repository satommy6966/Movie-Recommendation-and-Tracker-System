package cw3;

import java.util.ArrayList;

public final class CsvUtils {
    private CsvUtils() {}

    /**
     * 简易CSV解析：支持用双引号包裹的字段与逗号分隔；不处理换行内嵌等高级场景。
     */
    public static String[] parseCsvLine(String line) {
        ArrayList<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                // 处理转义双引号 ""
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"'); i++; // 跳过第二个引号
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }

    public static String escape(String raw) {
        if (raw == null) return "";
        boolean needQuote = raw.indexOf(',') >= 0 || raw.indexOf('"') >= 0 || raw.indexOf('\n') >= 0;
        String s = raw.replace("\"", "\"\"");
        return needQuote ? '"' + s + '"' : s;
    }
}
