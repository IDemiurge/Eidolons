package logic.content;

public class AUnitEnums {
    public static final String NAME = "NAME";
    public static final String IMAGE = "IMAGE";

    public static final String HP = "HP";
    public static final String INITIATIVE = "INITIATIVE";
    public static final String ARMOR = "ARMOR";
    public static final String DAMAGE = "DAMAGE";
    public static final String DEFENSE = "DEFENSE";
    public static final String ATTACK = "ATTACK";
    public static final String BODY = "BODY";

    public enum Body{
        stone, bone, dust, blood, metal
    }
    public enum UnitVal{
        IMAGE,
        HP,
        INITIATIVE,
        ARMOR,
        DAMAGE,
        RANGED,
        AOE,
        ATTACK,
        BODY,
        DEFENSE
    }

    public enum UnitType{
        Melee, Guard, Ranged, Caster, Sneak, Explode, Multiclass, Bonus, Boss
    }
}
