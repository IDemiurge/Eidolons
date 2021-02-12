package libgdx.gui.panels.headquarters.tabs.tree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.PARAMS;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMaster;
import libgdx.gui.controls.radial.RadialContainer;
import libgdx.gui.controls.radial.RadialMenu;
import libgdx.gui.panels.headquarters.HqPanel;
import libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.gui.tooltips.DynamicTooltip;
import libgdx.gui.tooltips.SmartClickListener;
import libgdx.shaders.GrayscaleShader;
import libgdx.texture.TextureCache;
import eidolons.system.libgdx.datasource.HeroDataModel;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.system.EventCallbackParam;
import main.system.EventType;
import main.system.GuiEventManager;
import main.system.math.MathMaster;
import main.system.text.TextParser;

import java.util.ArrayList;
import java.util.List;

import static main.system.GuiEventType.RADIAL_MENU_CLOSE;

/**
 * Created by JustMe on 5/9/2018.
 */
public abstract class SlotSelectionRadialMenu extends RadialMenu {
    private static HtNode activeNode;
    protected ObjectMap<List, List> cache = new ObjectMap<>();
    protected HqHeroDataSource dataSource;
    private int tier;
    private int slot;

    public SlotSelectionRadialMenu() {
        super();
        GuiEventManager.bind(getEvent(), p -> {
            if (ExplorationMaster.isExplorationOn())
                try { //TODO HC trees can fail otherwise, fix properly!
                    triggered(p);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
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
            if (HqPanel.getActiveInstance() != null)
                if (ready)
                    close();
        });
    }

    public void selected(ObjType type) {
        if (!ExplorationMaster.isExplorationOn())
            return;
        HqDataMaster.operation(dataSource,
                getOperation(), type, tier, slot);
    }

    protected abstract HeroDataModel.HERO_OPERATION getOperation();

    @Override
    protected Vector2 getInitialPosition() {
//        Vector2 pos = super.getInitialPosition();
//        pos.lerp(new Vector2(-GdxMaster.getWidth() / 2, -GdxMaster.getHeight() / 2), 0.35f);
//        setPosition(pos.x, pos.y);
//        return pos;
//        return screenToLocalCoordinates(pos);
//        stageToLocalCoordinates(pos);
//        Vector2 v = new Vector2(GdxMaster.getWidth() / 2 - getWidth() * 3,
//                GdxMaster.getHeight() / 2 - getHeight() / 2);
//        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
//        return v.lerp(mouse, 0.3f);


        float w = getWidth();
        float h = getHeight();

        float x;
        float y;

        Vector2 v = activeNode.localToAscendantCoordinates(
                activeNode.getFirstParentOfClass(HqTreeTab.class), new Vector2(0, 0));

        x = MathMaster.minMax(v.x,
                w / 2, GdxMaster.getWidth() - w);
        x = x - x / 6 - 100;
        y = MathMaster.minMax(v.y,
                h / 2, GdxMaster.getHeight() - h);

        v = //stageToLocalCoordinates
                (new Vector2(x - 100, y));
        return v;
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        cache.clear();
        dataSource = (HqHeroDataSource) getUserObject();
    }

    @Override
    protected void triggered(EventCallbackParam obj) {
        List params = (List) obj.get();
        List<ObjType> available = (List<ObjType>) params.get(0);
        tier = (int) params.get(1);
        slot = (int) params.get(2);
        List<RadialContainer> nodes = getNodes(available);
        init(nodes);
        open();
    }

    protected List<RadialContainer> getNodes(List<ObjType> available) {
        List<RadialContainer> list = cache.get(available);
        if (list == null) {
            list = createNodes(available);
        }
        return list;
    }

    @Override
    protected void updatePosition() {
        super.updatePosition();
        Vector2 v = getInitialPosition();
        setPosition(v.x, v.y);
        adjustPosition();
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

    protected List<RadialContainer> createNodes(List<ObjType> available) {
        List<RadialContainer> list = new ArrayList<>();
        for (ObjType type : available) {
            String reason = getReqReason(type);
            boolean valid = reason == null;
            TextureRegion region = TextureCache.getOrCreateR(getImagePath(type));
            RadialContainer node = new RadialContainer(getIconSize(), region, () -> {

                if (valid)
                    selected(type);
                else {
                    //floating text?
                }
            });
            node.setUserObject(type);
            if (!valid)
                node.setShader(GrayscaleShader.getGrayscaleShader());
            Ref ref = dataSource.getEntity().getRef().getCopy();
            ref.setID(KEYS.INFO, type.getId());
            ref.setID(KEYS.SOURCE, Eidolons.getMainHero().getId());

            DC_FeatObj infoFeat = new DC_FeatObj(type, ref);
            node.addListener(new DynamicTooltip(() -> {
                        ref.setID(KEYS.SKILL, infoFeat.getId());
                        ref.setID(KEYS.INFO, infoFeat.getId());

                        String text = type.getProperty(G_PROPS.TOOLTIP);
                        text += "\n" + TextParser.parse(type.getDescription( ),
                                ref,  TextParser.VARIABLE_PARSING_CODE, TextParser.TOOLTIP_PARSING_CODE, TextParser.INFO_PARSING_CODE);

                        text += (isFree() ? ""
                                : (valid ? "\nXp Cost:" + type.getIntParam(PARAMS.XP_COST)
                                : ("\n" + reason)));
                        return text;

                    }).getController()
            );

            node.setCustomRadialMenu(this);
            list.add(node);

        }
        return list;
    }

    protected abstract int getIconSize();

    protected abstract String getImagePath(ObjType type);

    protected boolean isFree() {
        return false;
    }

    protected abstract String getReqReason(ObjType type);

    @Override
    protected void adjustPosition() {
        //       TODO
        if (activeNode == null) {
            return;
        }
        float w = getWidth();
        float h = getHeight();

        float x;
        float y;

        Vector2 v = activeNode.localToAscendantCoordinates(
                activeNode.getFirstParentOfClass(HqTreeTab.class), new Vector2(0, 0));

        x = MathMaster.minMax(v.x,
                w / 2, GdxMaster.getWidth() - w);
        x = x - x / 6;
        y = MathMaster.minMax(v.y,
                h / 2, GdxMaster.getHeight() - h);

        v = //stageToLocalCoordinates
                (new Vector2(x - 200, y));

        ActionMaster.addMoveToAction(this, v.x, v.y, 1.5f);

    }

}

