package libgdx.anims.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechExecutor;
import eidolons.game.core.ActionInput;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.game.exploration.story.cinematic.Cinematics;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.system.libgdx.datasource.AnimContext;
import libgdx.anims.*;
import eidolons.content.consts.VisualEnums.ANIM_PART;
import libgdx.anims.construct.AnimConstructor;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.std.sprite.CustomSpriteAnim;
import eidolons.content.consts.GraphicData;
import libgdx.bf.grid.cell.BaseView;
import libgdx.screens.dungeon.DungeonScreen;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.GameplayOptions;
import eidolons.system.options.OptionsMaster;
import main.ability.effects.Effect;
import main.content.enums.entity.ActionEnums;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.system.EventCallback;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/9/2017.
 * Animation does not support ZOOM!
 */
public class AnimMaster extends Group {

    static private boolean off;
    private static AnimMaster instance;
    private static Float animationSpeedFactor;

    private final FloatingTextLayer floatTextLayer;
    private final EffectAnimMaster effectMaster;
    private final EventAnimMaster eventMaster;
    private final AnimDrawMaster drawer;
    private final ActionAnimMaster actionMaster;
    private final BuffAnimMaster buffMaster;

    //animations will use emitters, light, sprites, text and icons
    private AnimMaster() {
        instance = this;
        effectMaster = new EffectAnimMaster(this);
        eventMaster = new EventAnimMaster(this);
        buffMaster = new BuffAnimMaster();
        actionMaster = new ActionAnimMaster(this);
        addActor(drawer = new AnimDrawMaster(this));
        addActor(floatTextLayer = new FloatingTextLayer());

    }

    public static boolean isOn() {
        if (CoreEngine.isGraphicsOff())
            return false;
        return !off;
    }

    public static AnimMaster getInstance() {
        if (instance == null)
            instance = new AnimMaster();
        return instance;
    }

    public static boolean isSmoothStop(Anim anim) {
        return anim.getPart() != VisualEnums.ANIM_PART.CAST; //TODO anim Review - was it too long? on vfx side..

    }

    public static boolean isAnimationOffFor(Obj sourceObj, BaseView baseView) {
        //        if (true )
        //            return false;
        if (!ExplorationMaster.isExplorationOn())
            return false;
        if (sourceObj.isMine()) {
            return false;
        }
        if (baseView != null)
            if (!baseView.isVisible())
                return true;
        if (sourceObj instanceof DC_Obj)
            if (!sourceObj.isMine())
                return !VisionHelper.checkVisible((DC_Obj) sourceObj, false);

        return false;
    }

    public static boolean isPreconstructEventAnims() {
        return false;
    }

    public static float speedMod() {
        if (Cinematics.ON) {
            return Cinematics.ANIM_SPEED;
        }
        if (animationSpeedFactor == null) {
            animationSpeedFactor = (float) OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.SPEED) / 100;
        }
        return animationSpeedFactor;
    }

    public static void setAnimationSpeedFactor(float speedFactor) {
        animationSpeedFactor = speedFactor;
    }

    protected static boolean isMustWaitForAnim(ActionInput action) {
        if (action != null) {
            if (instance.getDrawer().findAnimation(action.getAction()) != null) {
//                if (instance.getDrawer().leadAnimation.getActive() == action.getAction())
                {
                    return true;
                }
            }
            return false;
        }
        return instance.isDrawing();
    }

    public static void waitForAnimations(ActionInput action) {
        if (action == null) {
            action = Core.getGame().getLoop().getLastActionInput();
        }
        if (action == null) {
            return;
        }
        int maxTime = getMaxAnimWaitTime(action);
        //*speed ?
        int period = getAnimWaitPeriod();
        if (!ExplorationMaster.isExplorationOn()) {
            waitCombatMode(maxTime, period, action);
        } else {
            waitExploreMode(maxTime, period, action);
        }

    }

    private static void waitCombatMode(int maxTime, int period, ActionInput action) {

        int waitTime = 0;
        int speed = 1 + OptionsMaster.getGameplayOptions().getIntValue(GameplayOptions.GAMEPLAY_OPTION.GAME_SPEED);

        if (action.getAction().getOwnerUnit().isMine()) {
            maxTime = maxTime / 2;
        }
        maxTime = maxTime * 100 / speed;
//        minTime
        int minTime = maxTime / 2;

        String show = "Wait " +
                action.getAction().getOwnerUnit().getNameIfKnown() +
                "..";
        main.system.auxiliary.log.LogMaster.log(LogMaster.ANIM_DEBUG, "Wait for anim to draw; max =" + maxTime);
        main.system.auxiliary.log.LogMaster.log(LogMaster.ANIM_DEBUG, "Min =" + minTime);
        while ((isMustWaitForAnim(action) && waitTime < maxTime) ||
                waitTime < minTime) {
            WaitMaster.WAIT(period);
            //update something? add "." to something?
            waitTime += period;
            System.out.print('.');
            EUtils.showInfoText(show);
            show += ".";
        }

        boolean control =

                OptionsMaster.getGameplayOptions().getBooleanValue(GameplayOptions.GAMEPLAY_OPTION.INPUT_BETWEEN_TURNS)
                        ||
                        OptionsMaster.getGameplayOptions().getBooleanValue(GameplayOptions.GAMEPLAY_OPTION.SPACE_BETWEEN_TURNS);
        if (!DungeonScreen.getInstance().getGridPanel().getActiveCommentSprites().isEmpty()) {
            control = true;
        }
        if (!control)
        {
            EUtils.showInfoText("... And Go");
        }
        else {
            if (!action.getAction().getOwnerUnit().isMine()) {
                if (checkActionControlled(action)) {
//                    if (OptionsMaster.getGameplayOptions().getBooleanValue(GameplayOptions.GAMEPLAY_OPTION.SPACE_BETWEEN_TURNS)) {
//                   TODO      SpeechExecutor.run("wait pass=200(5000);");
//                    } else
                        SpeechExecutor.run("wait input= ;");

                }
//                return;
            }
        }
    }

    private static boolean checkActionControlled(ActionInput action) {
        if (action.getAction().isTurn()) {
            return false;
        }
        if (action.getAction().getName().contains("Reload")) {
            return false;
        }
        return !action.getAction().getName().equalsIgnoreCase("Wait");
    }


    private static void waitExploreMode(int maxTime, int period, ActionInput action) {
        int waitTime = 0;
        int minTime = maxTime / 2;
        while ((isMustWaitForAnim(action) && waitTime < maxTime) ||
                waitTime < minTime) {
            WaitMaster.WAIT(period);
            //update something? add "." to something?
            waitTime += period;
            main.system.auxiliary.log.LogMaster.log(LogMaster.ANIM_DEBUG, "Waited for anim to draw: " + waitTime);

        }
    }

    protected static int getMaxAnimWaitTime(ActionInput action) {
        switch (action.getAction().getName()) {
//            case DC_ActionManager.STD_SPEC_ACTIONS.Wait.toString():
            case "Wait":
                return 0;
        }

        if (action.getAction().getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MODE) {
            return 0;
        }
        if (ExplorationMaster.isExplorationOn()) {
            return OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.MAX_ANIM_WAIT_TIME);
        }
        return OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.MAX_ANIM_WAIT_TIME_COMBAT);
    }

    protected static int getAnimWaitPeriod() {
        return 50;
    }

    protected static void waitForAnimationsOld() {
        Integer MAX_ANIM_TIME =
                OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.MAX_ANIM_WAIT_TIME);
        if (MAX_ANIM_TIME != null) {
            if (MAX_ANIM_TIME > 0) {
                if (AnimMaster.getInstance().isDrawing()) {
                    WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.ANIMATION_QUEUE_FINISHED, MAX_ANIM_TIME);
                }
            }
        }
    }

    public static void onCustomAnim(String spritePath, boolean centered, int loops, Runnable runnable) {
        //play mode
        //blend mode

    }

    public static void onCustomAnim(String spritePath, Runnable runnable) {
        onCustomAnim(new SimpleAnim(spritePath, runnable));
    }

    public static void onCustomAnim(GraphicData data, String vfx,String spritePath, Runnable runnable) {
        onCustomAnim(new SimpleAnim(data, vfx, spritePath, runnable));
    }
    public static void onCustomAnim(SimpleAnim anim) {
        Gdx.app.postRunnable(anim::startAsSingleAnim);
    }

    public void setOff(boolean off) {
        AnimMaster.off = off;
    }


    public void bindEvents() {
        floatTextLayer.bindEvents();
        GuiEventManager.bind(GuiEventType.MOUSE_HOVER, p -> {
            if (!isOn()) {
                return;
            }
            if (!(p.get() instanceof Unit)) {
                return;
            }
            try {
                buffMaster.mouseHover((Unit) p.get());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        });
        GuiEventManager.bind(GuiEventType.SHOW_SPRITE, p -> {
//            AnimConstructor.createSpriteAnim(path);
            try {
                List z = (List) p.get();
                String path = (String) z.get(0);
                ActiveObj active = (ActiveObj) z.get(1);
                new CustomSpriteAnim(active, path).startAsSingleAnim();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        });
        GuiEventManager.bind(GuiEventType.SHOW_SPRITE_SUPPLIER, p -> {
//            AnimConstructor.createSpriteAnim(path);
            Supplier<SpriteAnimation> sup = (Supplier<SpriteAnimation>) p.get();
            new CustomSpriteAnim(Core.getMainHero().getLastAction(), sup.get()).startAsSingleAnim();
        });

        GuiEventManager.bind(GuiEventType.ACTION_INTERRUPTED, p -> {
            if (!isOn()) {
                return;
            }
            drawer.interrupt();
        });
//        GuiEventManager.bind(GuiEventType.ACTION_BEING_RESOLVED, p -> {
        //            CompositeAnim animation = constructor.getOrCreate((DC_ActiveObj) portrait.getVar());

//        });

        GuiEventManager.bind(GuiEventType.PARRY, p -> {
            List args = (List) p.get();
            Ref context = (Ref) args.get(0);
            CompositeAnim animation = AnimConstructor.getParryAnim((WeaponItem) args.get(1)
                    , (ActiveObj) args.get(2));
            drawer.add(animation);
//            if (getParallelDrawing()) {
//                animation.start(context);
//            }
        });
        GuiEventManager.bind(GuiEventType.ACTION_RESOLVES, p -> {

            if (!isOn()) {
                return;
            }
            ActionInput input = (ActionInput) p.get();
            try {
                actionMaster.initActionAnimation(input.getAction(), (AnimContext) input.getContext());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        });
        GuiEventManager.bind(GuiEventType.INGAME_EVENT, p -> {
            if (!isOn()) {
                return;
            }
            try {
                eventMaster.initEventAnimation((Event) p.get());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        });
        GuiEventManager.bind(GuiEventType.CUSTOM_ANIMATION, p -> {
            CustomSpriteAnim anim = (CustomSpriteAnim) p.get();
            customAnimation(anim);

        });
        GuiEventManager.bind(GuiEventType.EFFECT_APPLIED, p -> {
                    if (!isOn()) {
                        return;
                    }
                    try {
                        effectMaster.initEffectAnimation((Effect) p.get());
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }

                }
        );


    }

    public void customAnimation(CustomSpriteAnim anim) {
        try {
            anim.startAsSingleAnim();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            anim.getOnDone().call(anim.getCallbackParam());
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        eventMaster.checkPendingEventAnimations();
    }

    public boolean isDrawing() {
        return drawer.isDrawing();
    }

    public boolean isDrawingPlayer() {
        return drawer.isDrawingPlayer();
    }

    public FloatingTextLayer getFloatTextLayer() {
        return floatTextLayer;
    }

    public EffectAnimMaster getEffectMaster() {
        return effectMaster;
    }

    public EventAnimMaster getEventMaster() {
        return eventMaster;
    }

    public AnimDrawMaster getDrawer() {
        return drawer;
    }

    public ActionAnimMaster getActionMaster() {
        return actionMaster;
    }

    public BuffAnimMaster getBuffMaster() {
        return buffMaster;
    }

    public static CompositeAnim getParentAnim(Ref ref) {
        if (ref.getActive() == null) {
            //TODO
        }
        if (ref.isTriggered()) {
            return getTriggerParentAnim(ref.getEvent());
        }
        return AnimConstructor.getCached(ref.getActive());
    }

    private static CompositeAnim getTriggerParentAnim(Event event) {
        CompositeAnim root = AnimConstructor.getCached(event.getRef().getActive());
        if (root == null) {
            main.system.auxiliary.log.LogMaster.log(LogMaster.ANIM_DEBUG, "getTriggerParentAnim failed for: " + event);
            return null;
        }
        ANIM_PART part = EventAnimCreator.getPartToAttachTo(event);
        List<Animation> list = new ArrayList<>();
        if (root.getAttached().get(part) != null) {
            list.addAll(root.getAttached().get(part));
        }
        if (root.getTimeAttached().get(part) != null) {
            list.addAll(root.getTimeAttached().get(part));
        }
        int i = 0;
        for (Animation e : list) {
            if (e instanceof CompositeAnim) {
                return (CompositeAnim) e;
            }
            CompositeAnim a = new CompositeAnim();
            a.add(
                    part
                    , e);
            if (e.getDelay() == 0) {
                root.getAttached().get(part).set(i, a);
            } else {
                root.getTimeAttached().get(part).set(i, a);
            }
            return a;
        }
        return root;
    }

    public void add(CompositeAnim animation) {
        getDrawer().add(animation);
    }

    public boolean isParallelDrawing() {
        return getDrawer().getParallelDrawing();
    }

    public void setParallelDrawing(Boolean aBoolean) {
        getDrawer().setParallelDrawing(aBoolean);
    }

    public void addAttached(Animation a) {
        drawer.addAttached(a);
    }

    public CustomSpriteAnim spriteAnim(String path, Coordinates coordinates) {
        return spriteAnim(path, coordinates, null , null);
    }
    public CustomSpriteAnim spriteAnim(String path, Coordinates coordinates,
                                       EventCallback callback, EventCallbackParam param) {
        CustomSpriteAnim anim = new CustomSpriteAnim(null, path);
        anim.setOrigin(coordinates);
        anim.onDone(callback, param);
        Core.onThisOrGdxThread(()->
            customAnimation(anim));
        return anim;
    }
}
