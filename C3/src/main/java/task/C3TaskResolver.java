package task;

import framework.C3Handler;
import framework.C3Manager;
import main.data.filesys.PathFinder;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static data.C3Enums.*;

public class C3TaskResolver extends C3Handler {

    private static final String DEVLOG_FOLDER = "devlogs/";
    private static final String DEFAULT_DEVLOG = "chronicle";
    private static final String DEVLOG_EXTENSION = ".docx";
    private static final String TIME_LIMIT = ">>";

    public C3TaskResolver(C3Manager manager) {
        super(manager);
    }

    public boolean resolveTask(C3_Task task) {
        if (task == null) {
            return false;
        }
        File devlog = FileManager.getFile(PathFinder.getRootPath()+DEVLOG_FOLDER + getDevlogName(task) + DEVLOG_EXTENSION);

        String text = task.getText();
        Integer timeLimit = getDefaultTimeLimit(task);
        if (text.contains(TIME_LIMIT)) {
            timeLimit = NumberUtils.getInt(text.substring(text.indexOf(TIME_LIMIT)));
            text = text.substring(0, text.indexOf(TIME_LIMIT));
        }

        try {
            String myString = StringMaster.wrapInBrackets(task.getCategory().toString().toUpperCase()
                    + "::" + task.getSubCategory().toUpperCase()) + " Task: " + text + "\n" + StringMaster.wrapInBrackets(TimeMaster.getFormattedTime(false));
            StringSelection stringSelection = new StringSelection(myString);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);

            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_ALT);
            Desktop.getDesktop().open( devlog);

            CodeTaskStatus status;

            long delay = timeLimit * 60000;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    manager.getTrayHandler().timeElapsed(task);
                }
            }, delay);

        } catch (IOException | AWTException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private Integer getDefaultTimeLimit(C3_Task task) {
        // switch (task.category) {
        // }
        return 15;
    }

    private String getDevlogName(C3_Task task) {
        switch (task.getCategory()) {
            case New_Code:
            case Code_Revamp:
            case Bug_fixing: {
                return getCodingDevlog(task.getSubCategory());
            }
            case Content_Design:
            case System_Design:
            case Global_Game_Design: {
                return getDesignDevlog(task.getSubCategory());
            }
            case Lore_Writing:
            case Game_Content_Writing:
            case Public_Writing: {
                return getWritingDevlog(task.getSubCategory());
            }
            case Team_management: {
                //TODO
                return getWritingDevlog(task.getSubCategory());
            }
        }
        return DEFAULT_DEVLOG;
    }

    private String getDesignDevlog(String subcategory) {
        subcategory = subcategory.split(" ")[0];
        switch (subcategory.toLowerCase(Locale.ROOT)) {
            case "content":
                return "Des Workshop";
            case "system":
                return "Game Design Document";
        }
        return "Game Design Document";
    }

    private String getWritingDevlog(String subcategory) {
        subcategory = subcategory.split(" ")[0];
        switch (subcategory.toLowerCase(Locale.ROOT)) {
            case "backlog":
                return "Backlog";
            case "lore":
                return "Dev Logs/WR_Devlog";
            case "meta":
                return "Meta";
        }
        return "Writing";
    }

    private String getCodingDevlog(String subcategory) {
        subcategory = subcategory.split(" ")[1];
        switch (subcategory.toLowerCase(Locale.ROOT)) {
            case "av":
                return "Dev Logs/AV_Devlog";
            case "dc":
                return "Dev Logs/DC_Devlog";
            case "nf":
                return "Dev Logs/NF_Devlog";
            case "c3":
            case "meta":
                return "Dev Logs/Meta_Devlog";
            case "le":
                return "Dev Logs/LE_Devlog";
        }
        return "Writing";
    }

    public void promptTaskInput(C3_Task task) {
        String input = JOptionPane.showInputDialog("Any comments for [" + task.getText() + "] ?");
        manager.getQLogger().done(task, input);
    }


    private void promptInput(C3_Task task) {
        /*

         */
        CodeTaskStatus status =
                manager.getTaskManager().getStatus(task.getText());
        status = new EnumMaster<CodeTaskStatus>().
                getNextEnumConst(CodeTaskStatus.class, status);
        String string = JOptionPane.showInputDialog("What is [" + task.getText() +
                "] new status?", status.toString());
        status = new EnumMaster<CodeTaskStatus>().
                retrieveEnumConst(CodeTaskStatus.class, string);
        if (status == CodeTaskStatus.Complete) {
            //done
        }
        manager.getTaskManager().upgradeStatus(task, status);
    }
}
