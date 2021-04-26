package main.gui.components.menu;

import main.system.auxiliary.StringMaster;

import javax.swing.*;
import java.util.Arrays;

public class AV_Menu {
    AV_MenuHandler handler = new AV_MenuHandler();
    private JMenuBar bar;

    public AV_Menu() {
        // G_Panel with a custom background perhaps, would be nice to have a bit
        // of art in AV
        bar = new JMenuBar();
        // MENUS.TOP
        for (MENUS m : MENUS.values()) {
            JMenu menu = new JMenu(m.getName());
            for (AV_MENU_ITEMS i : m.getItems()) {
                if (i.hasSubMenu()) {
                    JMenu menuItem = getMenu(i);
                    menu.add(menuItem);

                } else {
                    JMenuItem menuItem = new JMenuItem(i.getName());
                    menuItem.addActionListener(handler);
                    menu.add(menuItem);
                }
                // fileMenu.setMnemonic(KeyEvent.VK_F);
            }
            bar.add(menu);
        }
        // panel.add(bar);
    }

    private JMenu getMenu(AV_MENU_ITEMS i) {
        JMenu menu = new JMenu(i.getName());
        for (AV_MENU_ITEMS sub : i.getItems()) {
            if (sub.hasSubMenu()) {
                getMenu(sub);
            } else {
                JMenuItem menuItem = new JMenuItem(sub.getName());
                menuItem.setActionCommand(i.getName());
                menuItem.addActionListener(handler);
                menu.add(menuItem);
            }
        }
        return menu;
    }

    public JMenuBar getBar() {
        return bar;
    }

    public void setBar(JMenuBar bar) {
        this.bar = bar;
    }

    public enum MENUS {
        MENU(AV_MENU_ITEMS.SAVE, AV_MENU_ITEMS.AUTO_SAVE, AV_MENU_ITEMS.BACKUP, AV_MENU_ITEMS.SIMULATION),

        EDIT(AV_MENU_ITEMS.ADD, AV_MENU_ITEMS.UPGRADE, AV_MENU_ITEMS.DELETE,

                AV_MENU_ITEMS.UNDO, AV_MENU_ITEMS.REDO, AV_MENU_ITEMS.BACK, AV_MENU_ITEMS.FORWARD, AV_MENU_ITEMS.NODE_UP, AV_MENU_ITEMS.NODE_DOWN, AV_MENU_ITEMS.FORMULA, AV_MENU_ITEMS.SET_VALUE),

        WORKSPACE(AV_MENU_ITEMS.ADD_TO_WORKSPACE, AV_MENU_ITEMS.ADD_TO_CUSTOM_WORKSPACE, AV_MENU_ITEMS.LOAD_WORKSPACE, AV_MENU_ITEMS.SAVE_WORKSPACE, AV_MENU_ITEMS.SAVE_WORKSPACE_AS, AV_MENU_ITEMS.DELETE_WORKSPACE, AV_MENU_ITEMS.RENAME_WORKSPACE, AV_MENU_ITEMS.GROUPING, AV_MENU_ITEMS.SORT_WORKSPACE),

        FILTER,

        SEARCH(AV_MENU_ITEMS.FIND_TYPE),

        TRANSFORM,

        A_E,
        TEST(AV_MENU_ITEMS.DC, AV_MENU_ITEMS.HC),
        TEXT(AV_MENU_ITEMS.GENERATE_MISSING_DESCRIPTIONS);
        // ++ AE MENU?
        private final AV_MENU_ITEMS[] items;

        MENUS(AV_MENU_ITEMS... items) {
            this.items = items;
        }

        public String getName() {
            return StringMaster.format(name());
        }

        public AV_MENU_ITEMS[] getItems() {
            return items;
        }
    }

    public enum AV_MENU_ITEMS {
        // text
        SKILLS,
        SPELLS,
        CLASSES,
        DEITIES,
        GENERATE_MISSING_DESCRIPTIONS(SKILLS, SPELLS, CLASSES, DEITIES),

        // test
        PARTY,
        SELECTED,
        WORKSPACE,

        HC(PARTY, SELECTED, WORKSPACE),
        DC(PARTY, SELECTED, WORKSPACE),
        // subs
        WRAP_NODE,
        TOGGLE_AS,
        PERIOD,
        TOGGLE_SIM,

        // menu
        SAVE,
        COMMIT,
        AUTO_SAVE(TOGGLE_AS, PERIOD), // period?
        BACKUP,
        SIMULATION(TOGGLE_SIM),
        RELOAD,
        // edit
        ADD, // FROM TEMPLATE or *EMPTY*?
        UPGRADE,
        DELETE,

        UNDO,
        REDO,
        BACK,
        FORWARD,
        NODE_UP,
        NODE_DOWN,
        FORMULA,
        SET_VALUE,
        // workspace

        ADD_TO_WORKSPACE,
        ADD_TO_CUSTOM_WORKSPACE,
        LOAD_WORKSPACE,
        SAVE_WORKSPACE,
        SAVE_WORKSPACE_AS,
        DELETE_WORKSPACE,
        RENAME_WORKSPACE,
        GROUPING,
        SORT_WORKSPACE,

        GROUP_TOGGLE, // SUB VS GROUP
        REMOVE_GROUPING,
        GROUPING_BACK,

        // filter
        CUSTOM_FILTER,
        NEW_FILTER,
        FILTER_WORKSPACE_VIEW,
        // search
        FIND_TYPE,
        // transform
        REMOVE_VALUE,
        // AE
        SAVE_TEMPLATE,
        REMOVE,ADD_TAB,SAVE_ALL,PREVIEW,
        NEW(), CLONE(), TRANSFORM(), PASTE(), COPY(), TOGGLE(), MAIN(
                NEW, CLONE,
                REMOVE, UPGRADE,
                PREVIEW, UNDO,
                SAVE_ALL, SAVE,
                ADD_TAB, TOGGLE,
                COPY, PASTE,
                BACKUP, TRANSFORM

        );
        private final AV_MENU_ITEMS[] items;

        // boolean customGeneratedItems
        AV_MENU_ITEMS(AV_MENU_ITEMS... items) {
            this.items = items;

        }

        @Override
        public String toString() {
            return getName();
        }

        public boolean hasSubMenu() {
            if (items == null) {
                return false;
            }
            return items.length >= 1;
        }

        public String getName() {
            return StringMaster.format(name());
        }

        public AV_MENU_ITEMS[] getItems() {
            return items;
        }

    }

}
