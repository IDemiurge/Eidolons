package eidolons.entity.feat.spaces;

import eidolons.content.PROPS;
import eidolons.entity.feat.Feat;
import eidolons.entity.unit.Unit;
import main.content.enums.entity.NewRpgEnums;
import main.content.values.properties.PROPERTY;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;

import java.util.LinkedHashMap;
import java.util.Map;

//All this does is create usable FS objs from string data on heroes
public class FeatSpaceInitializer implements IFeatSpaceInitializer {
    public static final PROPS  featSpacePropsSpell = PROPS.SPELL_SPACES;
    public static final PROPS  featSpaceProps = PROPS.COMBAT_SPACES;
    public static final int MAX_SLOTS = 6;

    @Override
    public FeatSpaces createFeatSpaces(Unit unit, boolean spellSpaces) {
        FeatSpaces spaces = new FeatSpaces(unit, spellSpaces);
        int index = 0; // ???

        PROPERTY prop = spellSpaces ? featSpacePropsSpell : featSpaceProps;

        for (String string : ContainerUtils.openContainer(
                    unit.getProperty(prop), FeatSpaceData.getInstanceSeparator())) {
                FeatSpaceData data = new FeatSpaceData(string);
                FeatSpace space = createSpace(index++, unit, spaces, data, spellSpaces);
                spaces.add(space);
            }
        //TODO
        // addItemSpaces(unit, spaces);
        return spaces;
    }

    private FeatSpace createSpace(int i, Unit unit, FeatSpaces spaces, FeatSpaceData data, boolean spellSpaces) {
        NewRpgEnums.FeatSpaceType type = data.getType();
        // mods = getGlobalMods(unit, type); //TODO
        Map<Integer, Feat> actives = createFeats(unit, data);
        FeatSpace space = new FeatSpace(i, data.getName(), unit, type, data.getMode(), actives);
        return space;
    }

    private Map<Integer, Feat> createFeats(Unit unit, FeatSpaceData value) {
        Map<Integer, Feat> actions = new LinkedHashMap<>();
        for (int i = 0; i < MAX_SLOTS; i++) {
            String name = value.getFeat(i);
            if (StringMaster.isEmpty(name)) {
                continue;
            }
                //TODO Status: Outline
            // DC_TYPE featType = value.getFeatType(i); YAML!
            // Feat feat = factory.getOrCreate(featType, name, unit);
            // actions.put(i, feat);
        }
        return actions;
    }

    public void update(FeatSpace space, FeatSpaceData data) {
        //this is heavy
        space.setFeatMap(createFeats(space.getOwner(), data));
    }

    public FeatSpace.ActiveSpaceMeta createMeta(FeatSpace space) {
        FeatSpace.ActiveSpaceMeta meta = new FeatSpace.ActiveSpaceMeta(space.getName(), NewRpgEnums.FeatSpaceSkin.lite, false, false);
        return meta;
    }
}
