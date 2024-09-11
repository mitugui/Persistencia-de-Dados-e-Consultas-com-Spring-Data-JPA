package br.com.alura.screenmatch;

import br.com.alura.screenmatch.exceptions.ApiKeyException;
import br.com.alura.screenmatch.main.Main;
import br.com.alura.screenmatch.repository.SeriesRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {
	@Autowired
	private SeriesRepository seriesRepository;

	private final Dotenv dotenv = Dotenv.load();

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			validateApiKey();
			startMain();
		} catch (ApiKeyException e) {
			System.out.print(e.getMessage());
			System.exit(1);
		}
	}

	private void validateApiKey() throws IOException {
		String API_KEY = dotenv.get("API_KEY");
		String API_KEY_TEST_URL = "https://www.omdbapi.com/?apikey=" + API_KEY;

		if (API_KEY == null || API_KEY.isEmpty()) {
			throw new ApiKeyException("Nenhuma API Key foi fornecida! Verifique o arquivo .env!");
		}

		HttpURLConnection connection = (HttpURLConnection) new URL(API_KEY_TEST_URL).openConnection();
		connection.setRequestMethod("GET");

		int responseCode = connection.getResponseCode();

		if (responseCode == 401) {
			throw new ApiKeyException("API Key Inválida! Verifique o arquivo .env!");
		}
	}

	private void startMain() {
		var main = new Main(dotenv, seriesRepository);
		main.displayMenu();
	}
}
