package elements.stats;

public enum Counter {
    Energy, //didn't we impl it via _saved instead? Or is this different?
    Fatigue,
    Cadence,
    Stun,

    Blaze,
    Poison,
    Bleed,
    Doom,

    Rage,
    Focus,
    Glyph,
    Courage,
    Shield,
    Favor,

    Freeze,
    Ensnare,
    Haze,
    Fear,
    Lust,
    Corrosion,
    Blight,
    //+1?

    // Luck,
    ;

    public String mods() {
        return mods_(this);
    }

    private static String mods_(Counter counter) {
        return switch (counter){
            case Rage -> "Melee_Damage_Mod=1;";
            case Haze -> "Attack_Min=-1;Attack_Base=-1;Defense_Min=-1;Defense_Base=-1;";
            default -> "";
        };
    }
}
