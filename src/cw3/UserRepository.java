/*
 * UserRepository 负责管理系统中的所有用户数据，并与 users.csv 进行读写交互。
 * 主要职责：
 *  1. 从 CSV 文件加载所有用户数据（loadFromCsv）。
 *  2. 将用户数据保存回 CSV 文件（saveToCsv）。
 *  3. 提供 find/add 等查询、修改用户的接口。
 *  4. 负责解析与序列化 Watchlist、History 数据（以分隔符形式存储）。
 *
 * UserRepository 不处理密码校验和登录逻辑，那些由 AuthService 负责。
 */

package cw3;

import java.io.*;
import java.util.*;

public class UserRepository {
    //使用Hasmap存储用户，key=username
    private final HashMap<String, User> users = new HashMap<>();

    /*
     * 从 CSV 文件加载所有用户。格式：
     * Username, Password, Watchlist, History
     * Watchlist 示例： "M001;M002"
     * History 示例：   "M001@2025-07-12;M011@2025-08-10"
     */
    public void loadFromCsv(String path) throws IOException {
        users.clear();
        File f = new File(path);
        if (!f.exists()) throw new FileNotFoundException("Users CSV not found: " + path);
        try (BufferedReader br = new BufferedReader(new FileReader(f)))
        {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null)
            {
                if (line.trim().isEmpty()) continue;

                if (!headerSkipped)
                {
                    headerSkipped = true;
                    continue;
                }
                String[] cols = CsvUtils.parseCsvLine(line);

                if (cols.length < 4) continue;
                
                String username = cols[0];
                String password = cols[1];
                String watchlistStr = cols[2];
                String historyStr = cols[3];
                User u = new User(username, password);
                // 解析 Watchlist
                if (watchlistStr != null && !watchlistStr.trim().isEmpty())
                {
                    String[] ids = watchlistStr.split(AppConfig.DELIM_LIST);//watchliststr是由多个电影id组成的字符串，由分隔符";"+ AppConfig.DELIM_LIST组成，ids中存放watchlist中的电影id
                    for (String s : ids)
                    {
                        String id = s.trim();//用安全解析 safeParseInt 转成 int，失败时给 -1
                        if (!id.isEmpty()) u.getWatchlist().add(id);//合法的 id（>=0）加入用户 watchlist
                    }
                }
                // 解析 History
                if (historyStr != null && !historyStr.trim().isEmpty())
                {
                    String[] pairs = historyStr.split(AppConfig.DELIM_LIST);//分隔每条历史记录
                    for (String p : pairs)
                    {
                        String[] kv = p.split(AppConfig.DELIM_PAIR);//分隔电影id和时间戳
                        if (kv.length == 2)
                        {
                            String movieId = kv[0].trim();
                            long ts = Validators.safeParseLong(kv[1].trim(), 0L);
                            if (!movieId.isEmpty()) u.getHistory().add(movieId, ts);
                        }
                    }
                }
                users.put(username, u);
            }
        }
    }

    public void saveToCsv(String path) throws IOException
    {

        File f = new File(path);
        File parent = f.getParentFile();//讲用户的csv文件读进来
        if (parent != null && !parent.exists()) parent.mkdirs();//如果父目录不为空并且该目录为空，就调用mkdirs递归创建目录

        /*
        new FileWriter(f)：创建一个字符输出流，往文件 f 里写
        外面套一层 new BufferedWriter(...)：增加缓冲区，减少磁盘 IO 次数，效率更高
        最外面 new PrintWriter(...)：提供 println() 等更方便的按行输出方法
        try (...) { ... } 的语法告诉 JVM：pw 在 try 块结束时自动关闭（调用 close()），不用你手动写 finally 去关流，防止资源泄露
         */
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f))))
        {
            pw.println("Username,Password,Watchlist,History");

            for (User u : users.values())
            {
                //获取当前一行的watchlist和history
                String wl = joinList(u.getWatchlist().list());
                String hist = joinHistory(u.getHistory().list());

                String[] cols = new String[]
                        {
                        //转义csv特殊字符
                        CsvUtils.escape(u.getUsername()),
                        CsvUtils.escape(u.getPassword()),
                        CsvUtils.escape(wl),
                        CsvUtils.escape(hist)
                };
                pw.println(String.join(",", cols));
            }
        }
    }

    // watchlist: "M001;M002;M003"
    private String joinList(ArrayList<String> ids) {
        String result = "";
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                result = result + AppConfig.DELIM_LIST;
            }
            result = result + ids.get(i);
        }
        return result;
    }

    // history: "M001@12345;M002@67890"
    private String joinHistory(ArrayList<HistoryEntry> entries) {
        String result = "";
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) {
                result = result + AppConfig.DELIM_LIST;
            }
            HistoryEntry e = entries.get(i);
            result = result + e.getMovieId() + AppConfig.DELIM_PAIR + e.getWatchedAtEpochMillis();
        }
        return result;
    }

    public User find(String username)
    {
        return users.get(username);
    }

    public boolean add(User user)
    {
        if (users.containsKey(user.getUsername())) return false;
        users.put(user.getUsername(), user);
        return true;
    }

    public Collection<User> listAll()
    {
        return users.values();
    }
}