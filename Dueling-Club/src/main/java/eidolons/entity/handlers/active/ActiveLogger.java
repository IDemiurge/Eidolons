package eidolons.entity.handlers.active;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.handlers.EntityLogger;
import main.entity.handlers.EntityMaster;
import main.entity.obj.Obj;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;
import main.system.text.LogEntryNode;

/**
 * Created by JustMe on 2/23/2017.
 */
public class ActiveLogger extends EntityLogger<DC_ActiveObj> {

    private LogEntryNode entry;

    public ActiveLogger(DC_ActiveObj entity, EntityMaster<DC_ActiveObj> entityMaster) {
        super(entity, entityMaster);
    }

    public LogEntryNode getEntry() {
        return entry;
    }

    public void logCompletion() {

        if (getMaster().getChecker().isAttackAny()) {
            Obj targetObj = getRef().getTargetObj();
            if (targetObj == null) {
                if (getEntity().getLastSubaction() != null) {
                    targetObj = getEntity().getLastSubaction().getRef().getTargetObj();
                }
            }
            if (targetObj == null) {
                game.getLogManager().doneLogEntryNode(ENTRY_TYPE.ATTACK, getOwnerObj().getNameIfKnown());
            } else {
                game.getLogManager().doneLogEntryNode(ENTRY_TYPE.ATTACK, getOwnerObj().getNameIfKnown(),
                 // lastSubaction.getName()
                 targetObj.getNameIfKnown());
            }
        } else {
            game.getLogManager().doneLogEntryNode();
            if (getEntry() != null) {
            }
        }
    }

    public ENTRY_TYPE log() {
        // TODO *player's* detection, not AI's!
        String string = getOwnerObj().getNameIfKnown() + " is activating "
         + getEntity().getDisplayedName();
        LogMaster.gameInfo(StringMaster.getStringXTimes(80 - string.length(), ">") + string);

        boolean logAction = getOwnerObj().getVisibilityLevel() == VISIBILITY_LEVEL.CLEAR_SIGHT
         && !getMaster().getChecker().isAttackAny();
        entry = null;
        ENTRY_TYPE entryType = ENTRY_TYPE.ACTION;
        if (getMaster().getChecker().getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MOVE) {
            entryType = ENTRY_TYPE.MOVE;
            logAction = true;
        }
        if (!getMaster().getChecker().isAttackAny()) {
            entry = game.getLogManager().newLogEntryNode(entryType, getOwnerObj(), this);
        }

        if (logAction) {
            game.getLogManager().log(">> " + string);
        } else if (VisionHelper.checkVisible(getOwnerObj(), false) && !getMaster().getChecker().isAttackAny()) {
            String text = " performs an action... ";
            game.getLogManager().log(">> " + getOwnerObj().getNameIfKnown() + text);
        }
        return entryType;
    }

    public ENTRY_TYPE getEntryType() {
        return ENTRY_TYPE.ACTION;
    }

    @Override
    public ActiveMaster getMaster() {
        return (ActiveMaster) super.getMaster();
    }

    @Override
    public Executor getHandler() {
        return (Executor) super.getHandler();
    }

    public boolean isActivationLogged() {
        if (ExplorationMaster.isExplorationOn()) {
            if (getEntity().isTurn() || getEntity().isMove()
             || getEntity().getActionType() == ACTION_TYPE.HIDDEN
             ) {
                return false;
            }

            return getGame().isDebugMode() ||
             (getEntity().getOwnerObj().isMine() &&
              getEntity().getOwnerObj().getPlayerVisionStatus(false) != PLAYER_VISION.KNOWN
              && getEntity().getOwnerObj().getPlayerVisionStatus(false) != PLAYER_VISION.DETECTED
             );
        }
        if (!getEntity().getOwnerObj().isMine())
            return (getGame().isDebugMode() ||
                    getEntity().getOwnerObj().getPlayerVisionStatus(false) != PLAYER_VISION.CONCEALED)
                    &&
                    getEntity().getOwnerObj().getPlayerVisionStatus(false) != PLAYER_VISION.INVISIBLE;
        return true;
    }


    public boolean isTargetLogged() {
        if (getName().equalsIgnoreCase("wait")){
            return false;
        }
        if (entity.getActionGroup() == ACTION_TYPE_GROUPS.MODE)
            return false;
        if (entity.getActionGroup() == ACTION_TYPE_GROUPS.TURN)
            return false;
        return entity.getActionGroup() != ACTION_TYPE_GROUPS.MOVE;

    }

    public BattleFieldObject getOwnerObj() {
        return getEntity().getOwnerObj();
    }

}
