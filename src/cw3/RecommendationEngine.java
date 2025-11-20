package cw3;

import java.util.ArrayList;


public class RecommendationEngine {

    public static final int STRATEGY_GENRE  = 1;
    public static final int STRATEGY_RATING = 2;
    public static final int STRATEGY_YEAR   = 3;

    // 当前策略类型，默认 1（Genre）
    private int strategyType = STRATEGY_GENRE;

    public void setStrategyType(int type) {
        if (type == STRATEGY_GENRE || type == STRATEGY_RATING || type == STRATEGY_YEAR) {
            this.strategyType = type;
        }
    }

    public int getStrategyType() {
        return strategyType;
    }

    public ArrayList<Movie> recommend(User u, MovieLibrary lib, int topN) {
        if (topN <= 0) topN = AppConfig.DEFAULT_TOP_N;

        if (strategyType == STRATEGY_RATING) {
            RatingBasedStrategy s = new RatingBasedStrategy();
            return s.recommend(u, lib, topN);
        } else if (strategyType == STRATEGY_YEAR) {
            YearBasedStrategy s = new YearBasedStrategy();
            return s.recommend(u, lib, topN);
        } else {
            // 默认使用 Genre 策略
            GenreBasedStrategy s = new GenreBasedStrategy();
            return s.recommend(u, lib, topN);
        }
    }
}