package cw3;

import java.util.ArrayList;

public class Watchlist {
    private final ArrayList<Integer> movieIds = new ArrayList<>();

    public boolean add(int movieId) {
        if (movieIds.contains(movieId)) return false;
        movieIds.add(movieId);
        return true;
    }

    public boolean remove(int movieId) {
        return movieIds.remove((Integer) movieId);
    }

    public boolean contains(int movieId) {
        return movieIds.contains(movieId);
    }

    public ArrayList<Integer> list() {
        return new ArrayList<>(movieIds);
    }
}