package eidolons.libgdx.gui.panels.headquarters.weave.actor;

import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.headquarters.weave.Weave;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveTree;
import eidolons.libgdx.gui.panels.headquarters.weave.model.WeaveDataNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 6/25/2018.
 * <p>
 * animations for Weave
 * > new tree: displace other trees?
 * I could leave it unsupported kind of
 * <p>
 * in Weave, you can only unlock a new tree while being away from the center
 * then the new weave with fade in over the old...
 * <p>
 * as long as we sort by 'date'...
 * <p>
 * viewModes
 * - available only
 * -
 */
public class WeaveActorBuilder {
    static float baseAngle;
    static float currentAngle;
    private static int maxAngle;
    private static Map<WeaveDataNode, WeaveNodeActor> nodeMap;



    public static void build(Weave graph) {
        nodeMap = new HashMap<>();
        List<WeaveTree> trees = graph.getTrees();
        int angleStep = maxAngle = 360 / trees.size();
        //place trees

        //coordinate map
        GroupX mainGroup = new GroupX();
        for (WeaveTree sub : trees) {
            baseAngle += angleStep;
            GroupX treeGroup = buildTree(sub, baseAngle);

            mainGroup.addActor(treeGroup);
            //centering and fitting
            //            x = Math.sin(baseAngle);
            //            GdxMaster.centerWidth()
            //            treeGroup.setPosition(x, y);
        }


    }

    private static GroupX buildTree(WeaveTree sub, float baseAngle) {
        GroupX treeGroup = new GroupX();
        WeaveDataNode node = sub.getRoot();
        //angle offset
        addBranchLayer(treeGroup, null, node, node.getChildren(), baseAngle, 0);
        return treeGroup;
    }

    private static WeaveNodeActor addBranchLayer(GroupX treeGroup,
                                                 WeaveDataNode parent,
                                                 WeaveDataNode node,
                                                 List<WeaveDataNode> children, float angle,  int level ) {
        level++;
        float step = 10 + maxAngle / 2 / children.size();
        angle = angle - children.size() / 2 * step;
        for (WeaveDataNode sub : children) {
            angle += step;
            WeaveNodeActor nodeActor = addBranchLayer(treeGroup, node, sub, sub.getChildren(), angle, level);
            nodeActor.setParentNode(node);
        }
        WeaveNodeActor nodeActor = new WeaveNodeActor(node.getType().getImagePath());
        WeaveLinkActor link = new WeaveLinkActor(nodeMap.get(parent), nodeMap.get(node));
        link.setRotation(angle);

        nodeActor.setLink(link);
        treeGroup.addActor(link);
        treeGroup.addActor(nodeActor);
        Vector2 v = getCoordinateOnX(level);
        v.rotate(angle);
        nodeMap.put(node, nodeActor);
        nodeActor.setPosition(v.x, v.y);
        return nodeActor;
    }

    private static Vector2 getCoordinateOnX(int level) {
        //        int x= getOffsetX(); //from the center center, by default link + root node
        //        x+=getLinkLength()*level;
        //        x+=(getNodeLength()-1)*level;
        return new Vector2(200 * level, 0);
    }


}
