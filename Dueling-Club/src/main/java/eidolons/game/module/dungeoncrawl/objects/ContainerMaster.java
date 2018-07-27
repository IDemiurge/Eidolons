package eidolons.game.module.dungeoncrawl.objects;

import eidolons.ability.InventoryTransactionManager;
import eidolons.content.DC_CONSTS.JEWELRY_ITEM_TRAIT;
import eidolons.content.DC_CONSTS.MAGICAL_ITEM_LEVEL;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.module.dungeoncrawl.objects.ContainerMaster.CONTAINER_ACTION;
import eidolons.game.module.herocreator.logic.items.ItemGenerator;
import eidolons.game.module.herocreator.logic.items.ItemMaster;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerDataSource;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.DungeonObjEnums.CONTAINER_CONTENTS;
import main.content.enums.entity.DungeonObjEnums.CONTAINER_CONTENT_VALUE;
import main.content.enums.entity.ItemEnums.ITEM_RARITY;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.enums.entity.ItemEnums.QUALITY_LEVEL;
import main.content.enums.entity.ItemEnums.WEAPON_GROUP;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.SortMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.entity.FilterMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerMaster extends DungeonObjMaster<CONTAINER_ACTION> {

    public static final String[] treasureContents = {
     CONTAINER_CONTENTS.AMMO.name()
    };
    public static final String OPEN = "_open";
    Map<ObjType, Map<CONTAINER_CONTENTS,
     Map<ITEM_RARITY, List<ObjType>>>> itemPoolsMaps = new HashMap();
    private boolean test_mode = false;
    private QUALITY_LEVEL[] exceptionalQualities;
    private QUALITY_LEVEL[] rareQualities;
    private QUALITY_LEVEL[] uncommonQualities;
    private QUALITY_LEVEL[] commonQualities;
    private MATERIAL[] exceptionalMaterials;
    private MATERIAL[] rareMaterials;
    private MATERIAL[] uncommonMaterials;
    private MATERIAL[] commonMaterials;
    private boolean noDuplicates = true;


    public ContainerMaster(DungeonMaster dungeonMaster) {
        super(dungeonMaster);
    }

    public static boolean isPregenerateItems() {
        return CoreEngine.isIDE();
    }

    public static boolean isGenerateItemsForUnits() {
        return false;
    }

    public static boolean isGenerateItemsForContainers() {
        return !CoreEngine.isFastMode();
    }

    public ObjType getItem(CONTAINER_CONTENTS c, int maxCost) {

        int random = RandomWizard.getRandomInt(100);
        ITEM_RARITY rarity = ITEM_RARITY.COMMON;
        for (ITEM_RARITY sub : ITEM_RARITY.values()) {
            if (sub.getChance() >= random) {
                rarity = sub;
                break;
            }
        }
        main.system.auxiliary.log.LogMaster.log(1, "getItem " + c + "; " + rarity + maxCost);
        ObjType type = preselectBaseType(c, rarity);
        main.system.auxiliary.log.LogMaster.log(1, "preselectBaseType " + type);
        if (type == null)
            return null;
        List<ObjType> pool = new ArrayList<>(getItemPool(c, rarity, type)); //base types!
        main.system.auxiliary.log.LogMaster.log(1, "pool= " + pool);
        if (pool.isEmpty())
            return null;
        if (isRemoveBase(c, type))
            pool.removeIf(item -> !item.isGenerated());
        else {
//            pool.size(); debug
        }
        if (pool.isEmpty())
            return null;

        pool.removeIf(item -> item.getIntParam(PARAMS.GOLD_COST) > maxCost);
        if (pool.isEmpty())
            return null;
        if (pool.size() > 20) {
            SortMaster.sortEntitiesByExpression(pool, (item) -> item.getIntParam(PARAMS.GOLD_COST));
            pool.removeIf(item -> pool.indexOf(item) > pool.size() / 2);
        }
        main.system.auxiliary.log.LogMaster.log(1, "filtered pool= " + pool + " max cost: " + maxCost);
        if (pool.isEmpty())
            return null;
        ObjType baseType = new RandomWizard<ObjType>().getRandomListItem(pool);

        return baseType;

    }

    private boolean isRemoveBase(CONTAINER_CONTENTS c, ObjType type) {
        if (c == CONTAINER_CONTENTS.FOOD)
            return false;
        return !type.getSubGroupingKey().equalsIgnoreCase("Food");
    }

    private List<ObjType> getItemPool(CONTAINER_CONTENTS c, ITEM_RARITY rarity, ObjType type) {
        Map<CONTAINER_CONTENTS, Map<ITEM_RARITY, List<ObjType>>> map = itemPoolsMaps.get(type); //getItemPoolsMap(type);
        if (map == null) {
            map = new HashMap<>();
            itemPoolsMaps.put(type, map);
        }

        Map<ITEM_RARITY, List<ObjType>> rarityMap = map.get(c);
        if (rarityMap == null) {
            rarityMap = new HashMap<>();
            map.put(c, rarityMap);
        }
        List<ObjType> pool = rarityMap.get(rarity);
        if (pool == null) {
            pool = initPool(c, rarity, type);
            main.system.auxiliary.log.LogMaster.log(1, "initPool= " + pool);
            rarityMap.put(rarity, pool);
        }
        if (pool == null) {
            return new ArrayList<>();
        }
        return pool;
    }

    private ObjType preselectBaseType(CONTAINER_CONTENTS contents, ITEM_RARITY rarity) {
        DC_TYPE TYPE = getRandomTYPE(contents);
        boolean iterate =
         isIterateAlways() ||
          TYPE == DC_TYPE.ITEMS || TYPE == DC_TYPE.JEWELRY;

        if (isFastSelect()) {
            String group = getItemGroup(contents, false, TYPE);
            String subgroup = getItemGroup(contents, true, TYPE);
            ObjType type = null;
            List<ObjType> types = DataManager.getTypes(TYPE, iterate);
            main.system.auxiliary.log.LogMaster.log(1, " group= " + group + " subgroup= " + subgroup
             + "; types = " + types);
            Iterator<ObjType> iterator = types.iterator();
            int n = 0;
            Loop loop = new Loop(5);//Math.min(50, types.size() / 3));
            while (true) {

                if (iterate) {
                    if (!iterator.hasNext())
                        return null;
                    type = iterator.next();
                } else {

                    n++;
                    type = new RandomWizard<ObjType>().
                     getRandomListItem(types);
                    if (n > types.size() / 5) {
                        if (!loop.continues())
                            return null;
                        group = getItemGroup(contents, false, TYPE);
                        subgroup = getItemGroup(contents, true, TYPE);
                        n = 0;
                    }
                }
                if (type == null)
                    break;
                if (type.isGenerated())
                    continue;
                if (isRemoveBase(contents, type) && TYPE != DC_TYPE.JEWELRY)
                    if (!type.checkProperty(PROPS.ITEM_RARITY, rarity.name()))
                        continue;
                if (group != null)
                    if (!type.checkGroupingProperty(group))
                        continue;
                if (subgroup != null)
                    if (!type.checkSubGroup(subgroup))
                        continue;
                break;
            }
            return type;
        }
        List<ObjType> list = new ArrayList<>();
        String groups = getItemGroups(contents, TYPE);
        if (groups == null) groups = " ;";
        for (String sub : groups.split(";")) {
            if (sub.trim().isEmpty())
                sub = null;
            List<ObjType> group = new ArrayList<>(
             isGroupsOrSubgroups(contents, TYPE) ?
              DataManager.getTypesGroup(TYPE, sub) :
              DataManager.getTypesSubGroup(TYPE, sub));
            //TODO set rarity to common by default
            FilterMaster.filterByPropJ8(group, PROPS.ITEM_RARITY.getName(), rarity.name());
            filter(contents, group, rarity, TYPE);
            if (group.isEmpty())
                continue;
//            group = generateTypes(c, rarity, group);
//            group.removeIf(type -> !type.isGenerated()); //remove base types

            list.addAll(group);
        }
        main.system.auxiliary.log.LogMaster.log(1, "type list= " + list);
        if (list.isEmpty()) {
            return null;
        }
        return new RandomWizard<ObjType>().getRandomListItem(list);
    }

    private void filter(CONTAINER_CONTENTS contents, List<ObjType> group, ITEM_RARITY rarity, DC_TYPE TYPE) {
        switch (TYPE) {
            case WEAPONS:
                group.removeIf(type -> type.getProperty(G_PROPS.WEAPON_GROUP).equalsIgnoreCase(WEAPON_GROUP.FORCE.name()));
        }
    }

    private boolean isIterateAlways() {
        return true;
    }

    private boolean isFastSelect() {
        return true;
    }

    private List<ObjType> initPool(CONTAINER_CONTENTS c, ITEM_RARITY rarity, ObjType type) {

        return generateTypes(c, rarity, type);
    }

    private List<ObjType> generateJewelry(ITEM_RARITY rarity, List<ObjType> group) {
        ItemGenerator generator = ItemGenerator.getDefaultGenerator();
        JEWELRY_ITEM_TRAIT[] traits = getJewelryTraits(rarity);
        MAGICAL_ITEM_LEVEL[] levels = getJewelryLevels(rarity);
        for (ObjType sub : new ArrayList<>(group))
            for (MAGICAL_ITEM_LEVEL level : levels)
                for (JEWELRY_ITEM_TRAIT trait : traits)
                    group.add(generator.getOrCreateJewelry(sub, trait, level));

        return group;
    }

    private MAGICAL_ITEM_LEVEL[] getJewelryLevels(ITEM_RARITY rarity) {
        switch (rarity) {
            case EXCEPTIONAL:
                return new MAGICAL_ITEM_LEVEL[]{
                 MAGICAL_ITEM_LEVEL.LEGENDARY
                };
            case RARE:
                return new MAGICAL_ITEM_LEVEL[]{
                 MAGICAL_ITEM_LEVEL.LEGENDARY,
                 MAGICAL_ITEM_LEVEL.GREATER,
                };
            case UNCOMMON:
                return new MAGICAL_ITEM_LEVEL[]{
                 MAGICAL_ITEM_LEVEL.COMMON,
                 MAGICAL_ITEM_LEVEL.GREATER,
                };
            case COMMON:
                return new MAGICAL_ITEM_LEVEL[]{
                 MAGICAL_ITEM_LEVEL.COMMON,
                 MAGICAL_ITEM_LEVEL.LESSER,
                };
        }
        return new MAGICAL_ITEM_LEVEL[0];
    }

    private JEWELRY_ITEM_TRAIT[] getJewelryTraits(ITEM_RARITY rarity) {
        return JEWELRY_ITEM_TRAIT.values();
    }

    private List<ObjType> generateTypes(CONTAINER_CONTENTS c, ITEM_RARITY rarity,
                                        ObjType type) {
        List<ObjType> list = new ArrayList<>();
        list.add(type);
        list = generateTypes(c, rarity, list
//         new ListMaster<ObjType>().getList(type)
        );
        list = ContainerFilter.filter(list, c, rarity);
        return list;
    }

    private List<ObjType> generateTypes(CONTAINER_CONTENTS c, ITEM_RARITY rarity,
                                        List<ObjType> group) {

        if (c == CONTAINER_CONTENTS.POTIONS ||
         group.get(0).getGroup().equalsIgnoreCase("Alchemy")) {
            return
//             DataManager.getTypesGroup(DC_TYPE.ITEMS, "Alchemy");
            DataManager.getTypesSubGroup(DC_TYPE.ITEMS, "Potions");
//            return DataManager.gettype;
        }
        if (c == CONTAINER_CONTENTS.JEWELRY ||
         group.get(0).getOBJ_TYPE_ENUM() == DC_TYPE.JEWELRY) {
            if (!ItemGenerator.isJewelryOn())
                return null;
            return generateJewelry(rarity, group);
        }
        ItemGenerator generator = ItemGenerator.getDefaultGenerator();
        MATERIAL[] materials = getMaterials(rarity);
        QUALITY_LEVEL[] qualityLevels = getQualityLevels(rarity);

        for (ObjType type : new ArrayList<>(group)) {
            if (type.isGenerated()) {
                continue;
            }
            for (MATERIAL material : materials) {
                if (!ItemMaster.checkMaterial(type, material))
                    continue;
                for (QUALITY_LEVEL quality : qualityLevels) {
                    boolean weapon = type.getOBJ_TYPE_ENUM() == DC_TYPE.WEAPONS;
                    String name = ItemGenerator.generateName(quality, material, type);
                    ObjType newType = DataManager.getType(name, type.getOBJ_TYPE());
                    if (newType == null)
                        newType = generator.generateItem(weapon, quality, material, type);
                    group.add(newType);
                }
            }
        }
        return group;
    }

    private DC_TYPE getRandomTYPE(CONTAINER_CONTENTS c) {
        OBJ_TYPE TYPE = getTYPE(c);
        if (TYPE instanceof C_OBJ_TYPE) {
            while (true) {
                TYPE = new RandomWizard<DC_TYPE>().getRandomListItem(
                 Arrays.asList(((C_OBJ_TYPE) TYPE).getTypes()));
                if (TYPE == DC_TYPE.JEWELRY)
                    if (!ItemGenerator.isJewelryOn())
                        continue;
                break;
            }
        }
        return (DC_TYPE) TYPE;
    }

    private OBJ_TYPE getTYPE(CONTAINER_CONTENTS c) {
        switch (c) {
            case POTIONS:
            case FOOD:
            case TOOLS:
                return DC_TYPE.ITEMS;
            case ARMOR:
                return DC_TYPE.ARMOR;
            case AMMO:
            case WEAPONS:
                return DC_TYPE.WEAPONS;
        }
        return C_OBJ_TYPE.ITEMS;
    }

    private boolean isGroupsOrSubgroups(CONTAINER_CONTENTS c, DC_TYPE TYPE) {
        switch (c) {
            case FOOD:
            case POTIONS:
            case JUNK:
                return false;
            case TREASURE:
                if (TYPE == DC_TYPE.ITEMS)
                    return false;
        }
        return true;
    }

    private String getItemGroup(CONTAINER_CONTENTS c, boolean sub, DC_TYPE TYPE) {
        if (isGroupsOrSubgroups(c, TYPE) == sub)
            return null;
        String list = getItemGroups(c, TYPE);
        if (list == null)
            return null;
        return new RandomWizard<String>().getRandomListItem(ContainerUtils.openContainer(list));
    }

    private String getItemGroups(CONTAINER_CONTENTS c, DC_TYPE TYPE) {
        switch (c) {
            case JUNK:
                if (TYPE == DC_TYPE.ARMOR)
                    return "Cloth;";
                if (TYPE == DC_TYPE.ITEMS)
                    return "Coating;";
                if (TYPE == DC_TYPE.JEWELRY)
                    return "Rings;";
//                if (DC_Game.game.isMacroOn())
                return "Bolts;Arrows;Claws;Daggers;";
            case TREASURE:
                if (TYPE == DC_TYPE.ARMOR)
                    return "Heavy;";
                if (TYPE == DC_TYPE.ITEMS)
                    return "Elixirs;";
                if (TYPE == DC_TYPE.JEWELRY)
                    return null;
                return "Magical;Blade;Axe;Blunt;";
            case MISC:
                if (TYPE == DC_TYPE.ARMOR)
                    return "Light;";
                if (TYPE == DC_TYPE.ITEMS)
                    return "Alchemy;";
                if (TYPE == DC_TYPE.JEWELRY)
                    return null;
                return "Magical;Ammo;Ranged;Pole Arm;";
            case AMMO:
                return "Ammo";
            case FOOD:
                return "Food";
            case POTIONS:
                return "Potions;Potions;Concoctions;Coating;Elixirs;";
        }
        return null;
    }

    @Override
    protected boolean actionActivated(CONTAINER_ACTION sub, Unit unit, DungeonObj obj) {
        return loot(unit, obj);
    }

    public static boolean loot(Unit unit, DC_Obj obj) {
        unit.getGame().getInventoryManager().setOperationsPool(5);

        Pair<InventoryDataSource, ContainerDataSource> param =
         new ImmutablePair<>(new InventoryDataSource(unit), new ContainerDataSource(obj, unit));
        GuiEventManager.trigger(GuiEventType.SHOW_LOOT_PANEL, param);
        boolean result = (boolean) WaitMaster.waitForInput(InventoryTransactionManager.OPERATION);
        unit.getGame().getManager().reset();
        return result;
    }
    @Override
    public List<DC_ActiveObj> getActions(DungeonObj obj, Unit unit) {
        List<DC_ActiveObj> list = new ArrayList<>();
        list.add(
         createAction(CONTAINER_ACTION.OPEN, unit, obj));
        return list;
    }

    public void initContentsProps(Entity unit) {

    }

    public void initContents(BattleFieldObject obj) {
        String contents = "";

        RandomWizard<CONTAINER_CONTENTS> wizard = new RandomWizard<>();
        String prop = obj.getProperty(PROPS.CONTAINER_CONTENTS);
        if (test_mode)
            prop = "Food(10);";
        Map<CONTAINER_CONTENTS, Integer> map =
         wizard.
          constructWeightMap(prop, CONTAINER_CONTENTS.class);

        prop = obj.getProperty(PROPS.CONTAINER_CONTENT_VALUE);
        RandomWizard<CONTAINER_CONTENT_VALUE> wizard_ = new RandomWizard<>();
        Map<CONTAINER_CONTENT_VALUE, Integer> typeMap =
         wizard_.constructWeightMap(prop, CONTAINER_CONTENT_VALUE.class);

        List<CONTAINER_CONTENT_VALUE> itemValueList
         = new ArrayList<>();
        Integer maxCost = obj.getIntParam(PARAMS.GOLD_TOTAL);
        int totalCost = 0;
        int maxGroups = 2; //size prop!
        Loop overLoop = new Loop(maxGroups);
        while (overLoop.continues()) {
            CONTAINER_CONTENT_VALUE rarity = wizard_.getObjectByWeight(typeMap);
            if (rarity == null)
                rarity = CONTAINER_CONTENT_VALUE.COMMON;
            if (noDuplicates)
                typeMap.remove(rarity);
            if (totalCost + rarity.getGoldCost() > maxCost)
                break;
            itemValueList.add(rarity);
            totalCost += rarity.getGoldCost();
        }
        float randomization = (isRandomizationOn()) ? new Random().nextFloat() + 0.5f : 1;
        for (CONTAINER_CONTENT_VALUE rarity : itemValueList) {
            maxCost = Math.round(randomization * Math.min(totalCost, rarity.getGoldCost()));
            CONTAINER_CONTENTS c = wizard.getObjectByWeight(map);
            if (noDuplicates)
                map.remove(c);
            Integer maxItems = c.getMaxItems();
            Integer cost = 0;
            int items = 0;
            Loop loop = new Loop(maxItems * 3);
            while (cost < maxCost && items <= maxItems) {
                if (!loop.continues())
                    break;
                int max = Math.round((maxCost - cost) * 1.2f);
                ObjType item = getItem(c, max);
                if (item == null)
                    if (c == CONTAINER_CONTENTS.FOOD)
                        continue;
                main.system.auxiliary.log.LogMaster.log(1, c + " rarity  " + rarity + " = " + item
                 + "; total : " + cost);
                if (item == null)
                    continue;
                contents += item.getName() + StringMaster.SEPARATOR;
                cost += item.getIntParam(PARAMS.GOLD_COST);
                items++;
            }
        }
        if (contents.isEmpty()) {
            main.system.auxiliary.log.LogMaster.log(1, ">> " + obj + " has contents: " + contents + itemValueList);
        } else
            main.system.auxiliary.log.LogMaster.log(1, ">> " + obj + " has contents: " + contents + itemValueList);

        obj.setProperty(PROPS.INVENTORY, contents);


    }

    private boolean isRandomizationOn() {
        return false;
    }

    @Override
    public void open(DungeonObj obj, Ref ref) {

    }

    @Override
    public DC_ActiveObj getDefaultAction(Unit source, DungeonObj target) {
        return createAction(CONTAINER_ACTION.OPEN, source, target);
    }

    private QUALITY_LEVEL[] getQualityLevels(ITEM_RARITY rarity) {
        switch (rarity) {
            case EXCEPTIONAL:
                if (exceptionalQualities == null)
                    exceptionalQualities = new QUALITY_LEVEL[]{
                     QUALITY_LEVEL.ANCIENT, QUALITY_LEVEL.SUPERB, QUALITY_LEVEL.MASTERPIECE
                    };
                return exceptionalQualities;
            case RARE:
                if (rareQualities == null)
                    rareQualities = new QUALITY_LEVEL[]{
                     QUALITY_LEVEL.ANCIENT, QUALITY_LEVEL.SUPERIOR,
                     QUALITY_LEVEL.SUPERB, QUALITY_LEVEL.MASTERPIECE
                    };
                return rareQualities;
            case UNCOMMON:
                if (uncommonQualities == null)
                    uncommonQualities = new QUALITY_LEVEL[]{
                     QUALITY_LEVEL.DAMAGED, QUALITY_LEVEL.OLD,
                     QUALITY_LEVEL.NORMAL, QUALITY_LEVEL.SUPERIOR,
                     QUALITY_LEVEL.INFERIOR,
                     QUALITY_LEVEL.ANCIENT, QUALITY_LEVEL.SUPERIOR,
                     QUALITY_LEVEL.SUPERB, QUALITY_LEVEL.MASTERPIECE};
                return uncommonQualities;
            case COMMON:
                if (commonQualities == null)
                    commonQualities = new QUALITY_LEVEL[]{
                     QUALITY_LEVEL.DAMAGED, QUALITY_LEVEL.OLD,
                     QUALITY_LEVEL.NORMAL, QUALITY_LEVEL.SUPERIOR,
                     QUALITY_LEVEL.INFERIOR,
                    };
                return commonQualities;
        }
        return QUALITY_LEVEL.values();
    }

    private MATERIAL[] getMaterials(ITEM_RARITY rarity) {
        switch (rarity) {
            case EXCEPTIONAL:
                if (exceptionalMaterials == null) {
                    exceptionalMaterials = Arrays.asList(MATERIAL.values()).
                     stream().filter(material -> material.getCost() > 200
                     && material.isMagical()).toArray(MATERIAL[]::new);
                }
                return exceptionalMaterials;
            case RARE:
                if (rareMaterials == null) {
                    rareMaterials = Arrays.asList(MATERIAL.values()).stream().
                     filter(material -> material.getCost() > 150
                      || material.isMagical()).toArray(MATERIAL[]::new);
                }
                return rareMaterials;
            case UNCOMMON:
                if (uncommonMaterials == null) {
                    uncommonMaterials =
                     Arrays.stream(MATERIAL.values()).filter(material -> material.getCost()
                      >= 100
                      || material.isMagical()).toArray(MATERIAL[]::new);

                }
                return uncommonMaterials;
            case COMMON:
                if (commonMaterials == null) {
                    commonMaterials = Arrays.asList(MATERIAL.values()).stream().
                     filter(material -> material.getCost() < 100
                      && !material.isMagical()).toArray(MATERIAL[]::new);
                }
                return commonMaterials;
        }
        return ItemGenerator.BASIC_MATERIALS_WOOD;
    }

    public enum CONTAINER_ACTION implements DUNGEON_OBJ_ACTION {
        OPEN,
    }
}
