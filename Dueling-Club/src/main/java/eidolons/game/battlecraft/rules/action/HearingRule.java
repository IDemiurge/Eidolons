package eidolons.game.battlecraft.rules.action;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.core.EUtils;
import eidolons.game.core.game.DC_Game;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.math.roll.RollMaster;
import main.content.enums.GenericEnums;
import main.content.enums.rules.VisionEnums;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.math.PositionMaster;
import main.system.sound.AudioEnums;
import main.system.text.LogManager;

import java.util.HashMap;
import java.util.Map;

import static main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL.*;

// sound wave img?
public class HearingRule implements ActionRule {
    DC_Game game;
    private boolean playerWasHeard;
    private final Map<Unit, DIRECTION> map = new HashMap<>();

    public HearingRule(DC_Game game) {
        this.game = game;
    }

    // how to ensure there is no repeat on this?
    // critical success - become detected?
    public void checkLogged(DC_ActiveObj action, Unit listener) {

        if (!isHearingRequired(listener, action))
            return;

        String type = null;
        if (action.isMove() || action.isTurn()) {
            type = "move";
        }
        if (action.isSpell()) {
            type = "murmur";
        }
        if (action.isAttackAny()) {
            type = "strike";
        }
        if (type == null) {
            return;
        }
//type = "murmur" : "make strange sounds"

        Unit unit = action.getOwnerUnit();
        double dst = PositionMaster.getExactDistance(unit, listener) + 1;
        String suc = "(30-" + StringMaster.getValueRef(Ref.KEYS.SOURCE, PARAMS.NOISE) + ")" + "/" + dst;
        String fail = "10+" + StringMaster.getValueRef(Ref.KEYS.TARGET, PARAMS.PERCEPTION);
//        String suc = "("+StringMaster.getValueRef(Ref.KEYS.SOURCE, PARAMS.PERCEPTION) + "+10)"+ "/" +dst ;
//        String fail = "10+" +  StringMaster.getValueRef(Ref.KEYS.TARGET, PARAMS.NOISE);
        Ref ref = listener.getRef().getCopy();
        ref.setTarget(unit.getId());
        boolean result = RollMaster.roll(GenericEnums.ROLL_TYPES.HEARING, suc, fail, ref,
                "", "", false
        );//, "Hear me?", "source?");

        if (result) {
//            GuiEventManager.trigger(GuiEventType.SHOW_SPRITE, Sprites.SOUND, unit.getCoordinates());
            String descriptor = game.getVisionMaster().getHintMaster().getSoundHints(unit);
            DIRECTION direction = DirectionMaster.getRelativeDirection(listener, unit);
            String dstDescr = dst < 1.5f ? "up close" : dst < 4 ? "close by" : "some distance away";
            String quality = true ? "quietly" : "noisily"; //audibly

            String logged = listener +
                    " hears something " + StringMaster.wrapInBrackets(StringMaster.format(descriptor)) +
                    " " +
                    type +
                    " " +
                    quality +
                    " to the " +
                    DirectionMaster.getNSWE(direction) +
                    ", " + dstDescr;

            LogManager.LOGGING_DETAIL_LEVEL level = LogManager.LOGGING_DETAIL_LEVEL.ESSENTIAL;
            boolean repeat = false;
            if (unit.isMine()) {
                repeat = playerWasHeard;
                logged = "You have been heard!" + StringMaster.wrapInParenthesis(unit.getIntParam("noise") + " Noise level");
                DC_SoundMaster.playEffectSound(AudioEnums.SOUNDS.ALERT, listener);
                playerWasHeard = true;
            } else {
                repeat = map.get(unit) == direction; //TODO encapsulate all data
                map.put(unit, direction);
            }
//                    "Same sound again!"
//clear on combat end

            if (repeat || RandomWizard.chance(110 - listener.getIntParam("perception"))) {
                level = LogManager.LOGGING_DETAIL_LEVEL.FULL;
            } else {
                if (unit.isMine()) {
                    EUtils.showInfoText(logged);
                    if (RandomWizard.chance(89)) {
                        DC_SoundMaster.playEffectSound(AudioEnums.SOUNDS.WHAT, unit, 100, 0);
                        DC_SoundMaster.playEffectSound(AudioEnums.SOUNDS.IDLE, unit);
                    } else
                        DC_SoundMaster.playMoveSound(unit);
                    //TODO
                } else {
                    EUtils.showInfoText("A hearing event logged!");
                }
            }
            game.getLogManager().log(level, logged);

        }

    }

    @Override
    public void actionComplete(ActiveObj activeObj) {
        if (!isOn()) {
            return;
        }
        for (Unit listener : game.getPlayer(!activeObj.isMine()).collectControlledUnits_()) {
            checkLogged((DC_ActiveObj) activeObj, listener);
        }
//        Unit listener = Eidolons.getMainHero();
    }

    private boolean isOn() {
        return RuleKeeper.isRuleOn(RuleEnums.RULE.HEARING);
    }

    @Override
    public boolean isAppliedOnExploreAction(DC_ActiveObj action) {
        return true;
    }

    public void reset() {
        playerWasHeard = false;
        map.clear();
    }

    private boolean isHearingRequired(Unit listener, DC_ActiveObj action) {
        int maxDistance = listener.isMine() ? 8 : 4;
//            TODO  db!
        if (action.getOwnerUnit().getCoordinates().dst_(listener.getCoordinates()) > maxDistance) {
            return false;
        }
        VisionEnums.VISIBILITY_LEVEL v = action.getOwnerUnit().getVisibilityLevelMapper().get(listener, action.getOwnerUnit());
        return v == UNSEEN || v == BLOCKED || v == OUTLINE;
    }


}
