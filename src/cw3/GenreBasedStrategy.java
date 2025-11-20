package cw3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class GenreBasedStrategy {

    public ArrayList<Movie> recommend(User user, MovieLibrary lib, int topN) {
        // 1) 历史直方图
        HashMap<String, Integer> hist = user.getHistory().genreHistogram(lib);

        // 2) 生成候选：未看 & 不在watchlist
        HashSet<String> watched = new HashSet<String>();
        ArrayList<HistoryEntry> histList = user.getHistory().list();
        for (int i = 0; i < histList.size(); i++) {
            watched.add(histList.get(i).getMovieId());
        }

        HashSet<String> inWatch = new HashSet<String>(user.getWatchlist().list());

        ArrayList<Movie> candidates = new ArrayList<Movie>();
        ArrayList<Movie> all = lib.listAll();
        for (int i = 0; i < all.size(); i++) {
            Movie m = all.get(i);
            if (watched.contains(m.getId())) continue;
            if (inWatch.contains(m.getId())) continue;
            candidates.add(m);
        }

        // 3) 手写排序：按“喜欢的类型次数”降序，其次评分，其次年份
        sortByGenreScoreThenRatingThenYear(candidates, hist);

        // 4) 取 Top-N
        ArrayList<Movie> out = new ArrayList<Movie>();
        for (int i = 0; i < candidates.size() && i < topN; i++) {
            out.add(candidates.get(i));
        }
        return out;
    }


    private void sortByGenreScoreThenRatingThenYear(ArrayList<Movie> list, HashMap<String, Integer> hist) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int best = i;
            for (int j = i + 1; j < n; j++) {
                if (isBetter(list.get(j), list.get(best), hist)) {
                    best = j;
                }
            }
            if (best != i) {
                Movie tmp = list.get(i);
                list.set(i, list.get(best));
                list.set(best, tmp);
            }
        }
    }

    // 返回 a 是否应该排在 b 前面（true = a 更好）
    private boolean isBetter(Movie a, Movie b, HashMap<String, Integer> hist) {
        int ca = 0;
        int cb = 0;
        if (hist.containsKey(a.getGenre())) ca = hist.get(a.getGenre());
        if (hist.containsKey(b.getGenre())) cb = hist.get(b.getGenre());

        if (ca != cb) return ca > cb; // 类型出现次数多的优先

        if (a.getRating() != b.getRating()) {
            return a.getRating() > b.getRating(); // 高分优先
        }

        return a.getYear() > b.getYear(); // 年份新优先
    }
}