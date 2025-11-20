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

    public void run()
    {
        // 加载数据
        try
        {
            lib.loadFromCsv(AppConfig.MOVIES_CSV);
            users.loadFromCsv(AppConfig.USERS_CSV);
        } catch (IOException e)
        {
            System.out.println("[FATAL] Failed to load CSV files: " + e.getMessage());
            return;
        }

        User current = null;
        while (true)
        {
            if (current == null)
            {
                showMenuLoggedOut();
                int opt = Validators.requireIntInRange(scanner, 0, 2);
                if (opt == 0) { System.out.println("Bye."); break; }
                if (opt == 1) { current = doLogin(); }
                if (opt == 2) { doRegister(); }
            }
            else
            {
                showMenuLoggedIn(current);
                int opt = Validators.requireIntInRange(scanner, 0, 10);
                switch (opt)
                {
                    case 0: System.out.println("Bye."); persistAndExit(); return;
                    case 1: doBrowse(); break;
                    case 2: doSearchMovies() ; break;
                    case 3: doAddToWatchlist(current); break;
                    case 4: doRemoveFromWatchlist(current); break;
                    case 5: doViewWatchlist(current); break;
                    case 6: doMarkWatched(current); break;
                    case 7: doViewHistory(current); break;
                    case 8: doRecommend(current); break;
                    case 9: doChangeStrategy(); break;
                    case 10: current = null; System.out.println("Logged out."); break;
                    default: System.out.println("Unknown option.");
                }
            }
        }
    }

    private void showMenuLoggedOut()
    {
        System.out.println("\n=== Main Menu (Logged Out) ===");
        System.out.println("1) Login");
        System.out.println("2) Register (Advanced)");
        System.out.println("0) Exit");
        System.out.print("Enter option: ");
    }

    private void showMenuLoggedIn(User u)
    {
        System.out.println("\n=== Main Menu (User: " + u.getUsername() + ") ===");
        System.out.println("1) Browse movies");
        System.out.println("2) Search movies");
        System.out.println("3) Add movie to watchlist");
        System.out.println("4) Remove movie from watchlist");
        System.out.println("5) View watchlist");
        System.out.println("6) Mark movie as watched");
        System.out.println("7) View history");
        System.out.println("8) Get recommendations (Top-" + AppConfig.DEFAULT_TOP_N + ")");
        System.out.println("9) Change recommendation strategy");
        System.out.println("10) Logout");
        System.out.println("0) Exit");
        System.out.print("Enter option: ");
    }

    private User doLogin()
    {
        System.out.print("Username: ");
        String u = scanner.nextLine();
        System.out.print("Password: ");
        String p = scanner.nextLine();
        User user = auth.login(u, p);
        if (user == null) System.out.println("Login failed.");
        else System.out.println("Welcome, " + user.getUsername() + "!");
        return user;
    }

    private void doRegister()
    {
        System.out.print("New username: ");
        String u = scanner.nextLine();
        System.out.print("New password: ");
        String p = scanner.nextLine();
        boolean ok = auth.register(u, p);
        System.out.println(ok ? "Registered." : "Register failed (maybe user exists?).");
    }

    private void doBrowse() {
        System.out.println("\n-- All Movies --");
        System.out.println("[DEBUG] movies count = " + lib.listAll().size());
        for (Movie m : lib.listAll()) {
            System.out.println("  " + m);
        }
    }

    private void doSearchMovies()
    {
        System.out.print("Keyword in title (empty to skip): ");
        String kw = scanner.nextLine().trim().toLowerCase();

        System.out.print("Genre (empty to skip): ");
        String genre = scanner.nextLine().trim().toLowerCase();

        System.out.print("Min year (empty to skip): ");
        String minYearStr = scanner.nextLine().trim();
        System.out.print("Max year (empty to skip): ");
        String maxYearStr = scanner.nextLine().trim();

        int minYear = minYearStr.isEmpty() ? Integer.MIN_VALUE : Validators.safeParseInt(minYearStr, Integer.MIN_VALUE);
        int maxYear = maxYearStr.isEmpty() ? Integer.MAX_VALUE : Validators.safeParseInt(maxYearStr, Integer.MAX_VALUE);

        System.out.println("\n-- Search Result --");
        for (Movie m : lib.listAll()) {
            if (!kw.isEmpty() && !m.getTitle().toLowerCase().contains(kw)) continue;
            if (!genre.isEmpty() && !m.getGenre().toLowerCase().contains(genre)) continue;
            if (m.getYear() < minYear || m.getYear() > maxYear) continue;
            System.out.println("  " + m);
        }
    }

    private void doAddToWatchlist(User user)
    {
        System.out.print("Enter movie ID to add: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty() || lib.getById(id) == null) { System.out.println("Invalid movie ID."); return; }
        boolean ok = user.getWatchlist().add(id);
        System.out.println(ok ? "Added." : "Already in watchlist.");
    }

    private void doRemoveFromWatchlist(User user) {
        System.out.print("Enter movie ID to remove: ");
        String id = scanner.nextLine().trim();
        boolean ok = user.getWatchlist().remove(id);
        System.out.println(ok ? "Removed." : "Not in watchlist.");
    }

    private void doViewWatchlist(User user) {
        System.out.println("\n-- Watchlist --");
        for (String id : user.getWatchlist().list()) {
            Movie m = lib.getById(id);
            System.out.println("  " + (m == null ? ("#" + id) : m.toString()));
        }
    }

    private void doMarkWatched(User user) {
        System.out.print("Enter movie ID watched: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty() || lib.getById(id) == null) { System.out.println("Invalid movie ID."); return; }
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
        ArrayList<Movie> rec = engine.recommend(user, lib, AppConfig.DEFAULT_TOP_N);
        System.out.println("\n-- Recommendations --");
        int i = 1;
        for (Movie m : rec) {
            System.out.println("  " + (i++) + ". " + m);
        }
    }

    private void doChangeStrategy() {
        System.out.println("\nChoose recommendation strategy:");
        System.out.println("1) Genre-based (default)");
        System.out.println("2) Rating-based");
        System.out.println("3) Year-based");
        System.out.print("Enter option: ");

        int opt = Validators.safeParseInt(scanner.nextLine(), -1);

        if (opt == 1) {
            engine.setStrategyType(RecommendationEngine.STRATEGY_GENRE);
            System.out.println("Strategy set to Genre-based.");
        } else if (opt == 2) {
            engine.setStrategyType(RecommendationEngine.STRATEGY_RATING);
            System.out.println("Strategy set to Rating-based.");
        } else if (opt == 3) {
            engine.setStrategyType(RecommendationEngine.STRATEGY_YEAR);
            System.out.println("Strategy set to Year-based.");
        } else {
            System.out.println("Invalid option.");
        }
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
