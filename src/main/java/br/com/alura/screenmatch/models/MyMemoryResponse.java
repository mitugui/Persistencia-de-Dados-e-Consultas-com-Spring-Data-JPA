package br.com.alura.screenmatch.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MyMemoryResponse(@JsonAlias("responseData") ResponseData responseData) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ResponseData(@JsonAlias("translatedText") String translatedText) {}
}
