package cw3;

public final class CsvUtils {
    private CsvUtils() {}

    /**
     * 你的 movies.csv / users.csv 都是简单的逗号分隔、没有双引号，
     * 所以这里直接用 split(",") 就够了。
     */
    public static String[] parseCsvLine(String line) {
        if (line == null) return new String[0];
        return line.split(",", -1); // -1 保留空字段
    }

    public static String escape(String raw) {
        if (raw == null) return "";
        boolean needQuote = raw.indexOf(',') >= 0 || raw.indexOf('"') >= 0 || raw.indexOf('\n') >= 0;
        String s = raw.replace("\"", "\"\"");
        return needQuote ? '"' + s + '"' : s;
    }
}