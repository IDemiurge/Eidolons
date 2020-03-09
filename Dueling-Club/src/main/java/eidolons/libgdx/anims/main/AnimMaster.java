package eidolons.libgdx.anims.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechExecutor;
import eidolons.game.core.ActionInput;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.netherflame.boss.anim.BossAnimator;
import eidolons.game.module.netherflame.boss.entity.BossUnit;
import eidolons.game.module.netherflame.boss.sprite.BossView;
import eidolons.libgdx.anims.*;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.std.EventAnimCreator;
import eidolons.libgdx.anims.std.sprite.CustomSpriteAnim;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.GameplayOptions;
import eidolons.system.options.OptionsMaster;
import main.ability.effects.Effect;
import main.content.enums.entity.ActionEnums;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
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
    private BossAnimator bossAnimator;

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
        if (anim.getPart() == ANIM_PART.CAST) {
            return false; //TODO igg demo hack
        }
        if (anim.getOrigin().equals(anim.getDestination()))
            return true;
        return true;

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
                return !VisionManager.checkVisible((DC_Obj) sourceObj, false);

        return false;
    }

    public static boolean isPreconstructEventAnims() {
        return false;
    }

    public static float getAnimationSpeedFactor() {
        if (Cinematics.ON) {
            return Cinematics.ANIM_SPEED;
        }
        if (animationSpeedFactor == null) {
            animationSpeedFactor = new Float(OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.SPEED)) / 100;
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
            action = Eidolons.getGame().getLoop().getLastActionInput();
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

        if (action.getContext().getSourceObj() instanceof BossUnit) {
            maxTime = 2000;
            minTime = maxTime;
        }

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

    public static void onCustomAnim(SimpleAnim anim) {
        Gdx.app.postRunnable(() -> anim.startAsSingleAnim());
    }

    public void setOff(boolean off) {
        AnimMaster.off = off;
    }


    public void bindEvents() {
        DC_SoundMaster.bindEvents();
        floatTextLayer.bindEvents();
        GuiEventManager.bind(GuiEventType.BOSS_VIEW_CREATED, p -> {
            BossView b = (BossView) p.get();
            bossAnimator = new BossAnimator(b, this  );

        });
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
                DC_ActiveObj active = (DC_ActiveObj) z.get(1);
                new CustomSpriteAnim(active, path).startAsSingleAnim();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        });
        GuiEventManager.bind(GuiEventType.SHOW_SPRITE_SUPPLIER, p -> {
//            AnimConstructor.createSpriteAnim(path);
            Supplier<SpriteAnimation> sup = (Supplier<SpriteAnimation>) p.get();
            new CustomSpriteAnim(Eidolons.getMainHero().getLastAction(), sup.get()).startAsSingleAnim();
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
            CompositeAnim animation = AnimConstructor.getParryAnim((DC_WeaponObj) args.get(1)
                    , (DC_ActiveObj) args.get(2));
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
        GuiEventManager.bind(GuiEventType.INGAME_EVENT_TRIGGERED, p -> {
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
            try {
                anim.startAsSingleAnim();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                anim.getOnDone().call(anim.getCallbackParam());
            }

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

    public boolean getParallelDrawing() {
        return getDrawer().getParallelDrawing();
    }

    public void setParallelDrawing(Boolean aBoolean) {
        getDrawer().setParallelDrawing(aBoolean);
    }

    public void addAttached(Animation a) {
        drawer.addAttached(a);
    }

    public BossAnimator getBossAnimator() {
        return bossAnimator;
    }
}
