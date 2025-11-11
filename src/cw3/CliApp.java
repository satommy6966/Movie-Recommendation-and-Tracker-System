package cw3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CliApp {
    private final MovieLibrary lib = new MovieLibrary();
    private final UserRepository users = new UserRepository();
    private final RecommendationEngine engine = new RecommendationEngine();
    private final AuthService auth = new AuthService(users);
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        // 加载数据
        try {
            lib.loadFromCsv(AppConfig.MOVIES_CSV);
            users.loadFromCsv(AppConfig.USERS_CSV);
        } catch (IOException e) {
            System.out.println("[FATAL] Failed to load CSV files: " + e.getMessage());
            return;
        }

        User current = null;
        while (true) {
            if (current == null) {
                showMenuLoggedOut();
                int opt = Validators.requireIntInRange(scanner, 0, 2);
                if (opt == 0) { System.out.println("Bye."); break; }
                if (opt == 1) { current = doLogin(); }
                if (opt == 2) { doRegister(); }
            } else {
                showMenuLoggedIn(current);
                int opt = Validators.requireIntInRange(scanner, 0, 8);
                switch (opt) {
                    case 0: System.out.println("Bye."); persistAndExit(); return;
                    case 1: doBrowse(); break;
                    case 2: doAddToWatchlist(current); break;
                    case 3: doRemoveFromWatchlist(current); break;
                    case 4: doViewWatchlist(current); break;
                    case 5: doMarkWatched(current); break;
                    case 6: doViewHistory(current); break;
                    case 7: doRecommend(current); break;
                    case 8: current = null; System.out.println("Logged out."); break;
                    default: System.out.println("Unknown option.");
                }
            }
        }
    }

    private void showMenuLoggedOut() {
        System.out.println("\n=== Main Menu (Logged Out) ===");
        System.out.println("1) Login");
        System.out.println("2) Register (Advanced)");
        System.out.println("0) Exit");
        System.out.print("Enter option: ");
    }

    private void showMenuLoggedIn(User u) {
        System.out.println("\n=== Main Menu (User: " + u.getUsername() + ") ===");
        System.out.println("1) Browse movies");
        System.out.println("2) Add movie to watchlist");
        System.out.println("3) Remove movie from watchlist");
        System.out.println("4) View watchlist");
        System.out.println("5) Mark movie as watched");
        System.out.println("6) View history");
        System.out.println("7) Get recommendations (Top-" + AppConfig.DEFAULT_TOP_N + ")");
        System.out.println("8) Logout");
        System.out.println("0) Exit");
        System.out.print("Enter option: ");
    }

    private User doLogin() {
        System.out.print("Username: ");
        String u = scanner.nextLine();
        System.out.print("Password: ");
        String p = scanner.nextLine();
        User user = auth.login(u, p);
        if (user == null) System.out.println("Login failed.");
        else System.out.println("Welcome, " + user.getUsername() + "!");
        return user;
    }

    private void doRegister() {
        System.out.print("New username: ");
        String u = scanner.nextLine();
        System.out.print("New password: ");
        String p = scanner.nextLine();
        boolean ok = auth.register(u, p);
        System.out.println(ok ? "Registered." : "Register failed (maybe user exists?).");
    }

    private void doBrowse() {
        System.out.println("\n-- All Movies --");
        for (Movie m : lib.listAll()) System.out.println("  " + m);
    }

    private void doAddToWatchlist(User user) {
        System.out.print("Enter movie ID to add: ");
        int id = Validators.safeParseInt(scanner.nextLine(), -1);
        if (id < 0 || lib.getById(id) == null) { System.out.println("Invalid movie ID."); return; }
        boolean ok = user.getWatchlist().add(id);
        System.out.println(ok ? "Added." : "Already in watchlist.");
    }

    private void doRemoveFromWatchlist(User user) {
        System.out.print("Enter movie ID to remove: ");
        int id = Validators.safeParseInt(scanner.nextLine(), -1);
        boolean ok = user.getWatchlist().remove(id);
        System.out.println(ok ? "Removed." : "Not in watchlist.");
    }

    private void doViewWatchlist(User user) {
        System.out.println("\n-- Watchlist --");
        for (int id : user.getWatchlist().list()) {
            Movie m = lib.getById(id);
            System.out.println("  " + (m == null ? ("#" + id) : m.toString()));
        }
    }

    private void doMarkWatched(User user) {
        System.out.print("Enter movie ID watched: ");
        int id = Validators.safeParseInt(scanner.nextLine(), -1);
        if (id < 0 || lib.getById(id) == null) { System.out.println("Invalid movie ID."); return; }
        user.getHistory().add(id, System.currentTimeMillis());
        if (user.getWatchlist().contains(id)) user.getWatchlist().remove(id);
        System.out.println("Marked watched.");
    }

    private void doViewHistory(User user) {
        System.out.println("\n-- History --");
        for (HistoryEntry e : user.getHistory().list()) {
            Movie m = lib.getById(e.getMovieId());
            System.out.println("  " + (m == null ? ("#" + e.getMovieId()) : m.toString()));
        }
    }

    private void doRecommend(User user) {
        // 默认使用 Genre 策略，可扩展成可切换
        engine.setStrategy(new GenreBasedStrategy());
        ArrayList<Movie> rec = engine.recommend(user, lib, AppConfig.DEFAULT_TOP_N);
        System.out.println("\n-- Recommendations --");
        int i = 1;
        for (Movie m : rec) System.out.println("  " + (i++) + ". " + m);
    }

    private void persistAndExit() {
        try {
            users.saveToCsv(AppConfig.USERS_CSV);
        } catch (Exception e) {
            System.out.println("[WARN] Failed to save users: " + e.getMessage());
        }
        System.out.println("Saved. Bye.");
    }
}
