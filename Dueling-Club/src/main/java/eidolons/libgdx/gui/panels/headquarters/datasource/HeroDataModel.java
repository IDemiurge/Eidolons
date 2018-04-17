package eidolons.libgdx.gui.panels.headquarters.datasource;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PROPS;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.unit.Unit;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PropMap;
import main.entity.type.ObjType;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by JustMe on 4/15/2018.
 *
 * Will display proper values
 */
public class HeroDataModel extends Unit{
    private   Unit hero;

    public HeroDataModel(Unit hero) {
        super(hero);
        cloneMaps(hero);
        this.hero = hero;
        reset();
/*
cloneMaps
take same items?
same item cannot be equipped by 2 entities...

ok, but at least share among the models ?
yes, just equip the one that is displayed
 */
    }

    public Unit getHero() {
        return hero;
    }

    public HeroDataModel(ObjType type, Pair<ParamMap, PropMap> pair) {
        super(type);
        cloneMaps(pair.getRight(), pair.getLeft());
    }

    @Override
    public void modified(ModifyValueEffect modifyValueEffect) {
        super.modified(modifyValueEffect);
        //full reset?
    }

    public DC_FeatObj getFeat(boolean skill, ObjType type) {
        return (DC_FeatObj) getGame().getSimulationObj(this, type,
         skill ? PROPS.SKILLS : PROPS.CLASSES);
    }
}
