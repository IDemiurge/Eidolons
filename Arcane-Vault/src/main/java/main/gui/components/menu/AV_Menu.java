package main.gui.components.menu;

import main.system.auxiliary.StringMaster;

import javax.swing.*;

import static main.gui.components.menu.AV_Menu.MENUS.*;

public class AV_Menu {
    AV_MenuHandler handler = new AV_MenuHandler();
    private JMenuBar bar;
    public static final MENUS[] MENUS= { MENU, EDIT,
            // FILTER,
            // SEARCH
    };
    public static final MENUS[] ALT_MENUS= { WORKSPACE, TRANSFORM, EDIT,
            AE };
    public AV_Menu(boolean alt) {
        bar = new JMenuBar();

        for (MENUS m : alt? ALT_MENUS : MENUS) {
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
        MENU(AV_MENU_ITEMS.ADD_TAB,   AV_MENU_ITEMS.BACKUP ),

        // VERSIONS (CHANGE_LOG, ),
        // SIMULATION(TOGGLE, SET_FLAGS, SHOW_SIM_INFO, ),
        // VIEW(SORT, TREE_FILTER, CUSTOM_VIEW),
        EDIT(
        // REMOVE_BRANCH,
                 ),
        // FILTER(CUSTOM_VIEW, ) ,
        // SEARCH(AV_MENU_ITEMS.FIND_TYPE, ), // btn!

        // INFO(KEYS, ABOUT),
        // UTILS(ART_GEN, ),
        TRANSFORM(AV_MENU_ITEMS.TRANSFORM ),

        AE(AV_MENU_ITEMS.NODE_UP, AV_MENU_ITEMS.NODE_DOWN ),
        WORKSPACE(AV_MENU_ITEMS.ADD_TO_WORKSPACE, AV_MENU_ITEMS.ADD_TO_CUSTOM_WORKSPACE, AV_MENU_ITEMS.LOAD_WORKSPACE,
                AV_MENU_ITEMS.SAVE_WORKSPACE, AV_MENU_ITEMS.SAVE_WORKSPACE_AS, AV_MENU_ITEMS.DELETE_WORKSPACE,
                AV_MENU_ITEMS.RENAME_WORKSPACE, AV_MENU_ITEMS.GROUPING, AV_MENU_ITEMS.SORT_WORKSPACE),
        // TEST(AV_MENU_ITEMS.DC, AV_MENU_ITEMS.HC),
        // TEXT(AV_MENU_ITEMS.GENERATE_MISSING_DESCRIPTIONS)
        ;
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
        // SKILLS,
        // SPELLS,
        // CLASSES,
        // DEITIES,
        // GENERATE_MISSING_DESCRIPTIONS(SKILLS, SPELLS, CLASSES, DEITIES),

        // test
        // PARTY,
        // SELECTED,

        // HC(PARTY, SELECTED, WORKSPACE),
        // DC(PARTY, SELECTED, WORKSPACE),
        // subs
        TOGGLE_SIM,

        // menu
        SAVE,
        COMMIT,
        BACKUP,
        RELOAD,
        ADD_TAB,SAVE_ALL,
        // edit

        NODE_UP,
        NODE_DOWN,
        // FORMULA,

        // GROUP_TOGGLE, // SUB VS GROUP
        // REMOVE_GROUPING,
        // GROUPING_BACK,

        // filter
        CUSTOM_FILTER,
        NEW_FILTER,
        FILTER_WORKSPACE_VIEW,
        // search
        FIND_TYPE,
        // transform
        TRANSFORM(),REMOVE_VALUE,
        // AE
        SAVE_TEMPLATE,

        TOP_LEFT(),

        TOP_RIGHT(),

        ADD_TO_WORKSPACE,
        ADD_TO_CUSTOM_WORKSPACE,
        LOAD_WORKSPACE,
        SAVE_WORKSPACE,
        SAVE_WORKSPACE_AS,
        DELETE_WORKSPACE,
        RENAME_WORKSPACE,
        GROUPING,
        SORT_WORKSPACE,

        ;
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
