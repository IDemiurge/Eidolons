package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.GridObject;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.bf.grid.cell.BaseView;
import eidolons.libgdx.bf.grid.cell.GridUnitView;
import eidolons.libgdx.gui.generic.GroupX;
import main.system.EventType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * independent graphical events
 * :: hit-style displacement
 * :: scale up/down (‘jump’)
 * :: fade-flip
 * :: colorize / shader-ize / postfx
 * :: gridObj / unitSprite manipulation
 * <p>
 * :: custom overlay anims - blood, fire, magic, ...
 * ++ via emitters as well!
 * :: leveraging the Animation framework
 */
public class GridViewAnimator {


    protected   GroupX animated;
    protected GridPanel gridPanel;


    public GridViewAnimator(GridPanel panel) {
        this.gridPanel = panel;
        for (VIEW_ANIM value : VIEW_ANIM.values()) {
            GuiEventManager.bind(value.event, p -> {
                if (p.get() instanceof List) {
                    handleAnim((GroupX) ((List) p.get()).get(0), value,
                            ((List) p.get()).get(1));
                } else
                  handleAnim(animated, value, p.get());
                main.system.auxiliary.log.LogMaster.dev("Grid Anim: " + value.toString() + " \n" + p.get());
            });
        }
        GuiEventManager.bind(GuiEventType.GRID_SET_VIEW, p -> {
            animated = findView(p.get());
        });
    }

    protected void handleAnim(GroupX animated, VIEW_ANIM value, Object o) {
        List args = new ArrayList<>();
        GraphicData data = null;
        if (o instanceof List) {
            args = new ArrayList<>((List) o);
        }
        if (o instanceof GraphicData) {
            data = (GraphicData) o;
        } else if (animated == null) {
            animated = findView(args.remove(0));
        }
        if (data == null) {
            for (Object arg : args) {
                data = new GraphicData(arg.toString());
            }
        }
        if (data == null) {
            data = getDefaultData(value);
        }

        try {
            animate(animated, value, data);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            animated = null;
        }
    }

    private GraphicData getDefaultData(VIEW_ANIM value) {
        switch (value) {
            case displace:
                return new GraphicData("dur:1f;y:40;x:24");
            case screen:
                return new GraphicData("dur:1f;alpha:1f");
        }
        return null;
    }

    public void animate(GridObject gridObj, GraphicData data) {
//data.getType().switch
    }

    public void animate(BaseView view, VIEW_ANIM value ) {
        animate(view.getPortrait(), value, getDefaultData(value));
    }
    public boolean animate(GroupX animated, VIEW_ANIM value, GraphicData data) {

        main.system.auxiliary.log.LogMaster.dev(animated.toString() + "'s Grid Anim handled: " + value.toString() + " \n" + data);
        switch (value) {
            case attached:
                return doAttached((GridUnitView) animated, data);
            case displace:
                return doDisplace(animated, data);
            case screen:
                return doScreen( (SuperActor) animated, data);
            case color:
                return doColor(animated, data);
        }
        return false;
    }

    protected boolean doAttached(GridUnitView animated, GraphicData data) {
        for (SpriteX sprite : animated.getOverlaySprites()) {
            doSpriteAnim(sprite, data);
        }
        for (SpriteX sprite : animated.getUnderlaySprites()) {
            doSpriteAnim(sprite, data);
        }
        return false;
    }

    protected void doSpriteAnim(SpriteX sprite, GraphicData data) {
        if (data.getValue(GraphicData.GRAPHIC_VALUE.alpha) != null) {
            doAlpha(sprite, data);
        }
    }

    protected boolean doAlpha(GroupX animated, GraphicData data) {
        AlphaAction action = new AlphaAction();
        float a = data.getFloatValue(GraphicData.GRAPHIC_VALUE.alpha);
        action.setAlpha(a);
        initTemporal(data, action);
        SequenceAction sequence = ActionMaster.getBackSequence(action);
        addAction(animated, sequence, data);
        return false;
    }

    protected boolean doColor(GroupX animated, GraphicData data) {
        ColorAction action = new ColorAction();
        Color color = GdxColorMaster.getColorByName(data.getValue(GraphicData.GRAPHIC_VALUE.color));
        action.setEndColor(color);
        action.setColor(animated.getColor());
        initTemporal(data, action);
        SequenceAction sequence = ActionMaster.getBackSequence(action);
        addAction(animated, sequence, data);
        return false;
    }

    protected boolean doScreen(SuperActor animated, GraphicData data) {
        float a = data.getFloatValue(GraphicData.GRAPHIC_VALUE.alpha);
        FloatAction floatAction = new FloatAction();
        initTemporal(data, floatAction);
        floatAction.setStart(animated.getScreenOverlay());
        floatAction.setEnd(a);
//        SequenceAction sequence = ActionMaster.getBackSequence(floatAction);
//        Action screen = new Action() {
//            @Override
//            public boolean act(float delta) {
//                int index = new ReflectionMaster<Integer>().
//                        getFieldValue("index", sequence, SequenceAction.class);
//                if (sequence.getActions().size <= index) {
//                    return true;
//                }
//                FloatAction current = (FloatAction) sequence.getActions().get(index);
//                if (current.getTime() >= current.getDuration()) {
//                    return true;
//                }
//                animated.setScreenOverlay(current.getValue());
//                return false;
//            }
//        };

        FloatAction screenNew = new FloatAction() {
            @Override
            protected void update(float percent) {
                super.update(percent);
                if (percent < 0.5f) {
                    animated.setScreenOverlay(2 * getValue());
                } else {
                    animated.setScreenOverlay(1.5f*a- 1.5f*getValue());
                }
            }
        };
        screenNew.setStart(  animated.getScreenOverlay());
        floatAction.setEnd(a);
        initTemporal(data, screenNew);
        addAction(animated, screenNew, data);

        return true;
    }

    protected GroupX findView(Object arg) {
        if (arg instanceof GridUnitView) {
            return ((GridUnitView) arg).getPortrait();
        }
        if (arg instanceof BattleFieldObject) {
            return   gridPanel.getViewMap().get(arg).getPortrait();
        }
        return null;
    }

    protected void initTemporal(GraphicData data, TemporalAction a) {
        a.setDuration(data.getFloatValue(GraphicData.GRAPHIC_VALUE.dur));
    }

    protected boolean doDisplace(GroupX animated, GraphicData data) {
        int dx = data.getIntValue(GraphicData.GRAPHIC_VALUE.x);
        int dy = data.getIntValue(GraphicData.GRAPHIC_VALUE.y);
        float dur = 1f;
        if (data.getFloatValue(GraphicData.GRAPHIC_VALUE.dur) > 0) {
            dur = data.getFloatValue(GraphicData.GRAPHIC_VALUE.dur);
        }
        SequenceAction sequence = ActionMaster.getDisplaceSequence(animated.getX(), animated.getY(), dx, dy, dur, false);
        addAction(animated, sequence, data);

        return true;
    }

    protected void addAction(GroupX animated, Action action, GraphicData data) {
        ActionMaster.addAction(animated, action);
        if (action instanceof SequenceAction) {
            for (Action a : ((SequenceAction) action).getActions()) {
                if (a instanceof TemporalAction)
                    setInterpolation((TemporalAction) a, data);
            }
        }
        if (action instanceof TemporalAction) {
            setInterpolation((TemporalAction) action, data);
        }
    }

    protected void setInterpolation(TemporalAction action, GraphicData data) {
        if (StringMaster.isEmpty(data.getValue(GraphicData.GRAPHIC_VALUE.interpolation))) {
            return;
        }
        Interpolation interpolation = ActionMaster.getInterpolation(data.getValue(GraphicData.GRAPHIC_VALUE.interpolation));
        action.setInterpolation(interpolation);
    }


    public enum VIEW_ANIM {
        displace(GuiEventType.GRID_DISPLACE),
        screen(GuiEventType.GRID_SCREEN),
        //        scale,
//        alpha,
        color(GuiEventType.GRID_COLOR),
        //        shader,
//        postfx,
//        sprite,
//        vfx,
        attached(GuiEventType.GRID_ATTACHED);

        VIEW_ANIM(EventType event) {
            this.event = event;
        }

        public EventType event;
    }

}
