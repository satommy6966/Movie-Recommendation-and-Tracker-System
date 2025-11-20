package cw3;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * YearBasedStrategy：简单按上映年份从新到旧推荐。
 */
public class YearBasedStrategy {

    public ArrayList<Movie> recommend(User user, MovieLibrary lib, int topN) {
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

        sortByYearDesc(candidates);

        ArrayList<Movie> out = new ArrayList<Movie>();
        for (int i = 0; i < candidates.size() && i < topN; i++) {
            out.add(candidates.get(i));
        }
        return out;
    }

    private void sortByYearDesc(ArrayList<Movie> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int best = i;
            for (int j = i + 1; j < n; j++) {
                if (list.get(j).getYear() > list.get(best).getYear()) {
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
}