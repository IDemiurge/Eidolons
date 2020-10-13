package main.system.text;

import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.GenericEnums.ROLL_TYPES;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.values.parameters.PARAMETER;
import main.entity.DataModel;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.game.core.game.Game;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG;
import main.system.launch.CoreEngine;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.*;

public abstract class LogManager {

    public static final String WRITE_TO_TOP = "to top";
    public static final String DAMAGE_IS_BEING_DEALT_TO = " damage is being dealt to ";
    public static final String IS_DEALING = " is dealing ";
    static boolean dirty;
    protected List<String> topDisplayedEntries;
    protected Map<LOG, List<String>> entryMap;
    protected Game game;
    protected boolean addPeriod = true;
    List<LOG_CASES> loggedCasesCustom;
    Integer infoLevel;
    private List<String> displayedLines;

    public LogManager(Game game) {
        this.game = game;
        setEntryMap(new HashMap<>());
        topDisplayedEntries = new ArrayList<>();
        if (!LogMaster.isOff())
            initDefaultEntries();
        for (LOG log : LOG.values()) {
            entryMap.put(log, new ArrayList<>());
        }
    }

    public static boolean isDirty() {
        return dirty;
    }

    public static void setDirty(boolean dirty) {
        LogManager.dirty = dirty;
    }

    public Game getGame() {
        return game;
    }

    //TODO Misc Review
    public void flushFinalEntryHeader(ENTRY_TYPE type, Object... args) {
        // LogEntryNode entry = pendingEntries.get(type);
        // if (entry == null) {
        //     return;
        // }
        // pendingEntries.remove(type);
        // entry.initHeader(args);
        // addTextToDisplayed(entry.getHeader());
    }

    public boolean log(LOG log, String entry) {
        return log(log, entry, null);
    }

    public boolean log(LOG log, String entry, ENTRY_TYPE enclosingEntryType) {
        if (entry == null || log == null || LogMaster.isOff()) {
            return false;
        }
        entry = entry.trim();
        if (addPeriod) {
            if (!entry.endsWith(".") && !entry.endsWith("?") && !entry.endsWith("!")
                    && !entry.endsWith("<")) {
                entry += ".";
            }
        }

        getEntryMap().get(log).add(entry);
        if (!isLogOn(log)) {
            return false;
        }

        setDirty(true);

        LogMaster.log(entry);

        addTextToDisplayed(entry);

        return true;
    }

    protected void addTextToDisplayed(String entry) {
        if (CoreEngine.isGraphicsOff())
            return;
        getTopDisplayedEntries().add(entry);
        if (!isWrapped())
            getDisplayedLines().add(entry);
        else
            getDisplayedLines().addAll(TextWrapper.wrap(entry, EntryNodeMaster.getWrapLength(true)));
    }

    private boolean isWrapped() {
        return false;
    }

    // getCaseLogChannel()

    // isChannelLoggedToFile()

    public void doneLogEntryNode(ENTRY_TYPE type, Object... args) {
        // if (currentNode.getType()==type) ???
        flushFinalEntryHeader(type, args);
        doneLogEntryNode();
    }

    public void doneLogEntryNode() {
    }

    public boolean isCaseLoggedInGame(LOG_CASES CASE) {
        if (loggedCasesCustom != null) {
            return loggedCasesCustom.contains(CASE);
        }
        switch (CASE) {

        }
        return true;

    }

    protected void initDefaultEntries() {
        addTextToDisplayed(getTimeStarted());
    }

    protected String getTimeStarted() {
        int minutes = Calendar.getInstance().getTime().getMinutes();
        String minStr = "" + minutes;
        if (minutes / 10 == 0) {
            minStr = "0" + minutes;
        }
        return "Game started at " + Calendar.getInstance().getTime().getHours() + ":" + minStr;
    }

    public void clear() {
        getTopDisplayedEntries().clear();
        initDefaultEntries();

    }

    protected boolean isLogOn(LOG log) {
        switch (log) {
            case DEBUG:
                return game.isDebugMode();
            case GAME_INFO:
                return true;
            case HIDDEN_INFO:
                return false;
            case SYSTEM_INFO:
                return game.isDebugMode();
        }
        return false;
    }

    public void logDeath(Obj obj, Entity killer) {

        String entry = obj.getNameIfKnown() + " has been " +
                (obj.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ ? "destroyed" : "slain") +
                " by " + killer.getNameIfKnown();
        if (obj == killer) {
            entry = obj.getNameIfKnown() + " has fallen";
        }
        if (obj.getOwner().isMe()) {
            entry = Strings.MESSAGE_PREFIX_FAIL + entry;
        } else {
            entry = Strings.MESSAGE_PREFIX_SUCCESS + entry;
        }
        log(LOG.GAME_INFO, entry);
    }

    public void logNewObj(Obj obj) {
        String entry = obj.getNameIfKnown() + " has entered the battlefield";
        log(LOG.GAME_INFO, entry);
    }

    public void logActivation(ActiveObj active) {
        Obj obj = active.getOwnerUnit();
        // preCheck visibility?
    }

    public void logDamageBeingDealt(int amount, Obj attacker, Obj attacked, DAMAGE_TYPE dmg_type) {
        String entry = attacker.getNameIfKnown() + IS_DEALING + amount + " damage to "
                + attacked.getNameIfKnown() + " (" + dmg_type.getName() + ")";
        if (attacker == attacked) {
            entry = amount + " " + dmg_type.getName() + DAMAGE_IS_BEING_DEALT_TO
                    + attacked.getNameIfKnown();
        }

        entry = Strings.MESSAGE_PREFIX_MISC + entry;

        log(LOG.GAME_INFO, entry);

    }

    public void logDamageDealt(int t_damage, int e_damage, Obj attacker, Obj attacked) {
        String entry = attacker.getNameIfKnown() + " has dealt " + t_damage + " / " + e_damage
                + " damage to " + attacked.getNameIfKnown();
        if (attacker == attacked) {
            entry = t_damage + " / " + e_damage + " damage has been dealt to "
                    + attacked.getNameIfKnown();
        }

        // if (attacked.getOwner().isMe()) {
        // entry = StringMaster.MESSAGE_PREFIX_FAIL + entry;
        // } else {
        // entry = StringMaster.MESSAGE_PREFIX_SUCCESS + entry;
        // }
        entry = Strings.MESSAGE_PREFIX_INFO + entry;
        log(LOG.GAME_INFO, entry);
    }

    public abstract boolean logMovement(Ref ref);

    public void logValueMod(PARAMETER param, Number i, Obj obj) {
        Integer amount = NumberUtils.getIntParse(i.toString());
        if (amount.toString().equals("0")) {
            return;
        }

        PARAMETER baseParameter = ContentValsManager.getBaseParameterFromCurrent(param);
        if (baseParameter == null) {
            return;
        }

        boolean positive = !amount.toString().contains("-");
        String string = positive ? " gains " : " loses ";
        if (!obj.getOwner().isMe()) {
            positive = !positive;
        }
        String prefix = (positive) ? Strings.MESSAGE_PREFIX_SUCCESS
                : Strings.MESSAGE_PREFIX_FAIL;
        String s = (obj.getNameIfKnown()) + string + " " + amount.toString().replace("-", "") + " "
                + baseParameter.getShortName();
        log(prefix + s);
    }

    public boolean logEvent(Event event, boolean success) {
        if (event.getType() instanceof STANDARD_EVENT_TYPE) {
            STANDARD_EVENT_TYPE type = (STANDARD_EVENT_TYPE) event.getType();
            switch (type) {

                case UNIT_FINISHED_MOVING: {
                    return logMovement(event.getRef());
                }
                case UNIT_HAS_BEEN_KILLED: {
                    // return logDeath(event.getRef());
                }
                case UNIT_IS_BEING_ATTACKED: {
                    return logAttack(event.getRef());
                }
                case UNIT_SUMMONED: {
                    return logNewObj(event.getRef());
                }
            }
        }
        return true;

    }

    public void logAttack(Obj obj, Entity attacker) {
    }

    protected boolean logAttack(Ref ref) {
        logAttack(ref.getSourceObj(), ref.getTargetObj());
        return true;
    }

    protected boolean logNewObj(Ref ref) {
        // logNewObj(ref.getSourceObj());
        return true;
    }

    protected boolean logDeath(Ref ref) {
        logDeath(ref.getSourceObj(), ref.getTargetObj());
        return false;
    }

    public List<String> getTopDisplayedEntries() {
        return topDisplayedEntries;
    }

    public void setDisplayedEntries(List<String> displayedEntries) {
        this.topDisplayedEntries = displayedEntries;
    }

    public Map<LOG, List<String>> getEntryMap() {
        return entryMap;
    }

    public void setEntryMap(Map<LOG, List<String>> entryMap) {
        this.entryMap = entryMap;
    }


    public abstract boolean log(LOGGING_DETAIL_LEVEL log, String entry);

    public void log(String string) {
        log(LOGGING_DETAIL_LEVEL.ESSENTIAL, string);
    }

    public void logStdRoll(Ref ref, int greater, int randomInt, int than, int randomInt2,
                           ROLL_TYPES roll_type) {
        Obj source = ref.getSourceObj();
        Obj target = ref.getEvent().getRef().getTargetObj();
        boolean fail = randomInt2 > randomInt;
        String rollTarget = target.getNameIfKnown() + ((fail) ? " fails" : " wins") + " a "
                + roll_type.getName() + " roll with " + randomInt + " out of " + greater;
        String rollSource = source.getNameIfKnown() + "'s " + randomInt2 + " out of " + than;
        String string = rollTarget + " vs " + rollSource;
        if (!target.getOwner().isMe()) {
            fail = !fail;
        }
        if (fail) {
            string = Strings.MESSAGE_PREFIX_FAIL + string;
        } else {
            string = Strings.MESSAGE_PREFIX_SUCCESS + string;
        }

        log(string);
    }

    public void logFastAction(Obj payee, Ref ref) {

        String text = " finishes ";
        ActiveObj active = ref.getActive();
        if (active.getOBJ_TYPE_ENUM() == DC_TYPE.SPELLS) {
            text = " casts ";
            if (StringMaster.compare(active.getProp("spell tags"), "channeling", false)) {
                text = " channels ";
            }
        }
        String string = payee.getNameIfKnown() + text + active.getName()
                + " rapidly, saving 1 Action point";
        // logAlert(string);
        log(LOG.GAME_INFO, Strings.MESSAGE_PREFIX_ALERT + string, ENTRY_TYPE.ACTION);

    }

    public abstract void logCounterModified(DataModel entity, String name, int modValue);

    public void logGoodOrBad(boolean positive, Obj obj, String logText) {
        if (!obj.getOwner().isMe()) {
            positive = !positive;
        }
        if (positive) {
            logGood(logText);
        } else {
            logBad(logText);
        }

    }

    public void logAlert(String string) {
        log(Strings.MESSAGE_PREFIX_ALERT + string);
    }

    public void logGood(String string) {
        log(Strings.MESSAGE_PREFIX_SUCCESS + string);
    }

    public void logBad(String string) {
        log(Strings.MESSAGE_PREFIX_FAIL + string);
    }

    public void logInfo(String string) {
        log(LOGGING_DETAIL_LEVEL.FULL, Strings.MESSAGE_PREFIX_INFO + string);
    }

    public void logProceeds(String string) {
        log(Strings.MESSAGE_PREFIX_PROCEEDING + string);
    }

    public void logCoating(Obj target, Obj item, Obj source, COUNTER c) {
        // TODO Auto-generated method stub

    }

    public List<String> getDisplayedLines() {
        if (displayedLines == null) {
            displayedLines = new ArrayList<>();
        }
        return displayedLines;
    }

    public void newLogEntryNode(ENTRY_TYPE type, Object... args) {

    }

    public enum LOG_CASES {
        ALLY_PARAMETER_CHANGE, ALLY_DAMAGE_TAKEN, CRITICAL_STRIKE,

        ALLY_MOVEMENT, ALLY_ROLL_STANDARD, FAST_ACTION, COATING, COUNTER, DEATH,

    }

    public enum LOGGING_DETAIL_LEVEL {
        CONCISE,
        ESSENTIAL,
        FULL,
        DEV,
        ;
    }

}
