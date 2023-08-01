package logic.action;

import com.google.inject.internal.util.ImmutableSet;
import logic.field.FieldPos;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

// import com.google.common.collect.ImmutableSet;

import static logic.action.Targeting.TargetingType.*;

/**
 * Created by Alexander on 6/11/2023 Special rules will be for Flank-to-Flank etc
 */
public class Targeting {
    //then we'll need CustomTargeting too
    boolean all_in_range; //if true, action affects all units that match the Targeting condition
    boolean friendly_fire;
    boolean left_right; //for ray only?
    TargetingType type;

    public enum TargetingType {
        Close_Quarters,
        Melee,
        Long_Range,
        Range,
        Ray,
        Ray_2x,
        Any,
    }

    public Set<FieldPos> getArea(FieldPos from) {
        Set<FieldPos> set = getPreset(from);
        //some cases are easier to do with just 'add()', others with mass-check via generic method
        if (set!=null){
            return set;
        }
        set = new LinkedHashSet<>();
        for (FieldPos fieldPos : FieldPos.all) {
            // if (checkCanTarget(from, fieldPos))
            //     set.add(fieldPos);
        }
        return set;
    }

    private Set<FieldPos> getPreset(FieldPos from) {
        switch (type) {
            case Long_Range, Melee, Close_Quarters -> {
                // if (from.isFlank()){
                //     return ImmutableSet.of(from.getOppositeFlank(), type==Long_Range ? from.getNearestFront() : null);
                // }
                // if (from.isVanguard()){
                //     return ImmutableSet.of(type== Close_Quarters? from.getMelee() : from.getEnemyFront(), from.getOtherVanguard());
                // }
                // return ImmutableSet.of(from.getInFront());
            }
            case Range -> {
            }
            // case Ray , Ray_2x -> {
                //can hit flanks?
            //     front = from.getInFront();
            //     Set<FieldPos> set = front.getAllInFront(friendly_fire);
            //     if (type == Ray_2x){
            //         set.addAll(front.getAllInFront(friendly_fire));
            //     }
            //     return set;
            // }
            // case Any -> {
            //     if (friendly_fire){
            //         return ImmutableSet.of(FieldPos.all);
            //     }
            //     return ImmutableSet.of(from.getEnemyCells());
            // }
        }
        return null;
    }
}
















