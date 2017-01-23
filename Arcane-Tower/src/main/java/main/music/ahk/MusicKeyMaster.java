package main.music.ahk;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import javafx.util.Pair;
import main.content.VALUE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.enums.StatEnums.MUSIC_TAG_GROUPS;
import main.enums.StatEnums.MUSIC_TYPE;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PROPS;
import main.music.MusicCore;
import main.music.entity.MusicList;
import main.music.gui.MC_ControlPanel;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.RandomWizard;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MusicKeyMaster implements HotkeyListener {
    private static final int keyIdCycleView = 100;
    private static final int keyIdCyclePlayMode = 101;
    private static final int keyIdCycleSort = 102;
    private static final int keyIdRandomActive = 110;
    private static final int keyIdRandomView = 111;
    private static final int keyIdRandomAll = 112;
    private static final int keyIdRandomChoice = 113;
    private static final int keyIdDialog = 120;
    private static final int keyIdDialogLast = 121;
    private static final int keyIdDialogLastToggle = 122;
    private static final int keyIdFind = 123;
    MUSIC_TYPE[] bind_types = {

    };
    /*
                                                    * cycle active panel (for alt)
													* cycle view
													*
													*/
    private boolean keysOn;

    @Override
    public void onHotKey(final int aIdentifier) {
        new Thread(new Runnable() {
            public void run() {
                if (aIdentifier >= 1000) {
                    playRandomFromGroupHotkey(aIdentifier);
                    return;
                }
                if (!checkSpecialHotkey(aIdentifier))
                    return;

                int mod = getModFromHotkeyId(aIdentifier);
                int i = aIdentifier % 10;
                play(i, mod, aIdentifier);
            }
        }).start();
    }

    protected int getModFromHotkeyId(int aIdentifier) {
        return getMod(aIdentifier / 10);
    }

    private int getMod(int mod) {
        switch (mod) {
            case 0:

                return JIntellitype.MOD_ALT;
            case 1:
                return JIntellitype.MOD_CONTROL;
            case 2:
                return JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT;
            case 3:
                return JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT;
            case 4:
                return JIntellitype.MOD_SHIFT + JIntellitype.MOD_ALT;
            case 5:
                return JIntellitype.MOD_ALT + JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT;
            case 6:
                return JIntellitype.MOD_ALT + JIntellitype.MOD_WIN + JIntellitype.MOD_SHIFT;
            case 7:
                return JIntellitype.MOD_WIN + JIntellitype.MOD_SHIFT;
        }
        return JIntellitype.MOD_ALT;
    }

    protected void play(int i, int mod, int aIdentifier) {
        List<List<JButton>> lists = AHK_Master.getButtonLists();
        int topIndex = aIdentifier / 10;

        // inverse i? if (mod== ?)
        try {
            lists.get(topIndex).get(i - 1).doClick();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void registerMusicKeys() {
        for (int i = 1; i < 60; i++) {
            int mod = getModFromHotkeyId(i);
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(Character.forDigit(i == 10 ? 0
             : i % 10, 10));
            if (mod == JIntellitype.MOD_CONTROL)
                if (i % 10 == 1)
                    continue;// eclipse :)
            JIntellitype.getInstance().registerHotKey(i, mod, keyCode);
        }
        int i = 1;// win+f1 taken by windows help...
        // GYM!
        JIntellitype.getInstance().registerHotKey(1000,
         JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT, getFx(0));
        for (MUSIC_TYPE n : MUSIC_TYPE.values()) {
            if (getFx(i) == 0)
                break;
            JIntellitype.getInstance().registerHotKey(1000 + i, JIntellitype.MOD_WIN, getFx(i));
            i++;
        }
        i = 0;
        for (MUSIC_TAG_GROUPS n : MUSIC_TAG_GROUPS.values()) {
            if (getFx(i) == 0)
                break;
            JIntellitype.getInstance().registerHotKey(2000 + i, JIntellitype.MOD_SHIFT, getFx(i));
            i++;
        }
        i = 0;
        for (MUSIC_TYPE n : MUSIC_TYPE.values()) {
            if (getFx(i) == 0)
                break;
            JIntellitype.getInstance().registerHotKey(3000 + i,
             JIntellitype.MOD_WIN+JIntellitype.MOD_SHIFT, getFx(i));
            i++;
        }
        i = 0;
        for (MUSIC_TAG_GROUPS n : MUSIC_TAG_GROUPS.values()) {
            if (getFx(i) == 0)
                break;
            JIntellitype.getInstance().registerHotKey(4000 + i,
             JIntellitype.MOD_WIN+JIntellitype.MOD_CONTROL, getFx(i));
            i++;
        }

        JIntellitype.getInstance().registerHotKey(keyIdDialog,
         JIntellitype.MOD_ALT + JIntellitype.MOD_WIN, KeyEvent.VK_SPACE);
        JIntellitype.getInstance().registerHotKey(keyIdDialogLast,
         JIntellitype.MOD_ALT + JIntellitype.MOD_WIN + JIntellitype.MOD_SHIFT,
         KeyEvent.VK_SPACE);
        JIntellitype.getInstance().registerHotKey(keyIdDialogLastToggle,
         JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT, KeyEvent.VK_SPACE);

        JIntellitype.getInstance().registerHotKey(keyIdCycleView, getMod(4), KeyEvent.VK_SPACE);
        // JIntellitype.getInstance().registerHotKey(keyIdCyclePlayMode,
        // getMod(5),
        // KeyEvent.VK_SPACE);
        // JIntellitype.getInstance().registerHotKey(keyIdCycleSort, getMod(6),
        // KeyEvent.VK_SPACE);
        JIntellitype.getInstance().registerHotKey(keyIdCyclePlayMode, getMod(5), KeyEvent.VK_F2);
        JIntellitype.getInstance().registerHotKey(keyIdCycleSort, getMod(6), KeyEvent.VK_F3);

        // JIntellitype.getInstance().registerHotKey(keyIdCycleView, getMod(6),
        // KeyEvent.VK_SPACE);

        int keyRandom  = KeyEvent.VK_MINUS;
        JIntellitype.getInstance().registerHotKey(keyIdRandomActive, getMod(1), keyRandom);
        JIntellitype.getInstance().registerHotKey(keyIdRandomView, getMod(2), keyRandom);
        JIntellitype.getInstance().registerHotKey(keyIdRandomAll, getMod(3), keyRandom);
        JIntellitype.getInstance().registerHotKey(keyIdRandomChoice, getMod(4), keyRandom);

        int keyFind = KeyEvent.VK_F2;
        JIntellitype.getInstance().registerHotKey(keyIdFind, getMod(1), keyFind);
//		JIntellitype.getInstance().registerHotKey(keyIdFind, getMod(2), keyFind);

    }

    private int getFx(int i) {
        switch (i) {
            case 0:
                return KeyEvent.VK_F1;
            case 1:
                return KeyEvent.VK_F2;
            case 2:
                return KeyEvent.VK_F3;
            case 3:
                return KeyEvent.VK_F4;
            case 4:
                return KeyEvent.VK_F5;
            case 5:
                return KeyEvent.VK_F6;
            case 6:
                return KeyEvent.VK_F7;
            case 7:
                return KeyEvent.VK_F8;
            case 8:
                return KeyEvent.VK_F9;
            case 9:
                return KeyEvent.VK_F10;
        }
        return 0;
    }

    protected void playRandomFromGroupHotkey(int aIdentifier) {
        playFromGroupHotkey(aIdentifier, true);
    }

    protected void playFromGroupHotkey(int aIdentifier, boolean random) {

        int i = aIdentifier % 1000;
        if (aIdentifier>=3000){
            random=false;
            aIdentifier-=2000;
        }
        boolean tag = aIdentifier >= 2000;
        Object c = tag ? MUSIC_TAG_GROUPS.values()[i] : MUSIC_TYPE.values()[i];

        List<ObjType> types = null;

        if (random) {
            types = new LinkedList<>(DataManager.getTypes(AT_OBJ_TYPE.MUSIC_LIST));
            Collections.shuffle(types);
        }else
            types =    new LinkedList<>() ;

        for (ObjType type :random?  types :DataManager.getTypes(AT_OBJ_TYPE.MUSIC_LIST)) {
            if (checkRandomGroup(type, c, tag, random))
                if (random) {
                    new MusicList(type).play();
                    return;
                } else types.add(type);
        }
        ObjType type = ListChooser.chooseType(types);
        if (type != null)
            new MusicList(type).play();
    }

    private boolean checkRandomGroup(ObjType type, Object c, boolean tag, boolean random) {
        if (random)
            for (Pair<VALUE, String> pair : MusicCore.getFilterValues()) {
            if (MusicCore.isFilterOut()) {
            if (type.checkValue(pair.getKey(), pair.getValue()))
                return false;
            }else
            if (!type.checkValue(pair.getKey(), pair.getValue()))
                return false;
        }
        AT_PROPS PROP = tag ? AT_PROPS.MUSIC_TAGS : AT_PROPS.MUSIC_TYPE;
        if (tag) {
            return type.checkContainerProp(PROP, c.toString().replace("_", ";"), true);
        }
        return type.checkProperty(PROP, c.toString());
    }

    protected boolean checkSpecialHotkey(int aIdentifier) {
        switch (aIdentifier) {
            case keyIdDialog:
                return dialog();
            case keyIdDialogLast:
                return dialogLast();
            case keyIdDialogLastToggle:
                MC_ControlPanel.setDialogChooseOrRandom(!MC_ControlPanel.isDialogChooseOrRandom());
                return dialogLast();
            case keyIdCycleSort:
                return cycleSort();
            case keyIdCycleView:
                return cycleView();
            case keyIdCyclePlayMode:
                return cyclePlayMode();

            case keyIdRandomView:
                return randomFromView();
            case keyIdRandomActive:
                return randomFromActive();
            case keyIdRandomAll:
                return randomFromAll();
            case keyIdRandomChoice:
                return randomChoice();
            case keyIdFind:
                return find();
        }
        return true;
    }

    private boolean find() {
        AHK_Master.getPanel().getControlPanel().doFind(true);
        return true;
    }

    private boolean dialogLast() {
        MC_ControlPanel.doDialogLast();
        return false;
    }

    private boolean dialog() {
        MC_ControlPanel.doDialog();
        return false;
    }

    private boolean cyclePlayMode() {
        AHK_Master.getPanel().getControlPanel().cyclePlayMode();
        return false;
    }

    private boolean cycleView() {
        AHK_Master.getPanel().getViewsPanel().cycleView();
        return false;
    }

    private boolean cycleSort() {
        AHK_Master.getPanel().getControlPanel().cycleSort();
        return false;
    }

    private boolean randomChoice() {
        // TODO choose subpanel/tag/group...
        return false;
    }

    private boolean randomFromAll() {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean randomFromView() {
        List<JButton> list = new RandomWizard<List<JButton>>().getRandomListItem(AHK_Master
         .getButtonLists());
        new RandomWizard<JButton>().getRandomListItem(list)

         .doClick();
        return false;
    }

    private boolean randomFromActive() {
        new RandomWizard<JButton>().getRandomListItem(AHK_Master.getButtonsFromActiveSubPanel())
         .doClick();
        return false;
    }

    public void initKeys() {
        try {
            JIntellitype.getInstance();
            JIntellitype.getInstance().addHotKeyListener(this);
            registerMusicKeys();

            keysOn = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggle() {
        JIntellitype.getInstance().removeHotKeyListener(this);
    }

}
