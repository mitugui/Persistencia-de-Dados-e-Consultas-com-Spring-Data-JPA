package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.models.SeasonData;
import br.com.alura.screenmatch.models.Series;
import br.com.alura.screenmatch.models.SeriesData;
import br.com.alura.screenmatch.services.ApiConsumption;
import br.com.alura.screenmatch.services.DataConverter;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private final Scanner reading = new Scanner(System.in);
    private final ApiConsumption apiConsumption = new ApiConsumption();
    private final DataConverter converter = new DataConverter();

    private final String BASE_URL = "http://www.omdbapi.com/?t=";
    private final String API_KEY_PARAM;

    private final List<SeriesData> seriesDataList = new ArrayList<>();

    public Main(Dotenv dotenv) {
        String API_KEY = dotenv.get("API_KEY");
        this.API_KEY_PARAM = "&apikey=" + API_KEY;
    }

    public void displayMenu() {
        var option = -1;

        while (option != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    
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

        this.seriesDataList.add(seriesData);
    }

    private void searchEpisodesBySeries() {
        SeriesData seriesData = getSeriesData();
        List<SeasonData> seasons = new ArrayList<>();

        for (int i = 1; i <= seriesData.totalSeasons(); i++) {
            var json = apiConsumption.getData(
                    BASE_URL
                            + seriesData.title().replace(" ", "+")
                            + "&season=" + i
                            + API_KEY_PARAM);

            SeasonData seasonData = converter.getData(json, SeasonData.class);
            seasons.add(seasonData);
        }

        seasons.forEach(System.out::println);
    }

    private void listSearchedSeries() {
        List<Series> series;

        series = seriesDataList.stream()
                .map(d -> new Series(d))
                        .collect(Collectors.toList());

        series.stream()
                .sorted(Comparator.comparing(Series::getGenre))
                .forEach(System.out::println);
    }
}
