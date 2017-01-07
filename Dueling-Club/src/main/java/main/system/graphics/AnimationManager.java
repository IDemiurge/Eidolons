package main.system.graphics;

import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.content.PARAMS;
import main.content.parameters.G_PARAMS;
import main.content.parameters.Param;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.game.battlefield.options.UIOptions;
import main.swing.components.battlefield.DC_BattleFieldGrid;
import main.swing.components.obj.CellComp;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.datatypes.DequeImpl;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;
import main.system.options.AnimationOptions.ANIMATION_OPTION;
import main.system.options.OptionsMaster;
import main.system.text.SmartText;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
// Overpaint on the entire bf_grid? 
// play sounds here! 
// threading
//move animation - leave action icon behind

public class AnimationManager {

    public static final int THUMBNAIL_SIZE = 32;
    private static final int ACTION_CONST = 200;
    private static final int DAMAGE_CONST = 175;
    private static final float DEFAULT_OVERLAY_FONT_SIZE = 18;
    private final Point OBJ_COMP_CENTER = new Point(getDefaultObjCompX(), getDefaultObjCompY());
    DequeImpl<Animation> animations = new DequeImpl<>();
    DequeImpl<Animation> archivedAnimations = new DequeImpl<>();
    Map<Obj, List<Ref>> modifiedValues = new ConcurrentHashMap<>();
    List<Animation> pendingCameraAdjustmentAnims = new LinkedList<>();
    private boolean waiting;
    private UIOptions options;
    private int damageDelay;
    private int speed;
    private int actionDelay;
    private int animationStatus;
    private Map<Obj, CellComp> map;
    private DC_Game game;
    private Coordinates offset;
    private Coordinates bufferedOffset;
    private Animation lastThumbnail;
    private List<Animation> tempAnims = new LinkedList<>();
    private boolean changed;

    public AnimationManager(DC_Game game) {
        this.game = game;
        options = game.getUiOptions();
        map = new HashMap<>();
        initParameters();
    }

    private int getDefaultObjCompX() {
        return (GuiManager.getSquareCellSize() - GuiManager.getSmallObjSize()) / 2;
    }

	/*
     * perhaps break it into multiple phases - attackActivated, attackProceeds, attackFinished
	 * indeed, this would seem reasonable for all action-anims! 
	 * >> click-thru to skip phases
	 * >> click on any of the anim-items to getOrCreate fast-info
	 * >> synchronize sounds
	 * >> overlay with damage/mod animation 
	 * 
	 */

    private int getDefaultObjCompY() {
        return (GuiManager.getSquareCellSize() - GuiManager.getSmallObjSize()) / 2;
    }

    private void initParameters() {
        // speed = options.getAnimationSpeed();
        speed = 10;
        damageDelay = DAMAGE_CONST * speed;
        actionDelay = ACTION_CONST * speed;
    }

    // TODO
    public void paintCalledOnBfGrid() {
        for (Animation anim : new LinkedList<>(animations)) {
            if (!anim.isAutoHandled())
                continue;
            if (!anim.isStarted())
                anim.start();
            // else if (anim.checkTime())
            // animations.remove(anim);
        }
    }

    public boolean isStackAnimOverride(Coordinates coordinates) {
        for (Animation anim : animations)
            if (anim.isDrawReady())
                if (anim.isStackDrawingOn()) {
                    if (anim.getTargetCoordinates().equals(coordinates))
                        return true;
                }
        return false;
    }

    public boolean updateAnimations() {
        if (!changed)
            changed = cleanAnimations();

        if (CoreEngine.isSwingOn())
            updatePoints();
        // DequeImpl<Animation> animsToDraw = new DequeImpl<>(animations);
        // animsToDraw.addAll(tempAnims); drawn manually!
        for (Animation anim : animations) {
            if (anim.isPending())
                anim.run();

            if (CoreEngine.isSwingOn())
                checkOverlapping(anim);
        }
        // drawThumbnails()
        if (changed) {
            changed = !changed;
            GuiEventManager.trigger(GuiEventType.UPDATE_PHASE_ANIMS, null);
            return true;
        }
        return changed;
    }

    public void drawAnimations(Graphics bfGraphics) {
        updateAnimations();
        // will phase have time to init via timer.start()?
        for (Animation anim : animations)
            anim.draw(bfGraphics);
        // check overlapping !
        // anim.getTarget()
    }

    public boolean cleanAnimations() {

        boolean changed = false;
        for (Animation anim : new LinkedList<>(animations)) {
            if (anim.isFinished())
                if (!anim.isPaused())
                    if (!anim.isThumbnail())
                        if (!anim.isReplay()) {
                            removeAnimation(anim);
                            changed = true;
                        }
        }
        return changed;
    }

    public void removeAnimation(Animation animation) {
        animations.remove(animation);
        archivedAnimations.add(animation);
    }

    private void checkOverlapping(Animation anim) {
        for (Animation anim1 : animations) {
            int i = 0;
            if (anim != (anim1))
                if (anim.isVisible())
                    if (anim.getTargetCoordinates().equals(anim1.getTargetCoordinates())) {
                        if (!isOffsetForOverlap()) {
                            anim = getAnimBelow(anim, anim1);
                            anim.setThumbnail(true);
                            anim.setThumbnailIndex(i);
                            i++;
                            continue;
                        }

                        int offset = GuiManager.getCellWidth() / 4;
                        for (Boolean[] bools : BooleanMaster.getBoolArrayCombinatorics2D()) {
                            boolean source_target_all = bools[0];
                            boolean x = bools[1];
                            if (anim.overlapsFully(anim1, source_target_all, x)) {
                                addOffsets(anim, anim1, offset, source_target_all, x);
                            }
                        }
                    }
        }
    }

    public boolean isOffsetForOverlap() {
        return OptionsMaster.getAnimOptions().getBooleanValue(ANIMATION_OPTION.OFFSET_FOR_OVERLAP);
    }

    private Animation getAnimBelow(Animation anim, Animation anim1) {
        // default priorities? what are the overlapping cases? TODO
        return anim1;
    }

    private void addOffsets(Animation anim, Animation anim1, int offset, boolean source_target_all,
                            boolean x) {
        if (anim.getOffset(source_target_all, x) == 0)
            anim.addOffset(source_target_all, x, offset);
        else
            anim1.addOffset(source_target_all, x, offset);

        if (Math.abs(anim1.getOffset(source_target_all, x)) < offset)
            anim1.addOffset(source_target_all, x, offset);
        else
            anim.addOffset(source_target_all, x, offset);
    }

    public Coordinates updatePoints() {
        bufferedOffset = game.getBattleField().getGrid().getOffsetCoordinate();
        List<Animation> anims = new LinkedList<>();
        for (Animation anim : animations) {
            if (!anim.isDrawReady())
                continue;
            anims.add(anim);
        }
        return updatePoints(anims);
    }

    public Coordinates updatePoints(List<Animation> anims) {
        for (Animation anim : anims) {

            boolean result = anim.updatePoints();
            if (!result) {
                if (anim.isCameraPanningOn()) {
                    pendingCameraAdjustmentAnims.add(anim);
                    // see what anims are beyond the edges for current offset
                }
                // find the minimum number of omitted anims?
                // usage cases - replaying mass-anims or single anims -
                // centering ;
            }
        }
        if (pendingCameraAdjustmentAnims.isEmpty())
            return bufferedOffset;
        offset = game.getBattleField().getGrid().getOffsetCoordinate();
        if (offset == null)
            offset = new Coordinates(0, 0);
        int y = offset.y;
        int x = offset.x;
        int y1 = offset.y;
        int x1 = offset.x;
        int minNumber = getMinNumberOfAnimsOutOfBounds(y, x, y1, x1, true);
        Coordinates bufferedCoordinate = offset;
        int minNumber2 = getMinNumberOfAnimsOutOfBounds(y, x, y1, x1, false);
        if (minNumber < minNumber2) {
            offset = bufferedCoordinate;
        }
        adjustOffset(offset);
        return bufferedOffset;
    }

    private int getMinNumberOfAnimsOutOfBounds(int y, int x, int y1, int x1, boolean positive) {
        int minNumber = Integer.MAX_VALUE;
        // TODO consider the panning distance?
        int step = positive ? 1 : -1;
        while (MathMaster.compare(y, y1, !positive)) {
            x += step;
            if (MathMaster.compare(x, x1, positive)) {
                x = 0;
                y += step;
            }

            Coordinates c_offset = new Coordinates(x, y);

            adjustOffset(c_offset);
            LinkedList<Animation> list = new LinkedList<>(pendingCameraAdjustmentAnims);
            for (Animation anim : pendingCameraAdjustmentAnims) {
                boolean result = anim.updatePoints();
                if (result) {
                    list.remove(anim);
                }
            }
            int size = list.size();
            if (size < minNumber)
                minNumber = size;
            offset = c_offset;
        }
        return minNumber;
    }

    private void adjustOffset(Coordinates c_offset) {
        game.getBattleField().getGrid()
                .setOffsetCoordinate(new Coordinates(c_offset.x, c_offset.y));

    }

    public Animation cloneWithPhases(Animation anim, PHASE_TYPE... phaseTypes) {
        Animation newAnim = (Animation) anim.clone();
        List<PHASE_TYPE> phaseList = new LinkedList<>(Arrays.asList(phaseTypes));
        for (AnimPhase phase : anim.getPhases()) {
            if (phaseList.contains(phase.getType())) {
                newAnim.addPhase(new AnimPhase(phase.getType(), phase.getArgs()));
            }
        }
        return newAnim;
    }

    public MultiAnim wrapInMultiAnim(DC_ActiveObj action) {
        ANIM_TYPE type = getAnimTypeForAction(action);
        // Ref ref, ANIM_TYPE type, Object... args
        switch (type) {
            // case ATTACK:
            // return new MultiAttackAnim(action);
            case ACTION:
                return new MultiActionAnim(action);
            // case EFFECT:
            // return new MultiEffectAnim(ref, args);
            case ATTACK:
                return new MultiActionAnim(action);
            case BUFF:
                return new MultiActionAnim(action);
            case DAMAGE:
                return new MultiActionAnim(action);
            case EFFECT:
                return new MultiActionAnim(action);
            // TODO

        }

        return null;
    }

    public Animation getAnimation(Object key) {
        Animation anim = getAnimation(key, animations);
        if (anim == null)
            anim = getAnimation(key, archivedAnimations);
        return anim;
    }

    public Animation getAnimation(Object key, DequeImpl<Animation> pool) {
        for (Animation anim : pool) {
            if (anim.getKey() != null)
                if (anim.getKey().equals(key))
                    return anim;
        }

        return null;
    }

    public void clearModValues() {
        modifiedValues.clear();
    }

    public void valueModified(Ref ref) {
        List<Ref> list = modifiedValues.get(ref.getTargetObj());
        if (list == null) {
            list = new LinkedList<>();
            modifiedValues.put(ref.getTargetObj(), list);
        }
        list.add(ref);
    }

    public void animateValuesModified(Obj target) {
        List<Ref> list = modifiedValues.get(target);
        if (list == null)
            return;
        int i = 0;
        for (Ref ref : list) {
            animateValueModification(ref, i);
            i++;
        }
    }

    // TODO ++ animate counter mods! use small icons too :)
    public void animateValueModification(Ref ref, int i) {
        if (ref.getValue() instanceof G_PARAMS)
            return;
        if (ref.getValue() instanceof Param)
            return;
        if (ref.getAmount() == null)
            return;
        PARAMS p = (PARAMS) ref.getValue();
        Color c = p.getColor();
        String text = ref.getAmount() + "";
        if (ref.getAmount() > 0)
            text = "+" + ref.getAmount();
        // TODO coordinates! by i
        Point pt = OBJ_COMP_CENTER;
        if (i != 0) {

        }

        addTextOverlay(text, ref, damageDelay, c, pt); // TODO increase for
        // bigger dmg?
    }

    public void damageDealt(int t_amount, int e_amount, Ref ref, DAMAGE_TYPE type, boolean lethal) {
        CellComp comp = getComp(ref.getTargetObj());
        if (lethal) {
            //comp.deadenIcon();
        }

        addTextOverlayCenter("-" + t_amount, ref, damageDelay, ColorManager.RED); // TODO
        // increase
        // for
        // bigger
        // dmg?
        if (ref.getObj(KEYS.ACTIVE) != null) {

            ImageIcon img = ref.getObj(KEYS.ACTIVE).getIcon();
            // if (ref.isPeriodic()) {
            if (ImageManager.isImage(ref.getValue(KEYS.IMAGE.toString()))) {
                img = ImageManager.getIcon(ref.getValue(KEYS.IMAGE.toString()));
            }
            if (ImageManager.isValidIcon(img))
                setOverlayingImage(ref.getTargetObj(), img.getImage());
        }
    }

    private void addTextOverlayCenter(String amount, Ref ref, int delay, Color c) {
        addTextOverlay(amount, ref, delay, c, OBJ_COMP_CENTER);
    }

    private void addTextOverlay(String amount, Ref ref, int delay, Color c, Point p) {
        CellComp comp = getComp(ref.getTargetObj());
        SmartText text = new SmartText(amount, c);
        text.setFont(FontMaster.getFont(FONT.AVQ, DEFAULT_OVERLAY_FONT_SIZE, Font.PLAIN));
        comp.addAnimOverlayingString(p, text);
        animate(delay, comp);
    }

    // action activated on source?
    // exception with std attacks - update weapon?
    public void actionResolves(DC_ActiveObj action, Ref ref) {
        ImageIcon img = action.getIcon();
        if (img == null)
            return;
        // actionResolves(img, ref); TODO old
    }

    public void actionResolves(ImageIcon img, Ref ref) {

        if (ref.getGroup() != null)
            for (Obj obj : ref.getGroup().getObjects()) {
                if (ListMaster.isNotEmpty(modifiedValues.get(obj)))
                    animateValuesModified(obj);
                else {
                    setOverlayingImage(obj, img.getImage());
                    animate(actionDelay, getComp(obj));
                }
            }
        else if (ref.getTargetObj() != null) {
            if (ListMaster.isNotEmpty(modifiedValues.get(ref.getTargetObj())))
                animateValuesModified(ref.getTargetObj());
            else {
                setOverlayingImage(ref.getTargetObj(), img.getImage());
                animate(actionDelay, getComp(ref.getTargetObj()));
            }
        } else
            return;
        clearModValues();
        // WaitMaster.waitForInput(WAIT_OPERATIONS.ANIMATION_FINISHED);

    }

    private void animate(final int delay, final CellComp comp) {
        new Thread
                // SwingUtilities.invokeLater
                (new Runnable() {

                    @Override
                    public void run() {
                        map.put(comp.getTopObjOrCell(), comp);

                        getGrid().refresh();
                        // main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG,
                        // comp + " animated");
                        WaitMaster.WAIT(delay);
                        // main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG,
                        // comp + " de-animated");
                        comp.removeAnimation();
                        comp.refresh();
                        //
                        // Chronos.mark(comp.getTopObjOrCell().getName() +
                        // " animation, delay = " + delay);
                        getGrid().refresh();
                        // Chronos.logTimeElapsedForMark(comp.getTopObjOrCell().getName()
                        // + " animation, delay = " + delay);

                        map.remove(comp.getTopObjOrCell());
                        if (map.isEmpty())
                            WaitMaster.receiveInput(WAIT_OPERATIONS.ANIMATION_FINISHED, true);
                        else {
                            WaitMaster.WAIT(delay);
                            WaitMaster.receiveInput(WAIT_OPERATIONS.ANIMATION_FINISHED, true);
                        }

                    }
                }, comp.getTopObjOrCell().getName() + " animation").start();

    }

    private void addOverlayingImage(Obj obj, Point c, Image img) {
        CellComp objComponent = getComp(obj);
        objComponent.addAnimOverlayingImage(c, img);
    }

    private void setOverlayingImage(Obj obj, Image img) {
        CellComp objComponent = getComp(obj);
        objComponent.setCenterOverlayingImage(img);

    }

    private CellComp getComp(Obj obj) {
        // if (getGrid().getObjCompMap().getOrCreate(obj.getCoordinates()) == null) {
        // CellComp component = (CellComp) getGrid()
        // .getPassableObjMap().getOrCreate(obj.getCoordinates());
        // if (component != null)
        // return component;
        // component = (CellComp) getGrid().getCellCompMap().getOrCreate(
        // obj.getCoordinates());
        // return component;
        // }
        return getGrid().getCellCompMap().get(obj.getCoordinates());
    }

    public DequeImpl<Animation> getAnimations() {
        return animations;
    }

    private DC_BattleFieldGrid getGrid() {
        return game.getBattleField().getGrid();
    }

    private ANIM_TYPE getAnimTypeForAction(DC_ActiveObj action) {
        switch (action.getActionGroup()) {
            case ATTACK:
                return ANIM_TYPE.ATTACK;
            case MOVE:
                return ANIM_TYPE.MOVE;
            case MODE:
                return ANIM_TYPE.BUFF;
            case ITEM:
            case SPECIAL:
            case TURN:
            case HIDDEN:
            case SPELL:
                return ANIM_TYPE.ACTION; // TODO additional checks!
            // return ANIM_TYPE.EFFECT;
        }
        return ANIM_TYPE.ACTION;
    }

    public void updateLastThumbnail(Animation animation) {
        return;
        // removeLastThumbnail();
        // setLastThumbnail(animation);
    }

    private void setLastThumbnail(Animation animation) {
        if (animation != null) {
            main.system.auxiliary.LogMaster.log(1, "setLastThumbnail " + animation);
            lastThumbnail = animation;
            lastThumbnail.setThumbnail(true);
        }
    }

    private void removeLastThumbnail() {
        if (lastThumbnail != null) {
            main.system.auxiliary.LogMaster.log(1, "removeLastThumbnail " + lastThumbnail);
            lastThumbnail.setThumbnail(false);
            removeAnimation(lastThumbnail);
        }
    }

    public List<Animation> getTempAnims() {
        return tempAnims;
    }

    public Animation getActionAnimation(DC_ActiveObj action) {
        if (action.isAttack())
            return null;
        if (action.getRef().getGroup() != null)
            if (action.getRef().getGroup().getObjects().size() > 1) {
                return wrapInMultiAnim(action);
            }
        ActionAnimation animation = new ActionAnimation(action);
        switch (action.getActionGroup()) {
            case ATTACK:
                animation = new AttackAnimation(action);
                break;
            case TURN:
            case HIDDEN:
            case MOVE:
                break;
            case ITEM:
            case MODE:
            case SPECIAL:
            case SPELL:
                animation = new EffectAnimation(action);
        }
        return animation;
    }

    public void newAnimation(Animation animation) {
        if (animation == null)
            return;
        if (animations.contains(animation))
            return;
        animations.add(animation);
        changed = true;

    }

    public enum MOUSE_ITEM implements MouseItem {
        CONTROL_BACK, CONTROL_FORWARD, TOOLTIP, SUB_PHASE, THUMBNAIL;

        @Override
        public Object getArg() {
            return null;
        }

        @Override
        public MOUSE_ITEM getType() {
            return this;
        }

        @Override
        public Point getPoint() {
            return null;
        }

        @Override
        public void setPoint(Point point) {
        }

        @Override
        public Rectangle getRectangle() {
            return null;
        }

        @Override
        public void setRectangle(Rectangle rectangle) {

        }
    }

    public enum ANIM_TYPE {
        ACTION, ATTACK, NO_TARGET, DAMAGE, BUFF, ITEM, MOVE, SPELL_SINGLE, EFFECT,
    }

    public interface MouseItem {
        Object getArg();

        MOUSE_ITEM getType();

        Point getPoint();

        void setPoint(Point point);

        Rectangle getRectangle();

        void setRectangle(Rectangle rectangle);
    }

}
