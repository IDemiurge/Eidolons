package eidolons.game.module.nethergate.apholon.eidola;

import eidolons.content.DC_CONSTS.ITEM_LEVEL;
import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.herocreator.logic.generic.RepertoireManager;
import eidolons.game.module.herocreator.logic.items.ItemTrait;
import eidolons.game.module.herocreator.logic.items.ItemTraitNamer;
import eidolons.game.module.herocreator.logic.items.ItemTraitParser;
import eidolons.game.module.herocreator.logic.items.ItemTraits.EIDOLON_ASPECT;
import eidolons.game.module.herocreator.logic.items.ItemTraits.ITEM_TRAIT;
import main.content.enums.GenericEnums.ASPECT;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.data.ArrayMaster;
import main.system.datatypes.WeightMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 12/7/2018.
 */
public class EidolonImbuer {

    public Set<DC_HeroSlotItem> getValidItems(Unit hero) {

        Set<DC_HeroSlotItem> set = new LinkedHashSet<>();


        return set;
    }

    public void imbue(DC_HeroSlotItem item, Eidolon eidolon) {
        //basically create a new item?

        //copy durability, previous...
        ObjType newType = new ObjType(item.getType());

        Set<ItemTrait> traits = getTraits(eidolon, item);
        //replace old traits? that wouldn't be much fun...

        ItemTraitParser.applyTraits(newType, traits.toArray(new ItemTrait[traits.size()]));

        String name = item.getName() + " imbued: " + ItemTraitNamer.getDescriptor(item, traits);
    }
//DC_HeroSlotItem
    public Set<ItemTrait> getTraits(Eidolon eidolon, Entity item) {
        WeightMap<EIDOLON_ASPECT> map = createMap(eidolon);

        List<ITEM_TRAIT> pool = Arrays.stream(ITEM_TRAIT.all).filter(trait ->
         isValid(trait, map) &&
          ArrayMaster.contains_(trait.getTypes(), item.getOBJ_TYPE_ENUM()))
         .collect(Collectors.toList());

        Set<ItemTrait> set = new LinkedHashSet<>();
        ITEM_LEVEL[] levels = getLevels(eidolon);
        for (ITEM_TRAIT type : pool) {
            for (ITEM_LEVEL level : levels) {
                set.add(new ItemTrait(type, level));
            }

        }
        //required number?

        /*
        can we generalize the system of randomly acquired N items from a pool with weights?

        we would need a couple of functions

        target type

         */
        int n = 2;
        int maxValue = getMaxValue(eidolon, item);
        List<ItemTrait> traits = new RepertoireManager<ItemTrait, EIDOLON_ASPECT>().select(
         n + 1, n - 1, n, set, maxValue, 0, getMapFunction(), getValueFunction(),
         map, 0.3f, true, false
        );
        return new LinkedHashSet<>(traits);
    }

    private int getMaxValue(Eidolon eidolon, Entity item) {
        return eidolon.getUnitType().getIntParam(PARAMS.POWER);
    }

    private ITEM_LEVEL[] getLevels(Eidolon eidolon) {
        return ITEM_LEVEL.values();
    }

    private Function<ItemTrait, Integer> getValueFunction() {
        return trait -> getTraitValue(trait);
    }

    private Integer getTraitValue(ItemTrait trait) {
        return trait.getLevel().getPower();
    }

    private Function<Pair<ItemTrait, EIDOLON_ASPECT>, Integer> getMapFunction() {
        return pair -> pair.getKey().getTemplate().getStyleMap().get(pair.getValue());
    }


    private WeightMap<EIDOLON_ASPECT> createMap(Eidolon eidolon) {
        ObjType type = eidolon.getUnitType();
        WeightMap<EIDOLON_ASPECT> map = new WeightMap<>();
        UNIT_GROUP group;
        ASPECT aspect = type.getAspect();
        //new special property?
        switch (aspect) {
            case NEUTRAL:
                break;
            case ARCANUM:
                break;
            case LIFE:
                break;
            case DARKNESS:
                break;
            case CHAOS:
                break;
            case LIGHT:
                break;
            case DEATH:
                break;
            default:
                map.chain(EIDOLON_ASPECT.EVIL, 10);
        }
        //        map.chain()
        return map;
    }

    private boolean isValid(ITEM_TRAIT trait, WeightMap<EIDOLON_ASPECT> map) {
        if (trait.getStyleMap() == null) {
            return false;
        }
        for (EIDOLON_ASPECT aspect : trait.getStyleMap().keySet()) {
            if (map.keySet().contains(aspect)) {
                return true;
            }
        }
        return false;
    }


}
