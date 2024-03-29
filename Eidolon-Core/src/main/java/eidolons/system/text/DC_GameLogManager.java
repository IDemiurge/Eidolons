package eidolons.system.text;

import com.badlogic.gdx.utils.StringBuilder;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.advanced.companion.Order;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.story.cinematic.Cinematics;
import eidolons.content.consts.Images;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.entity.DataModel;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.core.game.Game;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
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

import static main.content.enums.GenericEnums.DAMAGE_TYPE;

public class DC_GameLogManager extends LogManager {

    //EA check - use images in log more
    public static final String ALIGN_CENTER = "<center>";
    public static final String IMAGE_SEPARATOR = "[img==]";
    public static final String UNIT_TURN_PREFIX = "Active: ";
    private final List<String> fullEntryList = new ArrayList<>();

    public DC_GameLogManager(Game game) {
        super(game);
    }

    @Override
    public void newLogEntryNode(ENTRY_TYPE type, Object... args) {

    }

    @Override
    public void doneLogEntryNode() {
    }
    /*
     * Do I need to have information levels? What is the best way to access the
     * log and control its filtering?
     */

    public void logCounterModified(DataModel entity, String name, int modValue) {
        Integer value = entity.getCounter(name);
        name = StringMaster.format(name);
        LOGGING_DETAIL_LEVEL detail = LOGGING_DETAIL_LEVEL.ESSENTIAL;
        if (!entity.isMine()) {
            if (entity instanceof BattleFieldObject) {
                if (!((BattleFieldObject) entity).isDetectedByPlayer()) {
                    detail = LOGGING_DETAIL_LEVEL.FULL;
                }
            }
        }
        if (modValue > 0) {
            log(detail, modValue + " " + name + "s applied to " + entity.getNameIfKnown() + ", total "
                    + name + "s: " + value);
        } else {
            modValue = Math.abs(modValue);
            logInfo(modValue + " " + name + "s removed from " + entity.getNameIfKnown() + ", total "
                    + name + "s: " + value);
        }

    }


    public void logOrderFailed(Order order, Unit unit) {
        String entry = unit.getName() + " has failed to obey " + order.toString();
        entry = Strings.MESSAGE_PREFIX_PROCEEDING + entry;
        LogMaster.log(1, entry);
    }

    public void logMovement(DC_Obj obj, Coordinates c) {
        String name = obj.getName();
        // if (obj.getActivePlayerVisionStatus() ==
        // UNIT_TO_PLAYER_VISION.UNKNOWN)
        // name = "A unit";
        String entry = name + " has moved to a new position at " + c.toString();
        entry = Strings.MESSAGE_PREFIX_PROCEEDING + entry;
        LogMaster.log(1, entry);
        if (!obj.isMine())
            if (obj.getActivePlayerVisionStatus() == PLAYER_VISION.INVISIBLE) {
                return;
            }
        if (obj == Core.getMainHero()) {
            DIRECTION relative = DirectionMaster.getRelativeDirection( Core.getMainHero().getLastCoordinates()
                    , Core.getPlayerCoordinates()                   );
            int dst = Core.getPlayerCoordinates().dst(Core.getMainHero().getLastCoordinates());
            String gamelog = name + " has moved [" + relative.toString().toLowerCase() + "] "
                    +StringMaster.wrapInParenthesis(""+dst);
            log(gamelog);
        } else {
            int dst = Core.getPlayerCoordinates().dst(obj.getCoordinates());
            String gamelog = name + " has moved to a new position at [" + dst + "] distance";
            log(gamelog);
        }
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
                        String name = start ? unit.getNameIfKnown() : unit.getName();
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
            text.append(unit).append(", ");
        });
        text.delete(text.length() - 2, text.length());
        String message = text.toString();
        SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.MAIN, message);
        SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.COMBAT, message);
        log(message);
    }

    public void logBattleJoined(List<Unit> newUnits) {
        StringBuilder text = new StringBuilder();
        text.append("Battle joined by: ");
        newUnits.forEach(unit -> {
            text.append(unit.getNameIfKnown()).append(", ");
        });
        text.delete(text.length() - 2, text.length());
        String message = text.toString();
        SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.MAIN, message);
        SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.COMBAT, message);
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

        entry = tryAddColor(entry);

        if (isSeparatorTest())
            GuiEventManager.trigger(GuiEventType.LOG_ENTRY_ADDED, null);
        else
            entry = checkAddSeparator(entry);

        GuiEventManager.trigger(GuiEventType.LOG_ENTRY_ADDED, entry);

    }

    private String tryAddColor(String entry) {


        return entry;
    }

    private String checkAddSeparator(String entry) {
        if (entry.contains(UNIT_TURN_PREFIX)) {
//some might contain a prefix?
            GuiEventManager.trigger(GuiEventType.LOG_ENTRY_ADDED, null);
        }
        return entry;
    }

    public enum LOG_IMAGE_CASE {
        SNEAK,
        COUNTER,
        FORCE,
        CRITICAL,
        DODGE,
        PARRY,

    }

    private String tryAddImage(String entry) {
        if (entry.contains("FORCE")) {
            return "gen\\perk\\selected_00082.png" + IMAGE_SEPARATOR + entry;
//            return Images.ICONS_FORCE;
        }

        if (entry.contains(IS_DEALING) || entry.contains(DAMAGE_IS_BEING_DEALT_TO)) {
            for (DAMAGE_TYPE damage_type : DAMAGE_TYPE.values()) {
                if (entry.contains(damage_type.getName())) {
                    return ImageManager.getDamageTypeImagePath(
                            damage_type.getName()) + IMAGE_SEPARATOR + entry;
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
        if (Cinematics.ON) {
            return false;
        }
        return super.log(log, entry, enclosingEntryType);
    }

    public boolean log(LOGGING_DETAIL_LEVEL log, String entry) {
//    TODO     fullEntryList.add(entry);
        int i = EnumMaster.getEnumConstIndex(LOGGING_DETAIL_LEVEL.class, log);
        int logLevel = 1;
        if (logLevel < i)
            return false;

        return log(LOG.GAME_INFO, entry, null);
    }




}
