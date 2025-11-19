package cw3;

import java.util.*;

public class GenreBasedStrategy implements RecommendationStrategy
{
    @Override
    public ArrayList<Movie> recommend(User user, MovieLibrary lib, int topN)
    {
        // 1) 历史直方图
        HashMap<String, Integer> hist = user.getHistory().genreHistogram(lib);
        // 2) 生成候选：未看 & 不在watchlist
        HashSet<String> watched = new HashSet<>();

        for (HistoryEntry e : user.getHistory().list()) watched.add(e.getMovieId());

        HashSet<String> inWatch = new HashSet<>(user.getWatchlist().list());

        ArrayList<Movie> candidates = new ArrayList<>();
        for (Movie m : lib.listAll()) {
            if (watched.contains(m.getId())) continue;
            if (inWatch.contains(m.getId())) continue;
            candidates.add(m);
        }
        // 3) 简单打分：喜欢的类型优先 + 评分微调
        final HashMap<String,Integer> histFinal = hist;
        candidates.sort((a, b) -> {
            int ca = histFinal.getOrDefault(a.getGenre(), 0);
            int cb = histFinal.getOrDefault(b.getGenre(), 0);
            if (ca != cb) return Integer.compare(cb, ca); // 高频在前
            if (Double.compare(a.getRating(), b.getRating()) != 0) {
                return Double.compare(b.getRating(), a.getRating()); // 高分在前
            }
            return Integer.compare(b.getYear(), a.getYear()); // 新一些在前
        });
        // 4) 取 Top-N
        ArrayList<Movie> out = new ArrayList<>();
        for (int i = 0; i < candidates.size() && i < topN; i++) out.add(candidates.get(i));
        return out;
    }
}