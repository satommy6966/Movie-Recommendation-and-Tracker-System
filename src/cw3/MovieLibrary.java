package cw3;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MovieLibrary {
    private final HashMap<Integer, Movie> byId = new HashMap<>();

    public void loadFromCsv(String path) throws IOException {
        byId.clear();
        File f = new File(path);
        if (!f.exists()) throw new FileNotFoundException("Movies CSV not found: " + path);
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) { headerSkipped = true; continue; }
                String[] cols = CsvUtils.parseCsvLine(line);
                if (cols.length < 5) continue; // 跳过坏行
                int id = Validators.safeParseInt(cols[0], -1);
                String title = cols[1];
                String genre = cols[2];
                int year = Validators.safeParseInt(cols[3], 0);
                double rating = Validators.safeParseDouble(cols[4], 0.0);
                if (id >= 0) {
                    byId.put(id, new Movie(id, title, genre, year, rating));
                }
            }
        }
    }

    public Movie getById(int id) { return byId.get(id); }

    public ArrayList<Movie> listAll() {
        return new ArrayList<>(byId.values());
    }
}