package main.swing.components.panels;

import main.swing.generic.components.G_Panel;
import main.swing.generic.misc.LogPane;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.LogMaster.LOGS;
import main.system.auxiliary.secondary.Log;

import javax.swing.*;

public class DC_LogPanel extends G_Panel implements Log {

    public static final String[] fakeCombatLogs = {"Hero summons bats"};
    public static final String[] fakeSysLogs = {
            "Player HEROic connected to the game", "Game started"};
    public static final String[] fakeChatLogs = {"Hi gl hf"};
    JTabbedPane tabs;
    LogPane combatLog = new LogPane("Combat Log");
    LogPane sysLog = new LogPane("System Log");
    LogPane chatLog = new LogPane("Chat Log");
    LogPane[] logs = {combatLog, sysLog, chatLog};

    public DC_LogPanel() {
        super();

        tabs = new JTabbedPane();
        addTabs();
        init();
        add(tabs, "pos 0 0," + "  h 8/3*" + GuiManager.getSquareCellSize() + "!");
    }

    private void init() {
        for (String s : fakeChatLogs) {
            chatLog(s);
        }
        for (String s : fakeCombatLogs) {
            combatLog(s);
        }
        for (String s : fakeSysLogs) {
            sysLog(s);
        }
    }

    private void addTabs() {

        for (LogPane log : logs) {
            tabs.addTab(log.getName(), log.getScrollPane());

        }

    }

    public void log(LOGS log, String text) {
        switch (log) {
            case CHAT_LOG:
                chatLog(text);
                break;
            case COMBAT_LOG:
                combatLog(text);
                break;
            case SYS_LOG:
                sysLog(text);
                break;
            default:
                break;
        }
    }

    public void sysLog(String text) {
        sysLog.log(text);
    }

    public void combatLog(String text) {
        combatLog.log(text);
    }

    public void chatLog(String text) {
        chatLog.log(text);
    }

}
