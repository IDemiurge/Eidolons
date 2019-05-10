package eidolons.system.text;

import com.badlogic.gdx.utils.StringBuilder;
import com.bitfire.utils.ItemsManager;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.advanced.companion.Order;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.texture.Images;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.core.game.Game;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.images.ImageManager;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;
import main.system.text.LogManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static main.content.enums.GenericEnums.*;

public class DC_LogManager extends LogManager {


    public static final String ALIGN_CENTER = "<center>";
    public static final String IMAGE_SEPARATOR = "[img==]";
    public static final String UNIT_TURN_PREFIX = "Active: ";
    private int logLevel = 1;
    private List<String> fullEntryList=    new ArrayList<>() ;

    public DC_LogManager(Game game) {
        super(game);
    }

    /*
     * Do I need to have information levels? What is the best way to access the
     * log and control its filtering?
     */

    public void logOrderFailed(Order order, Unit unit) {
        String entry = unit.getName() + " has failed to obey " + order.toString();
        entry = StringMaster.MESSAGE_PREFIX_PROCEEDING + entry;
        LogMaster.log(1, entry);
    }

    public void logMovement(DC_Obj obj, Coordinates c) {
        if (obj.getActivePlayerVisionStatus() == PLAYER_VISION.INVISIBLE) {
            return;
        }
        String name = obj.getName();
        // if (obj.getActivePlayerVisionStatus() ==
        // UNIT_TO_PLAYER_VISION.UNKNOWN)
        // name = "A unit";

        String entry = name + " has moved to a new position at " + c.toString();

        entry = StringMaster.MESSAGE_PREFIX_PROCEEDING + entry;
        LogMaster.log(1, entry);
    }

    public boolean logMovement(Ref ref) {
        logMovement((DC_Obj) ref.getSourceObj(), ref.getTargetObj().getCoordinates());
        return true;
    }


    public void logBattleEnds() {
        logBattle(false);
    }

    public void logBattleStarts() {
        logBattle(true);
    }

    public void logBattle(boolean start) {
        StringBuilder text = new StringBuilder();
        if (start)
            text.append("Combat started, opponents: ");
        else
            text.append("Combat ended, enemies slain: ");
//TODO + "N"
        Map<String, Integer> map = new LinkedHashMap<>();
        getGame().getUnits().forEach(unit -> {
            if (unit.isHostileTo(getGame().getPlayer(true)))
                if (!unit.getAI().isOutsideCombat())
                    if (
                            (!start && unit.isDead()) ||
                                    (start && unit.getPlayerVisionStatus(false) != PLAYER_VISION.INVISIBLE)) {
                        String name = unit.getNameIfKnown();
                        if (map.containsKey(name))
                            MapMaster.addToIntegerMap(map, name, 1);
                        else {
                            map.put(name, 1);
                        }
                    }
        });
        map.keySet().forEach(unit -> {
            int i = map.get(unit);
            if (i > 1) {
                unit = unit + StringMaster.wrapInParenthesis(i + "");
            }
            text.append(unit + ", ");
        });
        text.delete(text.length() - 2, text.length());
        String message = text.toString();
        SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.MAIN, message);
        SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.COMBAT, message);
        log(message);
    }

    public void logBattleJoined(List<Unit> newUnits) {
        StringBuilder text = new StringBuilder();
        text.append("Battle joined by: ");
        newUnits.forEach(unit -> {
            text.append(unit.getNameIfKnown() + ", ");
        });
        text.delete(text.length() - 2, text.length());
        String message = text.toString();
        SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.MAIN, message);
        SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.COMBAT, message);
        log(message);
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    @Override
    protected void addTextToDisplayed(String entry) {
        super.addTextToDisplayed(entry);
        if (isImgTest())
            GuiEventManager.trigger(GuiEventType.LOG_ENTRY_ADDED, Images.TINY_GOLD + IMAGE_SEPARATOR + entry);
        else
            entry = tryAddImage(entry);

        if (isSeparatorTest())
            GuiEventManager.trigger(GuiEventType.LOG_ENTRY_ADDED, null);
        else
            entry = checkAddSeparator(entry);

        GuiEventManager.trigger(GuiEventType.LOG_ENTRY_ADDED, entry);

    }

    private String checkAddSeparator(String entry) {
        if (entry.contains(UNIT_TURN_PREFIX)) {
//some might contain a prefix?
            GuiEventManager.trigger(GuiEventType.LOG_ENTRY_ADDED, null);
        }
        return entry;
    }

    private String tryAddImage(String entry) {
        if (entry.contains(IS_DEALING) || entry.contains(DAMAGE_IS_BEING_DEALT_TO)){
            for (DAMAGE_TYPE damage_type:DAMAGE_TYPE.values()){
                if (entry.contains(damage_type.getName())) {
                    return ImageManager.getDamageTypeImagePath(
                            damage_type.getName())+IMAGE_SEPARATOR+ entry;
                }
            }
        }
        return entry;
    }

    public enum LOG_ICON_TYPE {
        DAMAGE,
        ACTION,
        ACTIVE,

        CRITICAL,
        SNEAK,
        VISIBILITY,

        //ADD TOOLTIP?
    }

    private boolean isImgTest() {
        return false;
    }

    public void addImageToLog(String path) {
        GuiEventManager.trigger(GuiEventType.LOG_ENTRY_ADDED, path);
    }

    private boolean isSeparatorTest() {
        return false;
    }

    @Override
    public boolean log(LOG log, String entry, ENTRY_TYPE enclosingEntryType) {
        return super.log(log, entry, enclosingEntryType);
    }

    public boolean log(LOGGING_DETAIL_LEVEL log, String entry) {
//    TODO     fullEntryList.add(entry);
        int i = EnumMaster.getEnumConstIndex(LOGGING_DETAIL_LEVEL.class, log);
         if (logLevel < i)
             return false;

        return super.log(LOG.GAME_INFO, entry);
    }


    public void logHide(Unit source, BattleFieldObject object) {
        LOGGING_DETAIL_LEVEL level = LOGGING_DETAIL_LEVEL.FULL;
        if (source.isPlayerCharacter()){
            level = LOGGING_DETAIL_LEVEL.ESSENTIAL;
        }
        log(level, source + " loses sight of " + object.getName());
    }

    public void logReveal(Unit source, BattleFieldObject object) {
        LOGGING_DETAIL_LEVEL level = LOGGING_DETAIL_LEVEL.FULL;
        if (source.isPlayerCharacter()){
            level = LOGGING_DETAIL_LEVEL.ESSENTIAL;
        }
        log(level, source + " spots " + object.getName());
    }


}
