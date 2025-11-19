package cw3;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 极简调试版 MovieLibrary：
 * - 不再依赖 CsvUtils
 * - 直接用 line.split(",") 解析
 * - 打印每一行和解析结果，方便看问题
 */
public class MovieLibrary {
    private final HashMap<String, Movie> byId = new HashMap<>();

    public void loadFromCsv(String path) throws IOException {
        byId.clear();
        File f = new File(path);
        System.out.println("[DEBUG] Loading movies from: " + f.getAbsolutePath());

        if (!f.exists()) {
            throw new FileNotFoundException("Movies CSV not found: " + path);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int lineNo = 0;

            while ((line = br.readLine()) != null) {
                lineNo++;
                // 打印原始行
                System.out.println("[DEBUG] raw line " + lineNo + ": " + line);

                // 第一行是表头，跳过
                if (lineNo == 1) {
                    System.out.println("[DEBUG] Header row: " + line);
                    continue;
                }

                if (line.trim().isEmpty()) {
                    System.out.println("[DEBUG] empty line, skip");
                    continue;
                }

                // 直接按逗号分割（你的文件就是标准逗号 CSV）
                String[] cols = line.split(",", -1); // -1 保留空字段
                System.out.println("[DEBUG] cols length = " + cols.length);

                if (cols.length < 5) {
                    System.out.println("[DEBUG] Bad movie row (cols<5), skipped: " + line);
                    continue;
                }

                String id    = cols[0].trim();
                String title = cols[1].trim();
                String genre = cols[2].trim();
                int year     = Validators.safeParseInt(cols[3], 0);
                double rating = Validators.safeParseDouble(cols[4], 0.0);

                if (id.isEmpty()) {
                    System.out.println("[DEBUG] Empty id, skipped: " + line);
                    continue;
                }

                Movie m = new Movie(id, title, genre, year, rating);
                byId.put(id, m);
            }
        }

        System.out.println("[DEBUG] Loaded movies count = " + byId.size());
    }

    public Movie getById(String id) {
        return byId.get(id);
    }

    public ArrayList<Movie> listAll() {
        return new ArrayList<>(byId.values());
    }
}