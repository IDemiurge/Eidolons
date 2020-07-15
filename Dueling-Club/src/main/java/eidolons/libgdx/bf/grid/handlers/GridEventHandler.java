package eidolons.libgdx.bf.grid.handlers;

import com.badlogic.gdx.Gdx;
import eidolons.entity.active.Spell;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.std.DeathAnim;
import eidolons.libgdx.anims.std.MoveAnimation;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.cell.BaseView;
import eidolons.libgdx.bf.grid.cell.HpBarView;
import eidolons.libgdx.bf.grid.cell.UnitGridView;
import eidolons.libgdx.bf.overlays.bar.HpBar;
import eidolons.libgdx.bf.overlays.bar.HpBarManager;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.effects.Effect;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.system.EventCallback;
import main.system.GuiEventManager;
import main.system.threading.WaitMaster;

import static main.game.logic.event.Event.STANDARD_EVENT_TYPE.*;
import static main.system.GuiEventType.*;

public class GridEventHandler extends GridHandler {

    public GridEventHandler(GridPanel panel) {
        super(panel);
    }

    @Override
    protected void bindEvents() {
        GuiEventManager.bind(INGAME_EVENT, onIngameEvent());

    }

    private EventCallback onIngameEvent() {

        return param -> {
            Event event = (Event) param.get();
            Ref ref = event.getRef();

            boolean caught = false;

            Event.EVENT_TYPE type = event.getType();
            if (type == EFFECT_HAS_BEEN_APPLIED) {
                Effect effect = event.getRef().getEffect();
                if (AnimConstructor.isAnimated(effect)) {
                    GuiEventManager.trigger(EFFECT_APPLIED, effect);
                }
                caught = true;
            } else if (type == UNIT_HAS_CHANGED_FACING
                    || type == UNIT_HAS_TURNED_CLOCKWISE
                    || type == UNIT_HAS_TURNED_ANTICLOCKWISE) {
                if ((ref.getObj(Ref.KEYS.TARGET) instanceof BattleFieldObject)) {
                    BattleFieldObject hero = (BattleFieldObject) ref.getObj(Ref.KEYS.TARGET);
                    BaseView view = getViewMap().get(hero);
                    if (view != null && view instanceof UnitGridView) {
                        UnitGridView unitView = ((UnitGridView) view);
                        unitView.updateRotation(hero.getFacing().getDirection().getDegrees());
                        if (hero instanceof Unit)
                            DC_SoundMaster.playTurnSound(hero);
                    }
                }
                caught = true;
            } else if (type == UNIT_HAS_FALLEN_UNCONSCIOUS
            ) {
                GuiEventManager.trigger(UNIT_GREYED_OUT_ON, ref.getSourceObj());
            } else if (type == UNIT_HAS_RECOVERED_FROM_UNCONSCIOUSNESS) {
                GuiEventManager.trigger(UNIT_GREYED_OUT_OFF, ref.getSourceObj());
            } else if (type == UNIT_HAS_BEEN_KILLED) {
                GuiEventManager.trigger(UNIT_GREYED_OUT_OFF, ref.getSourceObj());
                if (!DeathAnim.isOn() || ref.isDebug()) {
                    GuiEventManager.trigger(DESTROY_UNIT_MODEL, ref.getTargetObj());
                }
                caught = true;
            } else {

                BattleFieldObject object = (BattleFieldObject) ref.getSourceObj();

                if (type == UNIT_BEING_MOVED) {
                    if (!MoveAnimation.isOn())
                        grid.removeUnitView(object);
                    caught = true;

                    if (event.getRef().getActive() instanceof Spell) {
                        unitBeingMoved(object);
                    }

                } else if (type == UNIT_FINISHED_MOVING) {
                    unitMoved(object);

                    if (event.getRef().getActive() instanceof Spell) {
                        unitMovedForced(object, (Spell) event.getRef().getActive());
                    }
                    caught = true;
                } else if (type.name().startsWith("PARAM_BEING_MODIFIED")) {
                    caught = true;
                } else if (type.name().startsWith("PROP_")) {
                    caught = true;
                } else if (type.name().startsWith("ABILITY_")) {
                    caught = true;
                } else if (type.name().startsWith("EFFECT_")) {
                    caught = true;
                } else if (type.name().startsWith("PARAM_MODIFIED")) {
                    if (!HpBar.isResetOnLogicThread()) {
                        if (GuiEventManager.isBodyParam(type.getArg())) {
                            checkHpBarReset(object);
                            if (object.isPlayerCharacter()) {
                                checkBodyBarReset(object);
                            }
                        }
                        if (GuiEventManager.isSoulParam(type.getArg())) {
                            checkHpBarReset(object);
                            checkSoulBarReset(object);
                        }
                    }
                    caught = true;
                }
            }
            if (type == UNIT_HAS_BEEN_DEALT_PURE_DAMAGE) {
                if (!HpBar.isResetOnLogicThread())
                    checkHpBarReset(event.getRef().getTargetObj());
                caught = true;
            }

            if (!caught) {
                /*      System.out.println("catch ingame event: " + event.getType() + " in " + event.getRef());
                 */
            }
        };
    }

    public void unitMoved(Obj sourceObj) {
        if (!MoveAnimation.isOn() || AnimMaster.isAnimationOffFor(sourceObj,
                getViewMap().get(sourceObj))
                || sourceObj.getGame().getManager().getActiveObj() != sourceObj)
            //TODO EA check...
            //what about COUNTER ATTACK?!

            //move immediately
            grid.unitMoved((BattleFieldObject) sourceObj);
    }


    public void unitMovedForced(BattleFieldObject sourceObj, Spell active) {
        Coordinates c = sourceObj.getCoordinates();
        if (active.getName().contains("1Projection")) {
            sourceObj.setCoordinates(sourceObj.getLastCoordinates());
        }
        int n = 1765;
        if (active.getName().contains("Projection")) {
            n = 2765;
        }
        WaitMaster.doAfterWait(n, () -> {
            Gdx.app.postRunnable(() -> {
                if (active.getName().contains("1Projection")) {
                    sourceObj.setCoordinates(c);
                }
                grid.unitMoved(sourceObj);
                BaseView view = grid.getViewMap().get(sourceObj);
                view.fadeIn();
            });
        });
    }

    public void unitBeingMoved(BattleFieldObject sourceObj) {
        //              c = sourceObj.getCoordinates();
        //            sourceObj.setCoordinates(sourceObj.getLastCoordinates());
        BaseView view = grid.getViewMap().get(sourceObj);
        view.fadeOut();
        //        new MoveAnimation()

    }

    private void checkBodyBarReset(BattleFieldObject object) {
        GuiEventManager.trigger(UPDATE_MAIN_HERO);
    }

    private void checkSoulBarReset(BattleFieldObject object) {
        GuiEventManager.trigger(UPDATE_MAIN_HERO);
    }

    public void checkHpBarReset(Obj obj) {
        HpBarView view = (HpBarView) getViewMap().get(obj);
        if (view != null)
            if (view.getActor().isVisible())
                if (view.getHpBar() != null)
                    if (
                            !ExplorationMaster.isExplorationOn()
                                    || HpBarManager.canHpBarBeVisible((BattleFieldObject) view.getActor().getUserObject()))
                        view.resetHpBar();
    }

}
