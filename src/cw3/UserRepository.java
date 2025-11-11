package cw3;

import java.io.*;
import java.util.*;

public class UserRepository {
    private final HashMap<String, User> users = new HashMap<>();

    public void loadFromCsv(String path) throws IOException {
        users.clear();
        File f = new File(path);
        if (!f.exists()) throw new FileNotFoundException("Users CSV not found: " + path);
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line; boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) { headerSkipped = true; continue; }
                String[] cols = CsvUtils.parseCsvLine(line);
                if (cols.length < 4) continue;
                String username = cols[0];
                String password = cols[1];
                String watchlistStr = cols[2];
                String historyStr = cols[3];
                User u = new User(username, password);
                // 解析 Watchlist
                if (watchlistStr != null && !watchlistStr.trim().isEmpty()) {
                    String[] ids = watchlistStr.split("\\" + AppConfig.DELIM_LIST);
                    for (String s : ids) {
                        int id = Validators.safeParseInt(s.trim(), -1);
                        if (id >= 0) u.getWatchlist().add(id);
                    }
                }
                // 解析 History
                if (historyStr != null && !historyStr.trim().isEmpty()) {
                    String[] pairs = historyStr.split("\\" + AppConfig.DELIM_LIST);
                    for (String p : pairs) {
                        String[] kv = p.split("\\" + AppConfig.DELIM_PAIR);
                        if (kv.length == 2) {
                            int movieId = Validators.safeParseInt(kv[0].trim(), -1);
                            long ts = Validators.safeParseLong(kv[1].trim(), 0L);
                            if (movieId >= 0) u.getHistory().add(movieId, ts);
                        }
                    }
                }
                users.put(username, u);
            }
        }
    }

    public void saveToCsv(String path) throws IOException {
        // 简单写回（可改进为临时文件+原子替换）
        File f = new File(path);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)))) {
            pw.println("Username,Password,Watchlist,History");
            for (User u : users.values()) {
                String wl = joinList(u.getWatchlist().list());
                String hist = joinHistory(u.getHistory().list());
                String[] cols = new String[] {
                        CsvUtils.escape(u.getUsername()),
                        CsvUtils.escape(""), // 出于安全：不直接写出密码；实验阶段可改为 u 的真实字段
                        CsvUtils.escape(wl),
                        CsvUtils.escape(hist)
                };
                pw.println(String.join(",", cols));
            }
        }
    }

    private String joinList(ArrayList<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(AppConfig.DELIM_LIST);
            sb.append(ids.get(i));
        }
        return sb.toString();
    }

    private String joinHistory(ArrayList<HistoryEntry> entries) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) sb.append(AppConfig.DELIM_LIST);
            HistoryEntry e = entries.get(i);
            sb.append(e.getMovieId()).append(AppConfig.DELIM_PAIR).append(e.getWatchedAtEpochMillis());
        }
        return sb.toString();
    }

    public User find(String username) { return users.get(username); }

    public boolean add(User user) {
        if (users.containsKey(user.getUsername())) return false;
        users.put(user.getUsername(), user);
        return true;
    }

    public Collection<User> listAll() { return users.values(); }
}