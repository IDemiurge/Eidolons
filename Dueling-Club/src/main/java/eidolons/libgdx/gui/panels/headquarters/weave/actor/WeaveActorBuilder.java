package eidolons.libgdx.gui.panels.headquarters.weave.actor;

import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.headquarters.weave.Weave;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveTree;
import eidolons.libgdx.gui.panels.headquarters.weave.model.WeaveDataNode;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;

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
    private static int segmentWidth= TextureCache.getOrCreate(Images.WEAVE_LINK).getWidth()
     + 64;
    private static int totalWidth;


    public static void build(Weave graph) {
        nodeMap = new HashMap<>();
        List<WeaveTree> trees = graph.getTrees();
        int angleStep = maxAngle = 360 / trees.size();
        //place trees

        //coordinate map
        GroupX mainGroup = new GroupX();
        float maxSize=0;
        int n=0;
        for (WeaveTree sub : trees) {
            baseAngle += angleStep;
            GroupX treeGroup = buildTree(graph.getCoreNode(), sub, baseAngle);
            sub.add(treeGroup);
            if (treeGroup.getWidth()>maxSize)
                maxSize = treeGroup.getWidth();

            Vector2 v = getTreePosition(n++);
//            v.rotate(baseAngle);
            treeGroup.rotateBy(baseAngle);
            mainGroup.addActor(sub);
            treeGroup.setPosition(v.x, v.y);
        }
        graph.add(mainGroup);
        graph.setWidth(maxSize);
        graph.setHeight(maxSize);
    }

    private static GroupX buildTree(WeaveDataNode coreNode, WeaveTree sub, float baseAngle) {
        totalWidth=0;
        GroupX treeGroup = new GroupX();
        WeaveDataNode node = sub.getRoot();
        //angle offset
        addBranchLayer(treeGroup, coreNode, node, node.getChildren(), baseAngle, 0,0);
      treeGroup.setWidth(totalWidth);

      return treeGroup;
    }

    private static WeaveNodeActor addBranchLayer(GroupX treeGroup,
                                                 WeaveDataNode parent,
                                                 WeaveDataNode node,
                                                 List<WeaveDataNode> children,
                                                 float angle,  int level, int n ) {
        level++;
        float step = 10 + maxAngle / 2 / (1+children.size());
        angle = angle - children.size() / 2 * step;
        int i =0;
        for (WeaveDataNode sub : children) {
            angle += step;
            WeaveNodeActor nodeActor = addBranchLayer(treeGroup, node, sub,
             sub.getChildren(), angle, level, i++);
            nodeActor.setParentNode(node);
        }
        WeaveNodeActor nodeActor = new WeaveNodeActor(node.getImagePath());
        WeaveLinkActor link = new WeaveLinkActor(nodeMap.get(parent), nodeMap.get(node));

        nodeActor.setLink(link);
        treeGroup.addActor(link);
        treeGroup.addActor(nodeActor);
        Vector2 v = getNodePosition(level,parent==null||parent.getChildren()==null
         ? 0: parent.getChildren().size(), n);

        nodeActor.rotateBy(-baseAngle);
        link.rotateBy(baseAngle-angle);
        Vector2 rotated = v.rotate(baseAngle - angle);

        nodeMap.put(node, nodeActor);
        nodeActor.setPosition(rotated.x, rotated.y);

        rotated = new Vector2(v.x-link.getWidth() , v.y).rotate(baseAngle - angle);
        link.setPosition(rotated.x , 32);
        totalWidth+=segmentWidth;
        return nodeActor;
    }

    private static Vector2 getNodePosition(int children, int level, int n) {
        //        int x= getOffsetX(); //from the center center, by default link + root node
        //        x+=getLinkLength()*level;
        //        x+=(getNodeLength()-1)*level;
//        int coef = children - n*2-1;
//        Math.toDegrees(coef);
        return new Vector2(segmentWidth * level, 0);
    }


    public static Vector2 getNineSquareSegmentPosition(int i, int segmentSize) {

        int ring=i/9+1;
        int sector = i%9;
        switch (sector){

        }
        int xCoef=0;
        int yCoef=0;
        if (sector%3==1)
            yCoef=1;
        if (sector%3==2)
            yCoef=-1;
        if (sector>2)
            xCoef=1;
        if (sector>5)
            xCoef=-1;

        float x=xCoef*segmentSize*ring;
        float y=yCoef*segmentSize*ring;
        return new Vector2(x,y);
    }
    public static Vector2 getTreePosition(int i) {
        return getNineSquareSegmentPosition(i, segmentWidth );
    }
    public static Vector2 getWeavePosition(int i) {
        return getNineSquareSegmentPosition(i, segmentWidth*5);
    }
}
