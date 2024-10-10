package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.models.Category;
import br.com.alura.screenmatch.models.Series;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    Optional<Series> findByTitleContainingIgnoreCase(String seriesName);

    List<Series> findByActorsContainingIgnoreCaseAndRatingGreaterThanEqual(String actorName, Double rating);

    List<Series> findTop5ByOrderByRatingDesc();

    List<Series> findByGenre(Category genre);

    List<Series> findByTotalSeasonsLessThanEqualAndRatingGreaterThanEqual(Integer maximumSeasons, Double rating);
}
