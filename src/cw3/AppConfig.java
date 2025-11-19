package cw3;

public class AppConfig {
    private AppConfig() {}
    public static final String MOVIES_CSV = "data/movies.csv";   // 确保运行时存在
    public static final String USERS_CSV  = "data/users.csv";    // 确保运行时存在

    public static final String DELIM_LIST = ";";      // Watchlist与History列表分隔符
    public static final String DELIM_PAIR = "@";      // History中的 movieId@timestamp 分隔符

    public static final int DEFAULT_TOP_N = 5;         // 推荐数量缺省值
}
