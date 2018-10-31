package main.system.text;

import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.GenericEnums.ROLL_TYPES;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.values.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.entity.DataModel;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.game.core.game.Game;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG;
import main.system.graphics.ANIM;
import main.system.launch.CoreEngine;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.*;

public abstract class LogManager {

    public static final String WRITE_TO_TOP = "to top";
    static boolean dirty;
    protected List<String> topDisplayedEntries;
    protected List<String> fullDisplayedEntries;
    protected Map<LOG, List<String>> entryMap;
    protected Game game;
    protected boolean addPeriod = true;
    List<LOG_CASES> loggedCasesCustom;
    Integer infoLevel;
    LogEntryNode currentNode;
    private List<String> displayedLines;
    private List<LogEntryNode> topNodes = new ArrayList<>();
    private Map<Integer, List<LogEntryNode>> topNodeMap;
    private Map<ENTRY_TYPE, LogEntryNode> pendingEntries = new HashMap<>();
    private int layer;
    private Map<ENTRY_TYPE, List<ANIM>> pendingAnimsToLink;
    private boolean logNodesOn;

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

    public LogEntryNode newLogEntryNode(ENTRY_TYPE type, Object... args) {
        return newLogEntryNode(false, type, args);
    }

    public LogEntryNode getLogEntryNode(Boolean first_last_custom, ENTRY_TYPE type, Object... args) {
        LogEntryNode lastEntry = null;
        for (LogEntryNode entry : getTopNodes()) {
            if (entry.getType() == type) {
                if (first_last_custom != null) {
                    if (first_last_custom) {
                        return entry;
                    } else {
                        lastEntry = entry;
                    }
                } else {
                    if (entry.getArgs().equals(args)) {
                        return entry;
                    }
                }
            }
        }
        return lastEntry;
    }

    public LogEntryNode newLogEntryNode(boolean logLater, ENTRY_TYPE type, Object... args) {
        if (LogMaster.isOff())
            return null;
        Object[] argArray = args;
        if (argArray == null) {
            args = new Boolean[]{false}; // TODO quickfix logLater
        }
        if (currentNode != null) {
            if (currentNode.getType() == type) {
                doneLogEntryNode();
            }
        }

        boolean top = currentNode == null;
        boolean writeToTop = type.isWriteToTop();
        if (argArray.length > 0) {
            if (argArray[0] == WRITE_TO_TOP) {
                writeToTop = true;
                argArray = ListMaster.removeIndices(ListMaster.toList(argArray), 0).toArray();
            }
        }

        LogEntryNode entry = logLater ? new LogEntryNode(currentNode, type, getDisplayedLines()
         .size() + 1, logLater) : new LogEntryNode(currentNode, type, getDisplayedLines()
         .size() + 1, logLater, argArray);
        // TODO why +1? could lineIndex be the reason why first/last position
        // isn't filled?
        entry.addLinkedAnimations(getPendingAnimsToLink().remove(type));
        // entry.setLayer(layer);
        // node.getLineIndex() > getRowCount() * getCurrentIndex()
        // && node.getLineIndex() < (getCurrentIndex() + 1) * getRowCount()

        // start point is known, so why not init index/y ?
        if (CoreEngine.isGraphicsOff()) {
            addTextToDisplayed(entry.getHeader());
            return entry;
        }

        if (top || writeToTop) {
            int size = getDisplayedLines().size();
            int pageIndex = size
             / (EntryNodeMaster.INNER_HEIGHT / EntryNodeMaster.getRowHeight(true));
            entry.setPageIndex(pageIndex);
        }
        if (!top) {
            // TODO topIndex separate!!
            // size =currentNode.getTextLines().size();
            // pageIndex = size/
            // (EntryNodeMaster.INNER_HEIGHT/EntryNodeMaster.getRowHeight(false));
            // entry.setSubNodePageIndex(pageIndex);
        }
        // TODO some entries should be duplicated inside parent node and on top!
        if (!top) {
            // subNodeMap = subNodesMap.get(currentNode);
            currentNode.addEntry(entry); // index and y here too!
            // TODO how to know whether we are continuing in the parent?

        }
        if (writeToTop || top) {
            // y = topY;
            // pageIndex = topIndex;
            // calculate proper Y? Always single line per header? TODO
            getTopNodes().add(entry);
            if (!logLater) {
                addTextToDisplayed(entry.getHeader());
            }

            // here!
        }
        if (logLater) {
            pendingEntries.put(type, entry);
        }

        if (layer >= getMaxLayerSupported()) {
            currentNode.addEntry(entry);
            return entry;
        }
        layer++;
        currentNode = entry;

        return entry;
    }

    private int getMaxLayerSupported() {
        return 2;
    }

    public void flushFinalEntryHeader(ENTRY_TYPE type, Object... args) {
        LogEntryNode entry = pendingEntries.get(type);
        if (entry == null) {
            return;
        }
        pendingEntries.remove(type);
        entry.initHeader(args);
        addTextToDisplayed(entry.getHeader());
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
        // pendingEntriesMap.put(enclosingEntryType, list);
        // list.add(entry);
        if (currentNode != null) {
            currentNode.addString(entry);
            // will increase Y
        }
        // if (enclosingEntryType!=null ){
        // pendingEntries
        // }

        getEntryMap().get(log).add(entry);
        if (!isLogOn(log)) {
            return false;
        }

        setDirty(true);

        LogMaster.log(entry);

        if (!logNodesOn || currentNode == null) {
            addTextToDisplayed(entry);
        }

        return true;
    }

    // public Map<Integer, LogEntryNode> getEntryNodesForPageIndex(int index) {
    // return topNodeMap.get(index);
    // }
    //
    // public Map<Integer, LogEntryNode>
    // getEntrySubNodesForNodeView(LogEntryNode node) {
    // Map<Integer, LogEntryNode> map = subNodesMap.get(node);
    // if (map == null) {
    // map = new HashMap<>();
    // }
    // return map;
    // }

    protected void addTextToDisplayed(String entry) {
        if (CoreEngine.isGraphicsOff())
            return;
        getTopDisplayedEntries().add(entry);
        getDisplayedLines().addAll(TextWrapper.wrap(entry, EntryNodeMaster.getWrapLength(true)));
    }

    // getCaseLogChannel()

    // isChannelLoggedToFile()

    public void doneLogEntryNode(ENTRY_TYPE type, Object... args) {
        // if (currentNode.getType()==type) ???
        flushFinalEntryHeader(type, args);
        doneLogEntryNode();
    }

    public void doneLogEntryNode() {
        if (currentNode != null) {
            layer--;
            currentNode = currentNode.getParent();
        } else {
            layer = 0;
        }
        // stackY.pop(); // TODO return to previous page level Y
        // y = stackY.peek();
    }

    public boolean isCaseLoggedInGame(LOG_CASES CASE) {
        if (loggedCasesCustom != null) {
            return loggedCasesCustom.contains(CASE);
        }
        switch (CASE) {

        }
        return true;

    }

    public boolean isCaseLoggedInGameSuperFastMode(LOG_CASES CASE) {
        return false;

    }

    public boolean isCaseLoggedInGameFastMode(LOG_CASES CASE) {

        return false;

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

        String entry = obj.getNameIfKnown() + " has been slain by " + killer.getNameIfKnown();
        if (obj == killer) {
            entry = obj.getNameIfKnown() + " has fallen";
        }
        if (obj.getOwner().isMe()) {
            entry = StringMaster.MESSAGE_PREFIX_FAIL + entry;
        } else {
            entry = StringMaster.MESSAGE_PREFIX_SUCCESS + entry;
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
        String entry = attacker.getNameIfKnown() + " is dealing " + amount + " damage to "
         + attacked.getNameIfKnown() + " (" + dmg_type.getName() + ")";
        if (attacker == attacked) {
            entry = amount + " " + dmg_type.getName() + " damage is being dealt to "
             + attacked.getNameIfKnown();
        }

        entry = StringMaster.MESSAGE_PREFIX_MISC + entry;

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
        entry = StringMaster.MESSAGE_PREFIX_INFO + entry;
        log(LOG.GAME_INFO, entry);
    }

    public abstract boolean logMovement(Ref ref);

    public void logValueMod(PARAMETER param, Number i, Obj obj) {
        Integer amount = NumberUtils.getInteger(i.toString());
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
        String prefix = (positive) ? StringMaster.MESSAGE_PREFIX_SUCCESS
         : StringMaster.MESSAGE_PREFIX_FAIL;
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

    public void log(String string) {
        log(LOG.GAME_INFO, string);
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
            string = StringMaster.MESSAGE_PREFIX_FAIL + string;
        } else {
            string = StringMaster.MESSAGE_PREFIX_SUCCESS + string;
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
        log(LOG.GAME_INFO, StringMaster.MESSAGE_PREFIX_ALERT + string, ENTRY_TYPE.ACTION);

    }

    public void logCounterModified(DataModel entity, String name, int modValue) {
        Integer value = entity.getCounter(name);
        modValue = Math.abs(modValue);
        name = StringMaster.getWellFormattedString(name);
        if (modValue > 0) {
            logInfo(modValue + " " + name + "s applied to " + entity.getNameIfKnown() + ", total "
             + name + "s: " + value);
        } else {
            logInfo(modValue + " " + name + "s removed from " + entity.getNameIfKnown() + ", total "
             + name + "s: " + value);
        }

    }

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
        log(StringMaster.MESSAGE_PREFIX_ALERT + string);
    }

    public void logGood(String string) {
        log(StringMaster.MESSAGE_PREFIX_SUCCESS + string);
    }

    public void logBad(String string) {
        log(StringMaster.MESSAGE_PREFIX_FAIL + string);
    }

    public void logInfo(String string) {
        log(StringMaster.MESSAGE_PREFIX_INFO + string);
    }

    public void logProceeds(String string) {
        log(StringMaster.MESSAGE_PREFIX_PROCEEDING + string);
    }

    public void logCoating(Obj target, Obj item, Obj source, COUNTER c) {
        // TODO Auto-generated method stub

    }

    public Map<Integer, List<LogEntryNode>> getTopEntryNodesMap() {
        if (topNodeMap == null) {
            topNodeMap = new HashMap<>();
        }
        return topNodeMap;
    }

    // public void logAlert(String string) {
    // log(StringMaster.MESSAGE_PREFIX_UNKNOWN+string);
    // }
    // }

    public List<String> getDisplayedLines() {
        if (displayedLines == null) {
            displayedLines = new ArrayList<>();
        }
        return displayedLines;
    }

    public List<LogEntryNode> getTopNodes() {
        return topNodes;
    }

    // public Map<Integer, LogEntryNode> getPageIndexNodeMap() {
    // pageIndexNodeMap = topNodeMap.get(topIndex);
    // if (pageIndexNodeMap == null) {
    // pageIndexNodeMap = new HashMap<>(); // ???
    // topNodeMap.put(pageIndex, pageIndexNodeMap);
    // }
    // return pageIndexNodeMap;
    // }

    public void setTopNodes(List<LogEntryNode> topNodes) {
        this.topNodes = topNodes;
    }

    public void addPendingAnim(ENTRY_TYPE entryType, ANIM anim) {

        MapMaster.addToListMap(getPendingAnimsToLink(), entryType, anim);
    }

    public Map<ENTRY_TYPE, List<ANIM>> getPendingAnimsToLink() {
        if (pendingAnimsToLink == null) {
            pendingAnimsToLink = new XLinkedMap<>();
        }
        return pendingAnimsToLink;
    }


    public enum CASE_LOG_INFO_LEVEL {
        NONE, BASIC, FULL,
    }

    public enum LOG_CASES {
        ALLY_PARAMETER_CHANGE, ALLY_DAMAGE_TAKEN, CRITICAL_STRIKE,

        ALLY_MOVEMENT, ALLY_ROLL_STANDARD, FAST_ACTION, COATING, COUNTER, DEATH,

    }

    // int pageIndex = 0;
    // subNodePageIndex;
    // int y = 0;
    // Stack<Integer> stackY = new Stack<>();
    // Stack<Integer> stackPageIndex = new Stack<>();
    // Map<Integer, Map<Integer, LogEntryNode>> topNodeMap = new HashMap<>();
    // Map<Integer, Map<Integer, LogEntryNode>> subNodePageIndexMap = new
    // HashMap<>();
    // Map<LogEntryNode, Map<Integer, LogEntryNode>> subNodesMap = new
    // HashMap<>();
    // private Map<Integer, LogEntryNode> pageIndexNodeMap; // for current page
    // index!
    // private Map<Integer, LogEntryNode> subNodeMap; // for current node!
    // private int topIndex = 0;
    // private int topY = 0;

    // public void addedLineToSubNodeEntry(int size) {
    // y += size * EntryNodeMaster.getRowHeight();
    // if (y > EntryNodeMaster.INNER_HEIGHT) {
    // pageIndexNodeMap = new HashMap<>();
    // pageIndex++;
    // topNodeMap.put(pageIndex, pageIndexNodeMap); // TODO not top!
    // y -= EntryNodeMaster.INNER_HEIGHT;
    // }
    // // TODO THERE CAN BE PAGES IN SUB-NODE
    // }

    // private void addedEntryToTop(LogEntryNode entry) {
    // getPageIndexNodeMap().put(topY, entry);
    // topY += EntryNodeMaster.getRowHeight();
    //
    // if (y > EntryNodeMaster.INNER_HEIGHT) {
    // topIndex++;
    // pageIndexNodeMap = new HashMap<>();
    // topNodeMap.put(pageIndex, pageIndexNodeMap);
    // topY = 0;
    // }
    // }
}
