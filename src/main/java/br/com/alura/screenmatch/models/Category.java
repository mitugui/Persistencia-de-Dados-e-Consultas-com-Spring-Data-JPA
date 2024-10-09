package br.com.alura.screenmatch.models;

public enum Category {
    ACTION("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    COMEDY("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime");

    private final String omdbCategory;
    private final String omdbCategoryInPortuguese;

    Category(String omdbCategory, String omdbCategoryInPortuguese) {
        this.omdbCategory = omdbCategory;
        this.omdbCategoryInPortuguese = omdbCategoryInPortuguese;
    }

    public static Category fromString(String text) {
        for (Category category : Category.values()) {
            if (category.omdbCategory.equalsIgnoreCase(text)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }

    public static Category fromPortuguese(String text) {
        for (Category category : Category.values()) {
            if (category.omdbCategoryInPortuguese.equalsIgnoreCase(text)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
