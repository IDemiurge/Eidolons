package libgdx.anims.std;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.ability.effects.oneshot.move.MoveEffect;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.puzzle.art.ArtPuzzle;
import eidolons.game.core.master.EffectMaster;
import libgdx.anims.AnimData;
import libgdx.anims.actions.MoveByActionLimited;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.bf.GridMaster;
import libgdx.bf.grid.cell.BaseView;
import libgdx.bf.grid.cell.UnitGridView;
import libgdx.particles.spell.SpellVfx;
import libgdx.screens.ScreenMaster;
import libgdx.screens.dungeon.DungeonScreen;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SoundOptions.SOUND_OPTION;
import main.entity.Entity;
import main.game.bf.Coordinates;
import main.system.EventCallbackParam;
import main.system.GuiEventType;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LOG_CHANNEL;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

import static main.system.auxiliary.log.LogMaster.ANIM_DEBUG;
import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 1/14/2017.
 */
public class MoveAnimation extends ActionAnim {


    static boolean on = true;
    private MoveByActionLimited action;
    private Unit unit;

    public MoveAnimation(Entity active, AnimData params) {
        super(active, params);
        if (!ListMaster.isNotEmpty(EffectMaster.getEffectsOfClass(getActive(),
         MoveEffect.class))) // for teleports, telekinesis etc
        {
            unit = (Unit) getRef().getTargetObj();
        }
        unit = (Unit) getRef().getSourceObj();
    }

    public static boolean isOn() {
        return on;
    }

    public static void setOn(boolean on) {
        MoveAnimation.on = on;
    }

    protected MoveByActionLimited getAction() {
        //        new Pool.Poolable()
        if (action == null) {
            action = new MoveByActionLimited();
        } else {
            action.reset();
        }
        setDuration(0.6f);
        float x = getDestination().x - getOrigin().x;
        if (x > 160) {
            x = GridMaster.CELL_W;
        }
        float y = getDestination().y - getOrigin().y;
        if (y > 160) {
            y = GridMaster.CELL_H;
        }
        action.setAmount(x
         ,         y);

        action.setDuration(getDuration());

        return action;
    }

    public void initPosition() {
        origin = GridMaster
         .getVectorForCoordinate(getOriginCoordinates(), false, true);
        log(LOG_CHANNEL.ANIM_DEBUG,
         this + " origin: " + origin);
        destination = GridMaster
         .getVectorForCoordinate(getDestinationCoordinates(), false, true);
        log(ANIM_DEBUG,
         this + " destination: " + destination);

        defaultPosition = getDefaultPosition();
    }

    @Override
    public List<SpriteAnimation> getSprites() {
        return new ArrayList<>();
    }

    @Override
    public List<SpellVfx> getEmitterList() {
        return new ArrayList<>();
    }

    public void playSound() {
        if (OptionsMaster.getSoundOptions().getBooleanValue(SOUND_OPTION.FOOTSTEPS_OFF))
            return;
        DC_SoundMaster.playMoveSound(getActive().getOwnerUnit());
    }

    @Override
    public void start() {
        try {
            super.start();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        Unit unit = (Unit) getRef().getSourceObj();
        if (!ListMaster.isNotEmpty(EffectMaster.getEffectsOfClass(getActive(),
         MoveEffect.class)))
            unit = (Unit) getRef().getTargetObj();
        UnitGridView actor = (UnitGridView) ScreenMaster.getGrid().getViewMap()
         .get(unit);

        if (actor.isStackView()) {
            DungeonScreen.getInstance().getGuiStage().getTooltips().getStackMaster().stackOff();
        }

        if (!ScreenMaster.getGrid().detachUnitView(unit)) {
            return;
        }

        //        DungeonScreen.getInstance().getGridStage().addActor(actor);
        //        actor.setPosition(getX(), getY());
        getAction().setStartPointY(actor.getY());
        getAction().setStartPointX(actor.getX());
        actor.addAction(getAction());
        action.setTarget(actor);

if (isGhostMoveOn()){
    ScreenMaster.getGrid().showMoveGhostOnCell(unit);
    ScreenMaster.getGrid().resetCell(unit.getLastCoordinates());
}
//        GuiEventManager.trigger(GuiEventType.CELL_SHOW_MOVE_GHOST, unit);
    }

    protected boolean isGhostMoveOn() {
        if (unit.getGame().getDungeonMaster().getPuzzleMaster().getCurrent() instanceof ArtPuzzle) {
            return false;
        }
        return unit.isPlayerCharacter();
    }

    @Override
    public boolean tryDraw(Batch batch) {
        return super.draw(batch);
    }

    @Override
    public boolean draw(Batch batch) {
        return super.draw(batch);
    }

    @Override
    public Coordinates getOriginCoordinates() {
        //        if (!ListMaster.isNotEmpty(EffectMaster.getEffectsOfClass(getActive(),
        //         MoveEffect.class))) // for teleports, telekinesis etc
        //            return getRef().getTargetObj().getCoordinates();
        //        return super.getOriginCoordinates();
        MoveEffect e = (MoveEffect) EffectMaster.getFirstEffectOfClass(getActive(),
         MoveEffect.class);
        return e.getOrigin();

    }


    @Override
    public float getPixelsPerSecond() {
        return 160;
//        return CoreEngine.isIDE() ? CoreEngine.isFastMode() ? 2350 : 1950 : 1750;
    }

    //getSpeed()
    //getTrajectory()

    //    @Override
    //    public List<Pair<GuiEventType, EventCallbackParam>> getEventsOnStart() {
    //        return Arrays.asList(new ImmutablePair<>(UNIT_STARTS_MOVING, new EventCallbackParam(unit)));
    //    }

    @Override
    public Coordinates getDestinationCoordinates() {


        MoveEffect e = (MoveEffect) EffectMaster.getFirstEffectOfClass(getActive(),
         MoveEffect.class); //TODO could be 2+?
        return e.getDestination();
        //            if (e.getDirection() != null) {
        //                return ref.getSourceObj().getCoordinates().getAdjacentCoordinate
        //                 (DirectionMaster.getDirectionByFacing(FacingMaster.getFacing(ref.getSourceObj()),
        //                  e.getDirection()));
        //            }
        //            if (e.getTemplate() != null)
        //                return
        //             DC_Game.game.getMovementManager().getTemplateMoveCoordinate(e.getTemplate(),
        //              FacingMaster.getFacing(ref.getSourceObj()), ref.getSourceObj(), ref);
        //            return super.getDestinationCoordinates();
    }

    @Override
    public List<Pair<GuiEventType, EventCallbackParam>> getEventsOnFinish() {
        return new ArrayList<>();
        //         Arrays.asList(new ImmutablePair<>(UNIT_MOVED, new EventCallbackParam(unit)));
    }

    @Override
    public void finished() {
        super.finished();
        ScreenMaster.getGrid().unitViewMoved((BaseView) getActor());


    }

}
