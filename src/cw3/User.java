package cw3;

public class User {
    private final String username;
    private String password; // 基础骨架：明文；可扩展为哈希
    private final Watchlist watchlist = new Watchlist();
    private final History history = new History();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public Watchlist getWatchlist() { return watchlist; }
    public History getHistory() { return history; }

    public boolean checkPassword(String raw) { return password != null && password.equals(raw); }
    public void setPassword(String newPwd) { this.password = newPwd; }
}