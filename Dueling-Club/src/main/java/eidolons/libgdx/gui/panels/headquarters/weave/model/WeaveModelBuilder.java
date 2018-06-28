package eidolons.libgdx.gui.panels.headquarters.weave.model;

import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.weave.Weave;
import eidolons.libgdx.gui.panels.headquarters.weave.actor.WeaveActorBuilder;
import eidolons.libgdx.gui.panels.headquarters.weave.model.classes.ClassWeave;
import eidolons.libgdx.gui.panels.headquarters.weave.model.skills.SkillWeave;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.values.properties.G_PROPS;
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
public class WeaveModelBuilder {

    public static Weave buildHeroGraph(Unit hero, boolean skills) {
        WeaveDataNode root = createHeroNode(hero, skills);
        Weave graph = skills ? new SkillWeave(root) : new ClassWeave(root);
        graph.setUserObject(HqDataMaster.getHeroDataSource(hero));
        //base node is 'hero'?
        //each mastery is a tree
        graph.init();
        WeaveActorBuilder.build(graph);

        return graph;
    }

    private static WeaveDataNode createHeroNode(Unit hero, boolean skills) {
        return new WeaveDataNode(hero.getImagePath(), hero.getName());
    }

    public static List<Weave> buildAllGraphs(boolean skills) {
        return null;
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
        WeaveDataNode root = new WeaveDataNode(getRootImagePath(rootArg), getRootDescription(rootArg));
        root.setChildren(children);
        return root;
    }

    private static String getRootImagePath(Object rootArg) {
        if (rootArg instanceof MASTERY) {
            return ImageManager.getValueIconPath(((MASTERY) rootArg).getParam());
        }
        return null;
    }

    private static String getRootDescription(Object rootArg) {
        return null;
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


}
