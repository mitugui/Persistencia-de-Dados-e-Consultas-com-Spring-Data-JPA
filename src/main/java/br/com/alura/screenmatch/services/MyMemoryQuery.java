package br.com.alura.screenmatch.services;

import br.com.alura.screenmatch.exceptions.TranslationException;
import br.com.alura.screenmatch.models.Language;
import br.com.alura.screenmatch.models.MyMemoryResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MyMemoryQuery {
    public static String getTranslation(String textToTranslate) {
        var apiConsumption = new ApiConsumption();

        var dataConverter = new DataConverter();

        var json = apiConsumption.getData(
                MyMemoryQuery.generateMyMemoryURL(textToTranslate, Language.ENGLISH, Language.BRAZILIAN_PORTUGUESE));

        MyMemoryResponse response;
        try {
            response = dataConverter.getData(json, MyMemoryResponse.class);
        } catch (RuntimeException e) {
            throw new TranslationException("Erro ao processar a resposta JSON da tradução", e);
        }

        return response.responseData().translatedText();
    }

    private static String generateMyMemoryURL(String textToTranslate, Language sourceLanguage, Language targetLanguage) {
        String BASE_URL = "https://api.mymemory.translated.net/get?q=";
        String text = URLEncoder.encode(textToTranslate, StandardCharsets.UTF_8);
        String langpair = URLEncoder.encode(
                sourceLanguage.languageAcronym + "|" + targetLanguage.languageAcronym,
                StandardCharsets.UTF_8);

        return BASE_URL + text + "&langpair=" + langpair;
    }
}
