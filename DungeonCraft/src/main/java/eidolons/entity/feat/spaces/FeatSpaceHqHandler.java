package eidolons.entity.feat.spaces;

import eidolons.content.PROPS;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.unit.netherflame.HeroUnit;
import eidolons.netherflame.eidolon.heromake.passives.HeroClassMaster;
import eidolons.system.libgdx.datasource.HeroDataModel;
import main.content.enums.entity.ClassEnums;
import main.content.enums.entity.NewRpgEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.WeightMap;

import java.util.Map;

public class FeatSpaceHqHandler {
    /* For the Feat Space TAB where we put 'em together  */

    FeatSpaceInitializer initializer;
    Map<FeatSpace, FeatSpaceData> dataMap; // ???
    FeatSpaces spaces; //   result/outcome?
    HeroDataModel model;

    //for normal procedure upon hero-creation on any level
    public void prepare(HeroUnit hero) {
        String combat = "";
        String items = "";
        String spells = "";
        WeightMap<ClassEnums.CLASS_RANK> map = HeroClassMaster.getRankWeightMap(hero);

        for (ClassEnums.CLASS_RANK rank : map.keySet()) {
            // rank.isCombat();
        }

        hero.setProperty(PROPS.FEAT_SPACES_COMBAT, combat);
        hero.setProperty(PROPS.FEAT_SPACES_ITEMS, items);
        hero.setProperty(PROPS.FEAT_SPACES_SPELLS, spells);

    }
    //TODO Status: Outline
    public void save() {
        //is this something that could use YAML? Aye, if we could make DataUnit via Yaml...
            FeatSpaceData data = new FeatSpaceData("");
            // List<String> feats = ContainerUtils.openContainer(s);
            // for (int i = 0; i < MAX_SLOTS; i++) {
            //     if (feats.size() <= i) break;
            //     data.setFeat(i, feats.get(i));
            // }
            // data.setValue(NewRpgEnums.FeatSpaceValue.feats, s);
            String name = "";
            data.setValue(NewRpgEnums.FeatSpaceValue.name, name);
        String string= data.toString();
        model.setProperty(PROPS.COMBAT_SPACES, string);
    }

    public void upgrade(ActiveObj active) {
        updateGui();
    }

    private void updateGui() {
        GuiEventManager.trigger(GuiEventType.UPDATE_GUI); //TODO
    }

    public boolean move(ActiveObj active, FeatSpace space, int newIndex) {
        FeatSpaceData data = dataMap.get(space);
        String prev = data.getFeat(newIndex);
        if (!StringMaster.isEmpty(prev)) {
            int oldIndex = data.indexOf(active.getName());
            data.set(oldIndex, prev);
        }
        data.set(newIndex, active.getName());
        initializer.update(space, data);
        return true;
    }

    public boolean add(ActiveObj active, FeatSpace space) {

        return false;
    }

    public void remove(ActiveObj active, FeatSpace space) {

    }

}














