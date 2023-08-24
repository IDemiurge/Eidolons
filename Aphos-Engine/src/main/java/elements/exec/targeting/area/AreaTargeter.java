package elements.exec.targeting.area;

import elements.content.enums.FieldConsts;
import framework.field.FieldPos;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Alexander on 8/23/2023
 * Maybe a family of exec classes?
 * Fixed | Selective | Auto
 * >> Do we need some aggregating object, any ability that needs 2 targetings for same FX?
 * >> Well, not for same fx I guess... although maybe
 * - things like Flagellant's "hit target, AND self" - or should we just copy?
 */
public class AreaTargeter {



    public Set<FieldPos> getArea(FieldPos from) {
        Set<FieldPos> set = getPreset(from);
        //some cases are easier to do with just 'add()', others with mass-check via generic method
        if (set!=null){
            return set;
        }
        set = new LinkedHashSet<>();
        for (FieldPos fieldPos : FieldConsts.all) {
            // if (checkCanTarget(from, fieldPos))
            //     set.add(fieldPos);
        }
        return set;
    }

    private Set<FieldPos> getPreset(FieldPos from) {
        // switch (type) {
        //     case Long_Range, Melee, Close_Quarters -> {
        //         // if (from.isFlank()){
        //         //     return ImmutableSet.of(from.getOppositeFlank(), type==Long_Range ? from.getNearestFront() : null);
        //         // }
        //         // if (from.isVanguard()){
        //         //     return ImmutableSet.of(type== Close_Quarters? from.getMelee() : from.getEnemyFront(), from.getOtherVanguard());
        //         // }
        //         // return ImmutableSet.of(from.getInFront());
        //     }
        //     case Range -> {
        //     }
        //     // case Ray , Ray_2x -> {
        //         //can hit flanks?
        //     //     front = from.getInFront();
        //     //     Set<FieldPos> set = front.getAllInFront(friendly_fire);
        //     //     if (type == Ray_2x){
        //     //         set.addAll(front.getAllInFront(friendly_fire));
        //     //     }
        //     //     return set;
        //     // }
        //     // case Any -> {
        //     //     if (friendly_fire){
        //     //         return ImmutableSet.of(FieldPos.all);
        //     //     }
        //     //     return ImmutableSet.of(from.getEnemyCells());
        //     // }
        // }
        return null;
    }
}
