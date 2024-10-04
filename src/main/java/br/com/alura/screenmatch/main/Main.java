package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.models.Episode;
import br.com.alura.screenmatch.models.SeasonData;
import br.com.alura.screenmatch.models.Series;
import br.com.alura.screenmatch.models.SeriesData;
import br.com.alura.screenmatch.repository.SeriesRepository;
import br.com.alura.screenmatch.services.ApiConsumption;
import br.com.alura.screenmatch.services.DataConverter;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private final Scanner reading = new Scanner(System.in);
    private final ApiConsumption apiConsumption = new ApiConsumption();
    private final DataConverter converter = new DataConverter();

    private final String BASE_URL = "http://www.omdbapi.com/?t=";
    private final String API_KEY_PARAM;

    private final SeriesRepository seriesRepository;

    private List<Series> seriesList = new ArrayList<>();

    public Main(Dotenv dotenv, SeriesRepository seriesRepository) {
        String API_KEY = dotenv.get("API_KEY");
        this.API_KEY_PARAM = "&apikey=" + API_KEY;

        this.seriesRepository = seriesRepository;
    }

    public void displayMenu() {
        var option = -1;

        while (option != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Buscar séries por ator
                    6 - Top 5 séries
                    
                    0 - Sair
                    """;

            System.out.println(menu);
            option = reading.nextInt();
            reading.nextLine();
                switch (option) {
                    case 1:
                        searchSeries();
                        break;
                    case 2:
                        searchEpisodesBySeries();
                        break;
                    case 3:
                        listSearchedSeries();
                        break;
                    case 4:
                        searchSeriesByTitle();
                        break;
                    case 5:
                        searchSeriesByActor();
                        break;
                    case 6:
                        searchTop5Series();
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválda");
                }
        }
    }

    private SeriesData getSeriesData() {
        System.out.println("Digite o nome da série para busca:");
        var seriesName = reading.nextLine();
        var json = apiConsumption.getData(
                BASE_URL
                        + seriesName.replace(" ", "+")
                        + API_KEY_PARAM);

        return converter.getData(json, SeriesData.class);
    }

    private void searchSeries() {
        SeriesData seriesData = getSeriesData();
        var series = new Series(seriesData);
        System.out.println(series);

        seriesRepository.save(series);
    }

    private void searchEpisodesBySeries() {
        listSearchedSeries();
        System.out.println("Digite o nome da série para busca:");
        var seriesName = reading.nextLine();

        Optional<Series> series = seriesList.stream()
                .filter(s -> s.getTitle().toLowerCase().contains(seriesName.toLowerCase()))
                .findFirst();

        if (series.isPresent()) {
            var foundSeries = series.get();
            List<SeasonData> seasons = new ArrayList<>();

            for (int i = 1; i <= foundSeries.getTotalSeasons(); i++) {
                var json = apiConsumption.getData(
                        BASE_URL
                                + foundSeries.getTitle().replace(" ", "+")
                                + "&season=" + i
                                + API_KEY_PARAM);

                SeasonData seasonData = converter.getData(json, SeasonData.class);
                seasons.add(seasonData);
            }

            seasons.forEach(System.out::println);

            List<Episode> episodes = seasons.stream()
                    .flatMap(s -> s.episodes().stream()
                            .map(e -> new Episode(s.number(), e)))
                    .collect(Collectors.toList());

            foundSeries.setEpisodes(episodes);
            seriesRepository.save(foundSeries);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listSearchedSeries() {
        seriesList = seriesRepository.findAll();
        seriesList.stream()
                .sorted(Comparator.comparing(Series::getTitle))
                .forEach(System.out::println);
    }

    private void searchSeriesByTitle() {
        System.out.println("Digite o nome da série para busca:");
        var seriesName = reading.nextLine();

        Optional<Series> searchedSeries = seriesRepository.findByTitleContainingIgnoreCase(seriesName);

        if (searchedSeries.isPresent()) {
            System.out.println("Dados da série: " + searchedSeries.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void searchSeriesByActor() {
        System.out.println("Qual o nome para a busca?");
        var actorName = reading.nextLine();
        System.out.println("Avaliações a partir de que valor?");
        var rating = reading.nextDouble();

        List<Series> foundedSeries = seriesRepository.findByActorsContainingIgnoreCaseAndRatingGreaterThanEqual(actorName, rating);

        System.out.println("Séries em que " + actorName +" trabalhou");
        foundedSeries.forEach(f ->
                System.out.println(f.getTitle() + ", Avaliação: " + f.getRating()));
    }

    private void searchTop5Series() {
        List<Series> topSeries = seriesRepository.findTop5ByOrderByRatingDesc();
        topSeries.forEach(s ->
                System.out.println(s.getTitle() + ", Avaliação: " + s.getRating()));
    }
}
