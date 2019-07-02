package eidolons.game.battlecraft.logic.meta.igg.soul.eidola;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.datatypes.WeightMap;

import java.util.ArrayList;
import java.util.Map;

import static eidolons.game.module.herocreator.logic.items.ItemTraits.*;
import static eidolons.game.module.herocreator.logic.items.ItemTraits.EIDOLON_ASPECT.*;

/**
 * Created by JustMe on 12/7/2018.
 */
public class Soul {

    ObjType unitType;
    private WeightMap<EIDOLON_ASPECT> aspects;
//    EIDOLON_STATE state;

    public Soul(ObjType unitType) {
        this.unitType = unitType;
        init();
    }

    public WeightMap<EIDOLON_ASPECT> getAspects() {
        return aspects;
    }

    public ObjType getUnitType() {
        return unitType;
    }

    public int getForce() {
        return unitType.getIntParam(PARAMS.POWER);
    }
    public void init(){
        aspects = new WeightMap<>();

        String prop = unitType.getProperty(PROPS.EIDOLON_ASPECTS);
        if (prop.isEmpty()) {
            aspects.putAll(fromAspect(unitType.getAspect()));
//            prop = unitType.getProperty(G_PROPS.UNIT_GROUP);
//            aspects.putAll(fromGroup(new EnumMaster<UnitEnums.UNIT_GROUPS>().retrieveEnumConst(UnitEnums.UNIT_GROUPS.class, prop)));
//            MapMaster.addToIntegerMap();
        } else {
            aspects = new WeightMap<>(new RandomWizard< EIDOLON_ASPECT>().constructWeightMap(prop, EIDOLON_ASPECT.class));
        }

    }

    private WeightMap<EIDOLON_ASPECT> fromGroup(UnitEnums.UNIT_GROUPS group) {
        switch (group) {
            case DWARVES:
            case BANDITS:
            case SPIDERS:
            case CONSTRUCTS:

        }
        return new WeightMap<>();
    }
        private WeightMap<EIDOLON_ASPECT> fromAspect(GenericEnums.ASPECT aspect) {
        switch (aspect) {
            case NEUTRAL:
                return new WeightMap<>(EIDOLON_ASPECT.class).chain(STONE, 10).chain(METAL, 5).chain(LIGHTNING, 5);
            case ARCANUM:
                return new WeightMap<>(EIDOLON_ASPECT.class).chain(COSMOS, 10).chain(CRYSTAL, 5).chain(TECH, 5);
            case LIFE:
                return new WeightMap<>(EIDOLON_ASPECT.class).chain(WOOD, 10).chain(WIND, 6).chain(OCEAN, 5);
            case DARKNESS:
                return new WeightMap<>(EIDOLON_ASPECT.class).chain(DARK, 10).chain(MOON, 6).chain(MIST, 5);
            case CHAOS:
                return new WeightMap<>(EIDOLON_ASPECT.class).chain(CHAOS, 10).chain(WARPED, 7).chain(FLAME, 5);
            case LIGHT:
                return new WeightMap<>(EIDOLON_ASPECT.class).chain(LIGHT, 10).chain(PURE, 7).chain(SUN, 5);
            case DEATH:
                return new WeightMap<>(EIDOLON_ASPECT.class).chain(ASH, 8).chain(VENOM, 7).chain(BLOOD, 6);
        }
        return null;
    }
}
