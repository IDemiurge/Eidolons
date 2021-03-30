package eidolons.game.battlecraft.rules.combat.attack.accuracy;

import main.content.enums.entity.NewRpgEnums;

import java.util.Map;

public class AccuracyBreakdown {
    //tooltip!
    Map<NewRpgEnums.HitType, Integer> chances;
    Map<NewRpgEnums.HitType, Integer> min;
    Map<NewRpgEnums.HitType, Integer> max;

    public AccuracyBreakdown(Map<NewRpgEnums.HitType, Integer> chances, Map<NewRpgEnums.HitType, Integer> min, Map<NewRpgEnums.HitType, Integer> max) {
        this.chances = chances;
        this.min = min;
        this.max = max;
    }
/*
Armor info?
Penetration chance
reduction in ()

what would be easy to understand?
If we have outer and inner armor?

 */
}
