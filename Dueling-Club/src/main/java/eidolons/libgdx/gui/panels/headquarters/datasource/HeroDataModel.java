package eidolons.libgdx.gui.panels.headquarters.datasource;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.Simulation;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PropMap;
import main.entity.type.ObjType;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by JustMe on 4/15/2018.
 * cloneMaps
 * take same items?
 * same item cannot be equipped by 2 entities...
 * <p>
 * ok, but at least share among the models ?
 * yes, just equip the one that is displayed
 */
public class HeroDataModel extends Unit {
    private Unit hero;

    public HeroDataModel(Unit hero) {
        super(new ObjType(hero.getType(), true), hero.getX(), hero.getY(),
         hero.getOriginalOwner(),
         Simulation.getGame(), hero.getRef().getCopy());
        cloneMaps(hero);
        this.hero = hero;
        reset();
    }

    public HeroDataModel(ObjType type, Pair<ParamMap, PropMap> pair) {
        super(type);
        cloneMaps(pair.getRight(), pair.getLeft());
    }


    public Unit getHero() {
        return hero;
    }

    @Override
    public void modified(ModifyValueEffect modifyValueEffect) {
        super.modified(modifyValueEffect);
        //full reset?
    }

    @Override
    public DC_HeroItemObj getItem(ITEM_SLOT slot) {
        return super.getItem(slot);
    }

    public DC_FeatObj getFeat(boolean skill, ObjType type) {
        return (DC_FeatObj) getGame().getSimulationObj(this, type,
         skill ? PROPS.SKILLS : PROPS.CLASSES);
    }
}
