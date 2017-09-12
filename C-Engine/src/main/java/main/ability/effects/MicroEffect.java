package main.ability.effects;

import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.game.core.game.MicroGame;
import main.system.graphics.ANIM;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.launch.CoreEngine;
import main.system.text.EntryNodeMaster;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;
import main.system.text.LogEntryNode;

import java.util.List;

public abstract class MicroEffect extends EffectImpl {
    protected MicroGame game;

    public MicroEffect() {
        super();
    }

    @Override
    public boolean apply() {

        boolean result = super.apply();
// FOR PHASE ANIMATIONS
        if (CoreEngine.isPhaseAnimsOn())
        if (result) {
            if (!isContinuousWrapped() || !isApplied()) {
                if (!isAnimationDisabled()) {
                    if (getAnimation() != null)
                    // if (!getAnimation().isVisible()) {
                    // TODO wait till all resolved?
                    // more exceptions?
                    {

						/*
                         * DelayedEffect will come here
						 * Entry not yet added
						 * Pending
						 *
						 */
                        if (getTrigger() != null) {
                            cleanAnimation();
                            // this ends up giving me a delay...
                            // of course - the delayed effect must have been
                            // activated *AFTER* ...
                            // solution?

                            // TODO how to retain only *newly added phases*,
                            // e.g. PARAM_MOD?
                            ENTRY_TYPE entryType = EntryNodeMaster
                                    .getEntryTypeForEvent(getTrigger().getEventType());
                            if (entryType != null) {
                                Boolean first_last_custom = null;
                                LogEntryNode entry = game.getLogManager().getLogEntryNode(
                                        first_last_custom, entryType);
                                if (entry != null) {
                                    List<PHASE_TYPE> animPhasesToPlay = getPhaseTypesForLink(
                                            entryType, getAnimation());
                                    entry.setAnimPhasesToPlay(animPhasesToPlay);
                                    entry.setLinkedAnimation(getAnimation());
                                } else {
                                    game.getLogManager().addPendingAnim(entryType, getAnimation());
                                }
                            }
                        }
                        try {
                            if (!checkAnimDisabledForAction(getActiveObj())) {
                                if (!getAnimation().isPending()) {
                                    if (!getAnimation().isFinished()) {
                                        getAnimation().start();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<PHASE_TYPE> getPhaseTypesForLink(ENTRY_TYPE entryType, ANIM animation) {
        // TODO Auto-generated method stub
        return null;
    }

    private boolean checkAnimDisabledForAction(ActiveObj activeObj) {
        if (activeObj == null) {
            return false;
        }

        return activeObj.isAttackGeneric();

    }

    private void cleanAnimation() {
        if (!CoreEngine.isPhaseAnimsOn())
            return ;
        AnimPhase lastPhase = getAnimation().getPhases().isEmpty() ? null : getAnimation()
                .getPhases().get(getAnimation().getPhases().size() - 1);
        // getActiveObj().initAnimation();
        // setAnimation(getActiveObj().getAnimation());

        ANIM newAnim = getAnimation().cloneAndAdd();
        // if (lastPhase!=null )
        // newAnim.setPhaseFilter(new
        // ListMaster<PHASE_TYPE>().getList(lastPhase.getType()));

        if (lastPhase != null) {
            try { // TODO clone phase?
                // if (lastPhase.getType() !=
                // getAnimation().getPhases().get(
                // getAnimation().getPhases().size() - 1).getType())
                newAnim.addPhase(lastPhase);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        newAnim.start();

        // ListMaster.removeIndicesAllExcept(getAnimation().getPhases(),
        // getAnimation().getPhases().size() - 1);
    }

    public void wrapInBuffPhase(Object... args) {
        if (getAnimation() != null) {
            getAnimation().addPhaseArgs(true, PHASE_TYPE.BUFF, args);
        }
    }

    public boolean isAnimationDisabled() {
        return false;
    }

    public void initLayer() {
        // if (active.getIntParam(LAYER) != 0)
        // if (target instanceof ItemObj)

        super.initLayer();
    }

    public MicroGame getGame() {
        return game;
    }

    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
        this.game = (MicroGame) ref.getGame();
    }

}
