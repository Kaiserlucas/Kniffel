package kniffel.data;

public enum ScoreTableRowNames {

    Einser("Einser"),
    Zweier("Zweier"),
    Dreier("Dreier"),
    Vierer("Vierer"),
    Fuenfer("Fünfer"),
    Sechser("Sechser"),
    Bonus("Bonus"),
    Oben("Oben"),
    Dreierpasch("Dreierpasch"),
    Viererpasch("Viererpasch"),
    FullHouse("Full House"),
    KleineStrasse("Kleine Straße"),
    GrosseStrasse("Große Straße"),
    Kniffel("Kniffel"),
    Chance("Chance"),
    Unten("Unten"),
    Gesamt("Gesamt");

    public final String name;

    private ScoreTableRowNames(String name) {
        this.name = name;
    }
}
