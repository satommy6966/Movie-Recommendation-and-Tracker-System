package cw3;

public class Movie {
    private final int id;
    private final String title;
    private final String genre;
    private final int year;
    private final double rating; // 0.0 ~ 10.0

    public Movie(int id, String title, String genre, int year, double rating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.rating = rating;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getYear() { return year; }
    public double getRating() { return rating; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s, %d, %.1f)", id, title, genre, year, rating);
    }
}
