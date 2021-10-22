package eidolons.entity.active.spaces;

import eidolons.content.PROPS;
import eidolons.entity.unit.Unit;
import main.content.enums.entity.NewRpgEnums;
import main.content.enums.entity.NewRpgEnums.FEAT_SPACE_VALUE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FeatSpaceInitializer implements IFeatSpaceInitializer {
    public static final PROPS[] featSpacePropsSpell = {
            PROPS.VERBATIM_SPACES,
            PROPS.MEMORIZED_SPACES,
            PROPS.GRIMOIRE_SPACES,
            PROPS.DIVINED_SPACES,
            PROPS.CUSTOM_SPACES,
    };
    public static final PROPS[] featSpaceProps = {
            PROPS.QUICK_ITEMS,
            PROPS.COMBAT_SPACES,
    };
    public static final int MAX_SLOTS = 6;

    @Override
    public FeatSpaces createFeatSpaces(Unit unit, boolean spellSpaces) {
        initDefaultSpaces(unit);
        FeatSpaces spaces = new FeatSpaces(unit, spellSpaces);
        int index = 0;
        //TODO what about standard ones - memorized/verbatim?
        for (PROPS prop : spellSpaces ? featSpacePropsSpell : featSpaceProps)
            for (String string : ContainerUtils.openContainer(
                    unit.getProperty(prop), FeatSpaceData.getInstanceSeparator())) {
                FeatSpaceData data = new FeatSpaceData(string);
                FeatSpace space = createSpace(index, unit, spaces, data, spellSpaces);
                spaces.add(space);
            }

        return spaces;
    }

    // name,
    // type,
    // mode,
    // actives,
    // skin,
    // index,
    public void initDefaultSpaces(Entity unit) {
        //this is an AD HOC method - in reality we must BUILD space-props explicitly!
        initDefaultCombatSpaces(unit);
        initDefaultSpellSpaces(unit);
    }
        public void initDefaultSpellSpaces(Entity unit) {
            initSpaceProp(unit, PROPS.VERBATIM_SPELLS, PROPS.VERBATIM_SPACES, "Memorized");
            initSpaceProp(unit, PROPS.MEMORIZED_SPELLS, PROPS.MEMORIZED_SPACES, "Verbatim");
            // initSpace(unit, PROPS.GRIMOIRE_SPELLS, PROPS.GRIMOIRE_SPACES, "grimoire");
    }
    public void initDefaultCombatSpaces(Entity unit) {
        initSpaceProp(unit, G_PROPS.ACTIVES, PROPS.COMBAT_SPACES, "Combat Skills");
        //TODO split items between spaces created from Inventory items!
        // quick slot param increases the possible size
        initSpaceProp(unit, PROPS.QUICK_ITEMS, PROPS.QUICK_ITEMS_SPACES, "Quick Items");
    }
    private void initSpaceProp(Entity unit, PROPERTY prop, PROPERTY spaceProp, String name) {
        //this assumes less than six actions?!
        FeatSpaceData data = new FeatSpaceData("");
        String s = unit.getProperty(prop);
        List<String> actives = ContainerUtils.openContainer(s);
        for (int i = 0; i < MAX_SLOTS; i++) {
            if (actives.size()<=i) break;
            data.setActive(i, actives.get(i));
        }
        data.setValue(FEAT_SPACE_VALUE.feats, s);
        data.setValue(FEAT_SPACE_VALUE.name, name);
        unit.setProperty(spaceProp, data.toString());
    }

    private FeatSpace createSpace(int i, Unit unit, FeatSpaces spaces, FeatSpaceData data, boolean spellSpaces) {
        NewRpgEnums.FEAT_SPACE_TYPE type = data.getType();
        // mods = getGlobalMods(unit, type); //TODO
        Map<Integer, Feat> actives = createActives(unit, data);
        FeatSpace space = new FeatSpace(i, data.getName(), unit, type, data.getMode(), actives);
        return space;
    }

    private Map<Integer, Feat> createActives(Unit unit, FeatSpaceData value) {
        Map<Integer, Feat> actions = new LinkedHashMap<>();
        for (int i = 0; i < MAX_SLOTS; i++) {
            String name = value.getActive(i);
            if (StringMaster.isEmpty(name)) {
                continue;
            }
            Feat action = unit.getGame().getActionManager().getOrCreateActionOrSpell(name, unit);
            actions.put(i, action);
        }
        return actions;
    }

    public void update(FeatSpace space, FeatSpaceData data) {
        space.setFeatMap(createActives(space.getOwner(), data));
    }

    public FeatSpace.ActiveSpaceMeta createMeta(FeatSpace space) {
        FeatSpace.ActiveSpaceMeta meta = new FeatSpace.ActiveSpaceMeta(space.getName(), NewRpgEnums.FEAT_SPACE_SKIN.lite, false, false);
        return meta;
    }
}
