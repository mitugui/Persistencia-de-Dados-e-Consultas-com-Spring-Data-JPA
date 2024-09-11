package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.models.Series;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeriesRepository extends JpaRepository<Series, Long> {}
