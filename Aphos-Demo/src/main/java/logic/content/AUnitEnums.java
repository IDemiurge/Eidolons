package logic.content;

public class AUnitEnums {
    public static final String HP = "HP";
    public static final String INITIATIVE = "INITIATIVE";
    public static final String ARMOR = "ARMOR";
    public static final String DAMAGE = "DAMAGE";
    public static final String DEFENSE = "DEFENSE";
    public static final String ATTACK = "ATTACK";

    public enum UnitVal{
        IMAGE,
        HP,
        INITIATIVE,
        ARMOR,
        DAMAGE,
        RANGED,
        AOE,
        ATTACK,
        DEFENSE
    }

    public enum UnitType{
        Melee, Ranged, Support, Explode, Bonus, Sneak
    }
}
