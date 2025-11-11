package cw3;

public class AuthService {
    private final UserRepository repo;

    public AuthService(UserRepository repo) {
        this.repo = repo;
    }

    public User login(String username, String password) {
        User u = repo.find(username);
        if (u != null && u.checkPassword(password)) return u;
        return null;
    }

    // 高级功能示例：注册/改密（可在 CLI 中接入）
    public boolean register(String username, String password) {
        if (username == null || username.trim().isEmpty()) return false;
        if (password == null || password.length() < 3) return false;
        User u = new User(username.trim(), password);
        return repo.add(u);
    }

    public boolean changePassword(User user, String oldPwd, String newPwd) {
        if (user == null) return false;
        if (!user.checkPassword(oldPwd)) return false;
        if (newPwd == null || newPwd.length() < 3) return false;
        user.setPassword(newPwd);
        return true;
    }
}