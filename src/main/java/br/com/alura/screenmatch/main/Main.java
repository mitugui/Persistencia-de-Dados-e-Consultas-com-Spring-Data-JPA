package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.models.Episode;
import br.com.alura.screenmatch.models.EpisodeData;
import br.com.alura.screenmatch.models.SeasonData;
import br.com.alura.screenmatch.models.SeriesData;
import br.com.alura.screenmatch.services.ApiConsumption;
import br.com.alura.screenmatch.services.DataConverter;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private Scanner reading = new Scanner(System.in);
    private ApiConsumption apiConsumption = new ApiConsumption();
    private DataConverter converter = new DataConverter();
    private final Dotenv dotenv = Dotenv.load();

    private final String BASE_URL = "http://www.omdbapi.com/?t=";
    private final String API_KEY = dotenv.get("API_KEY");
    private final String API_KEY_PARAM = "&apikey=" + API_KEY;

    public void displayMenu() {
        System.out.print("Digite o nome da série para buscá-la: ");
        var seriesName = reading.nextLine();
        var json =  apiConsumption.getData(
                BASE_URL +
                        seriesName.replace(" ", "+")
                        + API_KEY_PARAM);

        SeriesData seriesData = converter.getData(json, SeriesData.class);
//        System.out.println(seriesData);



		List<SeasonData> seasons = new ArrayList<>();
		for (int i = 1; i <= seriesData.totalSeasons(); i++) {
			json = apiConsumption.getData(
                    BASE_URL +
                            seriesName.replace(" ", "+")
                            + "&Season=" + i
                            + API_KEY_PARAM);

			SeasonData seasonData = converter.getData(json, SeasonData.class);
			seasons.add(seasonData);
		}

//		seasons.forEach(System.out::println);
//
//        seasons.forEach(s ->
//                s.episodes().forEach(e ->
//                        System.out.println(e.title())));



        List<EpisodeData> episodesData = seasons.stream()
                .flatMap(t -> t.episodes().stream())
                .collect(Collectors.toList());

//        System.out.println("\nTop 10 episódios\n");
//        episodesData.stream()
//                .filter(e -> !e.rating().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Filtro (N/A): " + e))
//                .sorted(Comparator.comparing(EpisodeData::rating).reversed())
//                .peek(e -> System.out.println("\nOrdenação: " + e))
//                .limit(10)
//                .peek(e -> System.out.println("Limite: " + e))
//                .map(e -> e.title().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento: " + e))
//                .forEach(System.out::println);



        List<Episode> episodes = seasons.stream()
                .flatMap(t -> t.episodes().stream()
                        .map(d -> new Episode(t.number(), d))
                ).collect(Collectors.toList());

        episodes.forEach(System.out::println);

//        System.out.println("Digite um trecho do titulo do episodio: ");
//        var titleSnippet = reading.nextLine();
//        Optional<Episode> searchedEpisode = episodes.stream()
//                .filter(e -> e.getTitle().toUpperCase().contains(titleSnippet.toUpperCase()))
//                .findFirst();
//
//        if (searchedEpisode.isPresent()) {
//            System.out.println("Episódio encontrado!");
//            System.out.println("Temporada: " + searchedEpisode.get().getSeason());
//        } else {
//            System.out.println("Episódio não encotrado!");
//        }

//        System.out.println("A partir de que ano você deseja ver os episódios?");
//        var year = reading.nextInt();
//        reading.nextLine();
//
//        LocalDate searchedDate = LocalDate.of(year, 1, 1);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodes.stream()
//                .filter(e -> e.getReleaseDate() != null && e.getReleaseDate().isAfter(searchedDate))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getReleaseDate() +
//                                " Episode: " + e.getTitle() +
//                                " Data lançamento: " + e.getReleaseDate().format(formatter)
//                ));

        Map<Integer, Double> ratingsBySeason = episodes.stream()
                .filter(e -> e.getRating() != null)
                .collect(Collectors.groupingBy(Episode::getSeason,
                        Collectors.averagingDouble(Episode::getRating)));
        System.out.println(ratingsBySeason);

        DoubleSummaryStatistics stats = episodes.stream()
                .filter(e -> e.getRating() != null)
                .collect(Collectors.summarizingDouble(Episode::getRating));
        System.out.println("Média: " + stats.getAverage());
        System.out.println("Melhor episódio: " + stats.getMax());
        System.out.println("Pior episódio: " + stats.getMin());
        System.out.println("Quantidade: " + stats.getCount());
    }
}