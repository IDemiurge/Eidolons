package eidolons.libgdx.gui.panels.headquarters.weave.model;

import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.weave.Weave;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveSpace.WEAVE_VIEW_FILTER;
import eidolons.libgdx.gui.panels.headquarters.weave.actor.WeaveActorBuilder;
import eidolons.libgdx.gui.panels.headquarters.weave.model.classes.ClassWeave;
import eidolons.libgdx.gui.panels.headquarters.weave.model.skills.SkillWeave;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.images.ImageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 6/25/2018.
 * <p>
 * based on radial?
 * <p>
 * rotate the link
 */
public abstract class WeaveModelBuilder {

    public static List<Weave> buildAllGraphs(WEAVE_VIEW_FILTER filter, Unit hero, boolean skills) {
        return skills? new SkillWeaveBuilder(filter).buildAll(hero) :
         new ClassWeaveBuilder().buildAll(hero);
    }
    private static WeaveDataNode createHeroNode(Unit hero, boolean skills) {
        return new WeaveDataNode(hero.getImagePath(), hero.getName(), null );
    }
    public static Weave buildHeroGraph(Unit hero, boolean skills) {
        WeaveDataNode root = createHeroNode(hero, skills);
        Weave graph = skills ? new SkillWeave(root, false) : new ClassWeave(root, false);
        graph.setUserObject(HqDataMaster.getHeroDataSource(hero));
        //base node is 'hero'?
        //each mastery is a tree
        graph.init();
        WeaveActorBuilder.build(graph);

        return graph;
    }

    protected abstract WeaveDataNode createGroupNode(String sub);
    protected abstract String[] getWeaveGroups() ;


    protected Vector2 getWeavePosition(int i) {
        return WeaveActorBuilder.getWeavePosition(i);
    }
    public static WeaveDataNode buildTreeModel(Object rootArg, List<ObjType> data, boolean skill) {
        List<WeaveDataNode> children = new ArrayList<>();
        for (ObjType sub : data) {
            if (!sub.isUpgrade()) {
                WeaveDataNode root = new WeaveDataNode(sub, skill);
                buildTreeNodesModel(root, data, skill);
                children.add(root);
            }
        }
        WeaveDataNode root = new WeaveDataNode(getRootImagePath(rootArg), getRootDescription(rootArg), null );
        root.setChildren(children);
        return root;
    }


    private static void buildTreeNodesModel(WeaveDataNode node, List<ObjType> data, boolean skill) {
        List<ObjType> children = data.stream().filter(type -> type.getProperty(G_PROPS.BASE_TYPE)
         .equalsIgnoreCase(node.getType().getName())).collect(Collectors.toList());
        List<WeaveDataNode> childNodes = new ArrayList<>();
        for (ObjType sub : children) {
            WeaveDataNode root = new WeaveDataNode(sub, skill);
            buildTreeNodesModel(root, data, skill);
            childNodes.add(root);
            root.setParent(node);
        }
        node.setChildren(childNodes);

    }


    private static String getRootImagePath(Object rootArg) {
        if (rootArg instanceof MASTERY) {
            return ImageManager.getValueIconPath(((MASTERY) rootArg).getParam());
        }
        if (rootArg instanceof CLASS_GROUP) {
            return DataManager.getType(rootArg.toString(), DC_TYPE.CLASSES).getImagePath();
        }
        return null;
    }

    private static String getRootDescription(Object rootArg) {
        return null;
    }
}
