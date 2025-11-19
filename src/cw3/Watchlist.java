package cw3;

import java.util.ArrayList;

public class Watchlist {
    private final ArrayList<String> movieIds = new ArrayList<>();

    public boolean add(String movieId) {
        if (movieIds.contains(movieId)) return false;
        movieIds.add(movieId);
        return true;
    }

    public boolean remove(String movieId) {
        return movieIds.remove(movieId);
    }

    public boolean contains(String movieId) {
        return movieIds.contains(movieId);
    }

    public ArrayList<String> list() {
        return new ArrayList<>(movieIds);
    }
}