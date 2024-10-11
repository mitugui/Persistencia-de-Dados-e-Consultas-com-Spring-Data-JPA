package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.models.Category;
import br.com.alura.screenmatch.models.Episode;
import br.com.alura.screenmatch.models.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    Optional<Series> findByTitleContainingIgnoreCase(String seriesName);

    List<Series> findByActorsContainingIgnoreCaseAndRatingGreaterThanEqual(String actorName, Double rating);

    List<Series> findTop5ByOrderByRatingDesc();

    List<Series> findByGenre(Category genre);

    @Query("SELECT s FROM Series s WHERE s.totalSeasons <= :maximumSeasons AND s.rating >= :minimumRating")
    List<Series> seriesBySeasonAndRating(Integer maximumSeasons, Double minimumRating);

    @Query("SELECT e FROM Series s JOIN s.episodes e WHERE e.title ILIKE %:episodeExcerpt%")
    List<Episode> episodesByExcerpt(String episodeExcerpt);
}
