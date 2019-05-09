package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.action.ActionRule;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.system.math.roll.RollMaster;
import main.content.ValueMap;
import main.content.enums.GenericEnums;
import main.content.enums.rules.VisionEnums;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;
import main.system.math.PositionMaster;
import main.system.text.LogManager;

import java.util.HashMap;
import java.util.Map;

import static main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL.*;

// sound wave img?
public class HearingRule implements ActionRule {
    DC_Game game;
    private boolean playerWasHeard;
    private Map<Unit, DIRECTION> map = new HashMap<>();

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
                    " hears something " + descriptor +
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
                logged = "You have been heard!";
                playerWasHeard = true;
            } else {
                repeat = map.get(unit) == direction; //TODO encapsulate all data
                map.put(unit, direction);
            }
//                    "Same sound again!"
//clear on combat end
            if (repeat) {
                level = LogManager.LOGGING_DETAIL_LEVEL.FULL;
            } else {
                if (unit.isMine())
                    EUtils.showInfoText(logged);
                else
                    EUtils.showInfoText("Hearing event logged!");
            }
            game.getLogManager().log(level, logged);

        }

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

    @Override
    public void actionComplete(ActiveObj activeObj) {
        for (Unit listener : game.getPlayer(!activeObj.isMine()).collectControlledUnits_()) {
            checkLogged((DC_ActiveObj) activeObj, listener);
        }
//        Unit listener = Eidolons.getMainHero();
    }

    @Override
    public boolean isAppliedOnExploreAction(DC_ActiveObj action) {
        return true;
    }

}
