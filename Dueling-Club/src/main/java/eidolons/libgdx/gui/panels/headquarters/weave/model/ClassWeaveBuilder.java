package eidolons.libgdx.gui.panels.headquarters.weave.model;

import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.weave.Weave;
import eidolons.libgdx.gui.panels.headquarters.weave.actor.WeaveActorBuilder;
import eidolons.libgdx.gui.panels.headquarters.weave.model.classes.ClassWeave;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;
import main.system.auxiliary.EnumMaster;
import main.system.images.ImageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 6/28/2018.
 */
public class ClassWeaveBuilder extends WeaveModelBuilder {
    public List<Weave> buildAll(Unit hero) {
        List<Weave> graphs = new ArrayList<>();
        for (String sub : getWeaveGroups()) {

            WeaveDataNode root = createGroupNode(sub);
            Weave graph =  new ClassWeave(root, true);
            graph.setUserObject(HqDataMaster.getHeroDataSource(hero));
            graph.init();
            WeaveActorBuilder.build(graph);
            graphs.add(graph);
        }
        return graphs;
    }

    @Override
    protected WeaveDataNode createGroupNode(String sub) {
        String img = ImageManager.getClassGroupPath(sub);
        String descr = "";
        Object arg = new EnumMaster<CLASS_GROUP>().retrieveEnumConst(CLASS_GROUP.class, sub);
        return new WeaveDataNode(img, descr, arg);
    }

    @Override
    protected String[] getWeaveGroups() {
        return new String[]{
         "FIGHTER",
         "KNIGHT",
         "ROGUE",
         "TRICKSTER",
         "RANGER",
         "HERMIT",
         "ACOLYTE",
         "WIZARD",
         "SORCERER",
        };
    }
}
