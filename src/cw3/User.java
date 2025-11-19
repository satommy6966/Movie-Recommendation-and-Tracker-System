/*
 * 用户类（User）用于表示系统中的一个用户实体。
 * 一个 User 拥有：
 *  - 用户名
 *  - 密码（当前为明文，可扩展为哈希）
 *  - 个人观影清单（Watchlist）
 *  - 观看历史（History）
 *
 * 该类主要提供基本数据封装，不直接处理文件读写和登录逻辑，
 * 文件相关操作由 UserRepository 负责，认证逻辑由 AuthService 负责。
 */

package cw3;

public class User {
    //用户的构造函数，包括用户名，密码，片单，观看历史
    //User's constructer, include user name, password, watchlist and histroy.
    private final String username;
    private String password; // 基础骨架：明文；可扩展为哈希
    private final Watchlist watchlist = new Watchlist();
    private final History history = new History();

    public User(String username, String password) {
        //构造函数重载
        //constructer overload
        this.username = username;
        this.password = password;
    }

    //getters
    public String getUsername() { return username; }
    public Watchlist getWatchlist() { return watchlist; }
    public History getHistory() { return history; }
    public String getPassword() { return password; }

    //判断输入的密码是否和存储的密码相同
    public boolean checkPassword(String raw)
    {
        return password != null && password.equals(raw);
    }
    //设置新密码
    public void setPassword(String newPwd)
    {
        this.password = newPwd;
    }

}