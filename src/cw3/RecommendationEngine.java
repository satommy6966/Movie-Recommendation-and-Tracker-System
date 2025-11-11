package cw3;

import java.util.ArrayList;

public class RecommendationEngine {
    private RecommendationStrategy strategy;

    public void setStrategy(RecommendationStrategy s) { this.strategy = s; }

    public ArrayList<Movie> recommend(User u, MovieLibrary lib, int topN) {
        if (strategy == null) strategy = new GenreBasedStrategy();
        if (topN <= 0) topN = AppConfig.DEFAULT_TOP_N;
        return strategy.recommend(u, lib, topN);
    }
}