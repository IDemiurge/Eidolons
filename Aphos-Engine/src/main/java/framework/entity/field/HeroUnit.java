package framework.entity.field;

import elements.exec.EntityRef;
import framework.entity.Entity;
import framework.field.FieldPos;
import framework.entity.field.Unit;

import java.util.Map;

/**
 * Created by Alexander on 8/21/2023
 */
public class HeroUnit extends Unit {

    // FeatSet feats;
    // Seal summonSeal; //put marks ... by more than 1 condition perhaps

    public HeroUnit(Map<String, Object> valueMap, Boolean ally, FieldPos pos) {
        super(valueMap, ally, pos);
    }


}
