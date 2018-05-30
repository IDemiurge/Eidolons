package eidolons.libgdx.gui.panels.headquarters.tabs.tree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.content.PARAMS;
import eidolons.game.core.EUtils;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.gui.controls.radial.RadialMenu;
import eidolons.libgdx.gui.controls.radial.RadialValueContainer;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HQ_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.shaders.GrayscaleShader;
import eidolons.libgdx.texture.TextureCache;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.system.EventCallbackParam;
import main.system.EventType;
import main.system.GuiEventManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.system.GuiEventType.RADIAL_MENU_CLOSE;

/**
 * Created by JustMe on 5/9/2018.
 */
public abstract class SlotSelectionRadialMenu extends RadialMenu {
    private static HtNode activeNode;
    protected Map<List, List> cache = new HashMap<>();
    protected HqHeroDataSource dataSource;

    public SlotSelectionRadialMenu() {
        super();
        GuiEventManager.bind(getEvent(), p -> {
            if (ExplorationMaster.isExplorationOn())
                triggered(p);
            else {
                EUtils.showInfoText("Cannot do this while in combat!");
            }
        });
    }

    public static HtNode getActiveNode() {
        return activeNode;
    }

    public static void setActiveNode(HtNode activeNode) {
        SlotSelectionRadialMenu.activeNode = activeNode;
    }

    protected abstract EventType getEvent();

    @Override
    protected void adjustPosition() {

//       TODO  if (activeNode == null) {
//            return;
//        }
//        float w = activeNode.getWidth();
//        float h = activeNode.getHeight();
//
//        float x = activeNode.getX() + w / 2;
//        float y = activeNode.getY() + h / 2;
//
//        Vector2 v = activeNode.localToAscendantCoordinates(activeNode.getFirstParentOfClass(HqTreeTab.class), new Vector2(0,0));
////        localToStageCoordinates(new Vector2(x, y));
//
//        w = getWidth();
//        h = getHeight();
//
//        x = MathMaster.minMax(v.x,
//         w / 2, GdxMaster.getWidth() - w);
//        y = MathMaster.minMax(v.y,
//         h / 2, GdxMaster.getHeight() - h);
//
//        v = stageToLocalCoordinates(new Vector2(x, y));
//        ActorMaster.addMoveToAction(this, v.x, v.y, 1.5f);

    }

    @Override
    protected void bindEvents() {
        addListener(new SmartClickListener(this) {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return event.getTarget() == SlotSelectionRadialMenu.this
                 || super.mouseMoved(event, x, y);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (event.getTarget() == null) {
                    close();
                }
                if (event.getTarget() == SlotSelectionRadialMenu.this) {
                    close();
                }
                if (event.getTarget() == closeButton) {
                    close();
                }
            }
        });
        GuiEventManager.bind(RADIAL_MENU_CLOSE, obj -> {
            if (HqPanel.getActiveInstance()!=null )
            if (ready)
                close();
        });
    }

    public void selected(ObjType type) {
        if (!ExplorationMaster.isExplorationOn())
            return;
        HqDataMaster.operation(dataSource,
         getOperation(), type);
    }

    protected abstract HQ_OPERATION getOperation();

    @Override
    protected Vector2 getInitialPosition() {
        Vector2 pos = super.getInitialPosition();
        setPosition(0, 0);
        return stageToLocalCoordinates(pos);
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        cache.clear();
        dataSource = (HqHeroDataSource) getUserObject();
    }

    @Override
    protected void triggered(EventCallbackParam obj) {
        List<ObjType> available = (List<ObjType>) obj.get();
        List<RadialValueContainer> nodes = getNodes(available);
        init(nodes);
        open();
    }

    protected List<RadialValueContainer> getNodes(List<ObjType> available) {
        List<RadialValueContainer> list = cache.get(available);
        if (list == null) {
            list = createNodes(available);
        }
        return list;
    }

    @Override
    protected void updatePosition() {
        super.updatePosition();
    }

    @Override
    protected boolean isMakeSecondRing(int size) {
        return super.isMakeSecondRing(size);
    }

    @Override
    protected float getMinCoef() {
        return super.getMinCoef();
    }

    @Override
    protected float getMaxCoef() {
        return super.getMaxCoef();
    }

    protected List<RadialValueContainer> createNodes(List<ObjType> available) {
        List<RadialValueContainer> list = new ArrayList<>();
        for (ObjType type : available) {
            String reason = getReqReason(type);
            boolean valid = reason == null;
            TextureRegion region = TextureCache.getOrCreateR(type.getImagePath());
            RadialValueContainer node = new RadialValueContainer(region, () -> {

                if (valid)
                    selected(type);
                else {
                    //floating text?
                }
            });
            if (!valid)
                node.setShader(GrayscaleShader.getGrayscaleShader());
            Ref ref = dataSource.getEntity().getRef().getCopy();
            ref.setID(KEYS.INFO, type.getId());
            node.addListener(new ValueTooltip(type.getName() +
              "\n" + type.getProperty(G_PROPS.TOOLTIP)
              + "\n" + type.getDescription(ref)
              + (isFree() ? "" :
             (valid
              ? "\nXp Cost:" + type.getIntParam(PARAMS.XP_COST)
              : ("\n" +reason))
             )).getController()
            );

            node.setCustomRadialMenu(this);
            list.add(node);

        }
        return list;
    }

    protected boolean isFree() {
        return false;
    }

    protected abstract String getReqReason(ObjType type);

}

