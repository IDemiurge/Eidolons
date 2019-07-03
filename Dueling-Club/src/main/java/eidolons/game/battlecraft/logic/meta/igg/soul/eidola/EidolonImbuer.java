package eidolons.game.battlecraft.logic.meta.igg.soul.eidola;

import eidolons.content.DC_CONSTS.ITEM_LEVEL;
import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.module.herocreator.logic.generic.RepertoireManager;
import eidolons.game.module.herocreator.logic.items.ItemTrait;
import eidolons.game.module.herocreator.logic.items.ItemTraitNamer;
import eidolons.game.module.herocreator.logic.items.ItemTraitParser;
import eidolons.game.module.herocreator.logic.items.ItemTraits.EIDOLON_ASPECT;
import eidolons.game.module.herocreator.logic.items.ItemTraits.ITEM_TRAIT;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums.ASPECT;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ArrayMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.datatypes.WeightMap;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 12/7/2018.
 */
public class EidolonImbuer {

    private static final String TEST_UNITS = "Dwarf Shieldman;Thief;Minotaur;Zombie;Imp;";

    public static Set<DC_HeroSlotItem> getValidItems(Unit hero) {
        Set<DC_HeroSlotItem> set = new LinkedHashSet<>();
        List<DC_HeroItemObj> full = new ArrayList<>();
        full.addAll(hero.getInventory());
        full.addAll(hero.getSlotItems());
        full.addAll(hero.getReserveItems());

        full.removeIf(item -> !isValidItem(item));
        for (DC_HeroItemObj itemObj : full) {
            set.add((DC_HeroSlotItem) itemObj);
        }
        return set;
    }

    private static boolean isValidItem(DC_HeroItemObj item) {
        if (item instanceof DC_HeroSlotItem) {

//            item.getProperty(PROPS.SOULS_IMBUED)   forbid double imbue?

            return true;
        }
        return false;
    }

    @Test
    public void test() {
        DC_Engine.dataInit();

        for (String substring : ContainerUtils.openContainer(TEST_UNITS)) {
            Soul soul = new Soul(DataManager.getType(substring, DC_TYPE.UNITS));

            ObjType item = DataManager.getType("Broad Sword", DC_TYPE.WEAPONS);

            Set<ItemTrait> traits = getTraits(item, soul);

            ItemTraitParser.applyTraits(item, traits.toArray(new ItemTrait[traits.size()]));

            String name = item.getName() + " imbued: " + ItemTraitNamer.getDescriptor(item, traits);

            item.setName(name);

            continue;
        }


    }

    public DC_HeroSlotItem imbue(DC_HeroItemObj item, Soul... eidolon) {
        //basically create a new item?

        //copy durability, previous...
        ObjType newType = new ObjType(item.getType());

        Set<ItemTrait> traits = getTraits(item, eidolon);
        //replace old traits? that wouldn't be much fun...

        ItemTraitParser.applyTraits(newType, traits.toArray(new ItemTrait[traits.size()]));

        String name = item.getName() + " imbued: " + ItemTraitNamer.getDescriptor(item, traits);

        newType.setName(name);

        item.remove();

        return (DC_HeroSlotItem) ItemFactory.createItemObj(newType, item.getOriginalUnit(), false);
    }

    //DC_HeroSlotItem
    public Set<ItemTrait> getTraits(Entity item, Soul... souls) {
        WeightMap<EIDOLON_ASPECT> map = createMap(souls);

        List<ITEM_TRAIT> pool = Arrays.stream(ITEM_TRAIT.all).filter(trait ->
                isValid(trait, map) &&
                        ArrayMaster.contains_(trait.getTypes(), item.getOBJ_TYPE_ENUM()))
                .collect(Collectors.toList());

        Set<ItemTrait> set = new LinkedHashSet<>();
        ITEM_LEVEL[] levels = getLevels(souls);
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
        int maxValue = getMaxValue(item, souls);
        List<ItemTrait> traits = new RepertoireManager<ItemTrait, EIDOLON_ASPECT>().select(
                n + 1, n - 1, n, set, maxValue, 0, getMapFunction(), getValueFunction(),
                map, 0.3f, true, false
        );
        return new LinkedHashSet<>(traits);
    }

    private int getMaxValue(Entity item, Soul... eidolon) {
        int force = 0;
        for (Soul soul : eidolon) {
            force += soul.getForce();
        }
        return force;
    }

    private ITEM_LEVEL[] getLevels(Soul... eidolon) {

        int force = 0;
        for (Soul soul : eidolon) {
            force += soul.getForce();
        }

        for (ITEM_LEVEL value : ITEM_LEVEL.values()) {
            switch (value) {
                case MINOR:
                    break;
                case LESSER:
                    break;
                case COMMON:
                    break;
                case GREATER:
                    break;
                case LEGENDARY:
                    break;
            }

        }

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


    public static String getAspects(Soul... souls) {
        WeightMap<EIDOLON_ASPECT> map = createMap(souls);
//        map.sortByWeight();

        return ContainerUtils.constructStringContainer(map.keySet().stream().map(
                a -> StringMaster.getWellFormattedString(a.toString()) + StringMaster.wrapInParenthesis(map.get(a) + "")
        ).collect(Collectors.toList()), " ");
    }

    private static WeightMap<EIDOLON_ASPECT> createMap(Soul... souls) {
        WeightMap<EIDOLON_ASPECT> map = new WeightMap<>();
        for (Soul eidolon : souls) {
            if (eidolon == null) {
                continue;
            }
            for (EIDOLON_ASPECT aspect : eidolon.getAspects().keySet()) {
                MapMaster.addToIntegerMap(map, aspect, eidolon.getAspects().get(aspect));
            }
        }
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
