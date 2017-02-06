package main.gui.components.menu;

import main.system.auxiliary.StringMaster;

import javax.swing.*;

public class AV_Menu {
	AV_MenuHandler handler = new AV_MenuHandler();
    private JMenuBar bar;

	public AV_Menu() {
		// G_Panel with a custom background perhaps, would be nice to have a bit
		// of art in AV
		bar = new JMenuBar();
		for (MENUS m : MENUS.values()) {
			JMenu menu = new JMenu(m.getName());
			for (MENU_ITEMS i : m.getItems()) {
				if (i.hasSubMenu()) {
					JMenu menuItem = getMenu(i);
					menu.add(menuItem);

				} else {
					JMenuItem menuItem = new JMenuItem(i.getName());
					menu.add(menuItem);
				}
				// fileMenu.setMnemonic(KeyEvent.VK_F);
			}
			bar.add(menu);
		}
		// panel.add(bar);
	}

	private JMenu getMenu(MENU_ITEMS i) {
		JMenu menu = new JMenu(i.getName());
		// menu.addActionListener(handler);
		// menu.addMenuListener(new AV_MenuHandler(i.getName(), true));
		for (MENU_ITEMS sub : i.getItems()) {
			if (sub.hasSubMenu()) {
				JMenu menuItem = getMenu(sub);
				menuItem.addActionListener(handler);
				// menu.addMenuListener(new AV_MenuHandler(i.getName(), true));
			} else {
				JMenuItem menuItem = new JMenuItem(sub.getName());
				menuItem.setActionCommand(i.getName());
				menuItem.addActionListener(handler);

				// menu.addMenuListener(new AV_MenuHandler(i.getName(), true));
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
		MENU(MENU_ITEMS.SAVE, MENU_ITEMS.AUTO_SAVE, MENU_ITEMS.BACKUP, MENU_ITEMS.SIMULATION),

		EDIT(MENU_ITEMS.ADD, MENU_ITEMS.UPGRADE, MENU_ITEMS.DELETE,

		MENU_ITEMS.UNDO, MENU_ITEMS.REDO, MENU_ITEMS.BACK, MENU_ITEMS.FORWARD, MENU_ITEMS.NODE_UP, MENU_ITEMS.NODE_DOWN, MENU_ITEMS.FORMULA, MENU_ITEMS.SET_VALUE),

		WORKSPACE(MENU_ITEMS.ADD_TO_WORKSPACE, MENU_ITEMS.ADD_TO_CUSTOM_WORKSPACE, MENU_ITEMS.LOAD_WORKSPACE, MENU_ITEMS.SAVE_WORKSPACE, MENU_ITEMS.SAVE_WORKSPACE_AS, MENU_ITEMS.DELETE_WORKSPACE, MENU_ITEMS.RENAME_WORKSPACE, MENU_ITEMS.GROUPING, MENU_ITEMS.SORT_WORKSPACE),

		FILTER,

		SEARCH(MENU_ITEMS.FIND_TYPE),

		TRANSFORM,

		A_E,
		TEST(MENU_ITEMS.DC, MENU_ITEMS.HC),
		TEXT(MENU_ITEMS.GENERATE_MISSING_DESCRIPTIONS);
		// ++ AE MENU?
		private MENU_ITEMS[] items;

		MENUS(MENU_ITEMS... items) {
			this.items = items;
		}

		public String getName() {
			return StringMaster.getWellFormattedString(name());
		}

		public MENU_ITEMS[] getItems() {
			return items;
		}
	}

	public enum MENU_ITEMS {
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
		ADD_TO_CUSTOM_WORKSPACE

		,
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

		;
		private MENU_ITEMS[] items;

		// boolean customGeneratedItems
		MENU_ITEMS(MENU_ITEMS... items) {
			this.items = items;

		}

		public boolean hasSubMenu() {
            if (items == null) {
                return false;
            }
            if (items.length < 1) {
                return false;
            }
            return true;
		}

		public String getName() {
			return StringMaster.getWellFormattedString(name());
		}

		public MENU_ITEMS[] getItems() {
			return items;
		}

	}

}
