package cw3;

import java.util.ArrayList;

public interface RecommendationStrategy {
    ArrayList<Movie> recommend(User user, MovieLibrary lib, int topN);
}
