package br.com.alura.screenmatch.models;

public enum Language {
    BRAZILIAN_PORTUGUESE("pt-br"),
    ENGLISH ("en");

    public final String languageAcronym;

    Language(String languageAcronym) {
        this.languageAcronym = languageAcronym;
    }
}
