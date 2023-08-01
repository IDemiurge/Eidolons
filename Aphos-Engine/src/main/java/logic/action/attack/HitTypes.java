package logic.action.attack;

/**
 * Created by Alexander on 6/10/2023
 *
 * When a unit targets another unit with attack or spell, their respective Offense (Accuracy or Spellpower) and Defense (Defense or Resistance) values are compared to determine what Hit Type will result.
 * If the lesser of 2 compared values is below 4, the difference must be 3 or greater to result in a Critical or a Miss. Else, the difference must be 2x.
 * E.g. for Defense of 2, Attack equal to 5 is needed to deal a Critical Hit, while for Resistance of 8, Spellpower 4 or lower will be a Miss.
 * For other cases, if Offense value is less than Defense, the Hit Type is Graze
 * If it is equal or greater, it is a normal Hit.
 */
public class HitTypes {
    private static final int ARITHMETIC_THRESHOLD = 4;
    private static final int ARITHMETIC_DIFFERENCE = 3;

    public enum HitType{
        Critical_Hit,
        Hit,
        Graze,
        Miss
    }
    public HitType getHitType(int offense, int defense){
        if (offense == defense)
            return HitType.Hit;
        int lesser = Math.min(offense, defense);
        boolean defLess = defense < offense;
        if (lesser < ARITHMETIC_THRESHOLD){
            if (Math.abs(offense - defense)>ARITHMETIC_DIFFERENCE){
                return defLess ? HitType.Critical_Hit : HitType.Miss;
            } else if (offense < defense)
                return HitType.Graze;
            return HitType.Hit;
        } else {
            int greater = Math.max(offense, defense);
            if (greater/lesser>=2){
                return defLess ? HitType.Critical_Hit : HitType.Miss;
            } else if (offense < defense)
                return HitType.Graze;
            return HitType.Hit;
        }
    }
}
