package main.music.gui;

import main.ArcaneMaster;
import main.ability.effects.Effect.MOD_PROP_TYPE;
import main.content.VALUE;
import main.content.VALUE.INPUT_REQ;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.entity.type.ObjType;
import main.enums.StatEnums.MUSIC_TAGS;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PARAMS;
import main.logic.AT_PROPS;
import main.logic.CreationHelper;
import main.music.MusicCore;
import main.music.MusicListMaster;
import main.music.ahk.AHK_Master;
import main.music.entity.MusicList;
import main.music.gui.MusicMouseListener.CLICK_MODE;
import main.music.gui.MusicMouseListener.PLAY_MODE;
import main.music.m3u.M3uGenerator;
import main.music.m3u.M3uMaster;
import main.swing.SwingMaster;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.EntityFilter;
import main.system.FilterMaster;
import main.system.auxiliary.*;
import main.system.auxiliary.FontMaster.FONT;
import main.system.math.MathMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class MC_ControlPanel extends G_Panel implements ActionListener {

	public static final VALUE[] std_edit_vals = new VALUE[] { AT_PARAMS.PRIORITY, G_PROPS.NAME,
			AT_PROPS.MUSIC_GENRE };
	public static final VALUE[] std_mass_edit_vals = new VALUE[] { AT_PARAMS.PRIORITY,
			AT_PROPS.MUSIC_GENRE, AT_PROPS.MUSIC_TYPE, AT_PROPS.MUSIC_TAGS, };

	public static final PROPERTY[] filter_vals = new PROPERTY[] { AT_PROPS.MUSIC_TYPE,
			AT_PROPS.MUSIC_GENRE, AT_PROPS.MUSIC_TAGS, };
	public static final VALUE[] sortOptions = { AT_PARAMS.PRIORITY, G_PROPS.NAME,
			AT_PARAMS.TIME_CREATED, AT_PROPS.MUSIC_GENRE, AT_PARAMS.TIME_LAST_MODIFIED,
			AT_PROPS.MUSIC_TAGS
	// size,
	};

	public static final String commands = "Visual;New;Dialog;Filter;Prioritize;@Filters;Save;Repair;Edit;Mass Edit;Random;Find;";
	private static List<ObjType> dialogListTypes;
	private static boolean dialogChooseOrRandom;
	private static LinkedList<ObjType> cachedDialogListTypes;
	private JComboBox<CLICK_MODE> clickModeBox;

	private JComboBox<VALUE> sortBox;
	private JComboBox<PLAY_MODE> playBox;
	private JComboBox<String> viewBox;
	private Map<String, MusicListPanel> additionalViews = new XLinkedMap<>();
	private JComboBox<MusicList> lastPlayed;
	private G_Panel btnPanel;
	private G_Panel boxPanel;

	public MC_ControlPanel() {
		super("flowy");

		boxPanel = new G_Panel("flowy");
		String tooltip = "On Click";
		clickModeBox = new JComboBox<CLICK_MODE>(CLICK_MODE.values());
		addBox(clickModeBox, tooltip, false);

		playBox = new JComboBox<PLAY_MODE>(PLAY_MODE.values());
		tooltip = "Play Mode";
		addBox(playBox, tooltip, true);

		sortBox = new JComboBox<VALUE>(sortOptions);
		tooltip = "Sort By";
		addBox(sortBox, tooltip, false);

		lastPlayed = new JComboBox<MusicList>(new Vector(MusicCore.getLastPlayed()));
		tooltip = "Last Played";
		addBox(lastPlayed, tooltip, true);

		// viewBox = new JComboBox<String>(additionalViews.keySet().toArray(
		// new String[additionalViews.size()]));
		// tooltip = "Additional Views";
		// addBox(viewBox, tooltip);

		add(boxPanel);
		btnPanel = new G_Panel("flowy");
		add(btnPanel, "x 100");
		int i = 0;
		for (String cmd : StringMaster.openContainer(commands)) {
			boolean wrap = false;
			if (cmd.contains("@")) {
				wrap = true;
				cmd = cmd.replace("@", "");
			}
			cmd = StringMaster.getWellFormattedString(cmd);
			JButton btn = new JButton(cmd);
			btn.setBackground(Color.black);
			btn.setForeground(Color.white);
			btn.setFont(FontMaster.getFont(FONT.AVQ, 17, Font.PLAIN));
			btn.addActionListener(this);
			btn.setActionCommand(cmd);
			btn.setToolTipText(getToolTipText(cmd));

			String pos = "sg " + i + ",";
			if (wrap) {
				pos = "wrap";
				i++;
			}
			btnPanel.add(btn, pos);

		}

	}

	public static void doDialogLast() {
		try {
			doDialog(false, false, false, null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void doDialog() {
		try {
			doDialog(false, false, false, null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void doDialog(boolean alt, boolean shift, boolean ctrl, MusicList list,
			boolean last) {
		if (!last)
            dialogChooseOrRandom = alt || DialogMaster.confirm("Choose or random?");
        if (!last) {
            dialogListTypes = new LinkedList<>(DataManager.getTypes(AT_OBJ_TYPE.MUSIC_LIST));
            dialogListTypes = filterViaDialog(dialogListTypes, ctrl, shift);
            cachedDialogListTypes = new LinkedList<>(dialogListTypes);
        }
        String result = null;
        if (dialogChooseOrRandom)
			result = ListChooser.chooseType(dialogListTypes).getProperty(AT_PROPS.PATH);
		else {
			ObjType item = new RandomWizard<ObjType>().getRandomListItem(dialogListTypes);
			dialogListTypes.remove(item);
			if (dialogListTypes.isEmpty())
				dialogListTypes = new LinkedList<>(cachedDialogListTypes);

			result = item.getProperty(AT_PROPS.PATH);
		}
		// String listPath = DataManager.getType(result, AT_OBJ_TYPE.MUSIC_LIST
		// )
		// .getProperty(AT_PROPS.PATH);
		PLAY_MODE playMode = MusicMouseListener.getPlayMode();
		if (ctrl)
			playMode = new EnumMaster<PLAY_MODE>().retrieveEnumConst(PLAY_MODE.class, ListChooser
					.chooseEnum(PLAY_MODE.class));
		MusicMouseListener.playM3uList(result, playMode);
	}

	public static void prioritize(boolean ctrl, boolean shift) {
		List<ObjType> types = filterViaDialog(DataManager.getTypes(AT_OBJ_TYPE.MUSIC_LIST), ctrl,
				shift);
		types = ListChooser.chooseTypes_(AT_OBJ_TYPE.MUSIC_LIST, new LinkedList<String>(),
				DataManager.toStringList(types));
		ArcaneMaster.setPriorityTopToBottom(types);
	}

	private static VALUE getMassEditValue() {
		VALUE prop = null;
		int optionChoice = DialogMaster.optionChoice(std_mass_edit_vals, "Value to set?");
		if (optionChoice >= 0)
			prop = std_mass_edit_vals[optionChoice];
		return prop;
	}

	public static List<ObjType> filterViaDialog(List<ObjType> types, boolean ctrl, boolean shift) {
		String filterValue = null;
		PROPERTY filterProp = null;
		boolean filterIn = !shift;
        boolean template = ctrl || DialogMaster.confirm("Template or Custom filter?");
        if (template) {
            // MusicMouseListener.s
            boolean group_tag = DialogMaster.confirm("Group or Tag Template?");
            Object[] values = null;
            if (group_tag) {
                filterProp = AT_PROPS.MUSIC_TYPE;
                values = MusicCore.std_groups[DialogMaster.optionChoice("Choose Template",
						(Object[]) MusicCore.std_groups)];
			} else {
				filterProp = AT_PROPS.MUSIC_TAGS;
				values = MusicCore.std_tags[DialogMaster.optionChoice("Choose Template",
						(Object[]) MusicCore.std_tags)];
			}
			filterValue = StringMaster.constructContainer(ListMaster.toStringList(values));
		} else {
			if (!shift)
				filterIn = DialogMaster.confirm("Filter In or Filter Out?");
			filterProp = filter_vals[DialogMaster.optionChoice(filter_vals, "Filter by...?")];
			filterValue = CreationHelper.getInput(filterProp, null, null, INPUT_REQ.MULTI_ENUM);

		}
		return (List<ObjType>) FilterMaster.filter(types, filterProp.getName(), filterValue,
				AT_OBJ_TYPE.MUSIC_LIST, true, !filterIn, null);
	}

	public static void doFilter(boolean alt, boolean shift) {
		// if (alt) {
		// AHK_Master.getPanel().resetFilter();
		// }
		PROPERTY filterProp = null;
		if (alt)
			filterProp = AT_PROPS.MUSIC_TYPE;
		else {
			int choice = DialogMaster.optionChoice(filter_vals, "Value to Filter by?");
			if (choice >= 0)
				filterProp = filter_vals[choice];
		}
		String filterVal = null;
		if (filterProp != null)
			filterVal = CreationHelper.getInput(filterProp);
		// strict filter
		if (!shift) {
			MusicListPanel panel = MusicCore.getFilterView(filterVal, filterProp);
			// additionalViews.put(panel.getName(), panel); TODO
			AHK_Master.getPanel().getViewsPanel().addView(panel);
			AHK_Master.getPanel().getViewsPanel().viewClicked(panel.getView().getName());
			return;
		}
		AHK_Master.getPanel().setFilterProp(filterProp);
		AHK_Master.getPanel().setFilterValue(filterVal);
		// AHK_Master.getPanel().setFilterOut(shift);

		// if (c)
		// AHK_Master.getPanel().setHighlight_disable_remove(
		// DialogMaster.ask("What to do with filtered lists?", true,
		// "Highlight",
		// "Disable", "Remove"));
		// else
		AHK_Master.getPanel().setHighlight_disable_remove(null);

		AHK_Master.getPanel().setViewAndRefresh(null);
		AHK_Master.getPanel().refresh();
	}

	public static void doFind(boolean alt) {
		String name = DialogMaster.inputText("Query...");
		MusicList list = MusicCore.findList(name);
		if (alt) {
			List<MusicList> musicLists = MusicCore.getMusicLists();
			Collection<MusicList> lists = new EntityFilter<MusicList>(musicLists,
					G_PROPS.NAME.toString(), name, AT_OBJ_TYPE.MUSIC_LIST, true).filter();
			name = ListChooser.chooseString(DataManager.toStringList(lists));
			list = MusicCore.findList(name);
			// choose
		} else {
			// list.play();
		}
		list.getMouseListener().handleClick(0, CLICK_MODE.PLAY);//list.getMouseListener().getClickMode()
        // getOrCreate path for name

		// add to selected?
		// replace selected
		// multiple results
	}

	public static void doSort(ActionEvent e, VALUE sortValue) {
		if (sortValue == AT_PROPS.MUSIC_TAGS) {
			AHK_Master.getWrappingPanel().setSortByTag(!AHK_Master.getPanel().isSortByTag());
		} else {
			AHK_Master.getWrappingPanel().setSortValue(sortValue);
		}
		AHK_Master.getWrappingPanel().setSortDescending(MathMaster.isMaskAlt(e.getModifiers()));
		// TODO by tag?
		AHK_Master.getWrappingPanel().reinitView();

		AHK_Master.getPanel().refresh();
	}

	public static void doMassEdit(boolean alt) {
		VALUE prop = AT_PROPS.MUSIC_TYPE;
		// List<ObjType> types=DataManager.getTypes(AT_OBJ_TYPE. MUSIC_LIST );
		if (!MusicMouseListener.getSelectedLists().isEmpty()) {
			massEdit(DataManager.toTypeList(MusicMouseListener.getSelectedLists()), prop);
		} else
			massEdit(alt);
	}

	public static void massEdit(boolean emptyOnly) {
		List<ObjType> types = DataManager.getTypes(AT_OBJ_TYPE.MUSIC_LIST);
		VALUE prop = getMassFilterValue();
		if (prop != null) {
			String filterValue = inputMassValue(prop);
			for (ObjType sub : new LinkedList<>(types))
				// if (emptyOnly) {
				if (!sub.checkContainerProp((PROPERTY) prop, filterValue, true))
					types.remove(sub);
			// } else if (!generic.checkValue(prop, filterValue))
			// types.remove(generic);

		}
		prop = getMassEditValue();
		if (prop == null)
			return;
		if (emptyOnly) {
			for (ObjType sub : new LinkedList<>(types))
				if (sub.checkValue(prop))
					types.remove(sub);
		}
		massEdit(types, prop);

	}

	public static void massEdit(List<ObjType> types, VALUE prop) {
		// GenericListChooser.s
		types = ListChooser.chooseTypes_(types);
		if (types.isEmpty())
			return;
		String value = inputMassValue(prop);
		if (StringMaster.isEmpty(value))
			return;
		MOD_PROP_TYPE p = new EnumMaster<MOD_PROP_TYPE>().selectEnum(MOD_PROP_TYPE.class);
		// if (prop.getInputReq() == INPUT_REQ.MULTI_ENUM)
		// if (prop instanceof PROPERTY) {
		// add = (DialogMaster.confirm("Add() instead of set() for " +
		// prop.getName() + "?"));
		// }
		for (ObjType sub : (types)) {
			sub.modifyProperty(p, (PROPERTY) prop, value);

		}
	}

	private static VALUE getMassFilterValue() {
		VALUE prop = null;
		int choice = DialogMaster.optionChoice(std_mass_edit_vals, "Value to filter by?");
		if (choice >= 0)
			prop = std_mass_edit_vals[choice];
		return prop;
	}

    // private void playRandom() {
    // randomList = new
    // RandomWizard<E>().getRandomListItem(AHK_Master.getLists());
    // String s = DialogMaster.inputText("", randomList);
    // MusicList list = null;
    // for (list l : AHK_Master.getLists())
    // list = l;
    // if (list != null)
    // AHK_Master.play(list);
    // }

	private static String inputMassValue(VALUE val) {
		return CreationHelper.getInput(val, null, null, INPUT_REQ.MULTI_ENUM);
	}

	public static void doEdit(MusicList list, boolean alt) {
		VALUE val = AT_PROPS.MUSIC_GENRE;

		if (!alt)
			// choose val
			val = std_edit_vals[DialogMaster.optionChoice(std_edit_vals, "What value to edit?")];
		// ListChooser.chooseString(stringList);
		String input = CreationHelper.getInput(val, list, list.getValue(val));
		list.setValue(val, input, true);
	}

	public static void doRandom(boolean alt) {

		List<JButton> buttons = alt ? AHK_Master.getButtonsAll() : AHK_Master
				.getButtonsFromActiveSubPanel();
		Map<MusicList, Integer> map = new HashMap<>();
		for (JButton sub : buttons) {
			MusicMouseListener listener = (MusicMouseListener) sub.getActionListeners()[0];
			Integer priority = listener.getList().getIntParam(AT_PARAMS.PRIORITY);
			map.put(listener.getList(), priority);
		}
		new RandomWizard<MusicList>().getObjectByWeight(map).play();

	}

	public static List<ObjType> getDialogListTypes() {
		return dialogListTypes;
	}

	public static void setDialogListTypes(List<ObjType> dialogListTypes) {
		MC_ControlPanel.dialogListTypes = dialogListTypes;
	}

	public static boolean isDialogChooseOrRandom() {
		return dialogChooseOrRandom;
	}

	public static void setDialogChooseOrRandom(boolean dialogChooseOrRandom) {
		MC_ControlPanel.dialogChooseOrRandom = dialogChooseOrRandom;
	}

    @Override
    public void refresh() {
        Vector vector = new Vector(MusicCore.getLastPlayed());
        lastPlayed.setModel(new DefaultComboBoxModel<MusicList>(vector));
        super.refresh();
    }

    private void addBox(JComboBox<?> box, String tooltip, boolean wrap) {
        box.addActionListener(this);
        G_Panel wrapper = SwingMaster.decorateWithText(tooltip, Color.black, box, "pos 0 20");
        boxPanel.add(wrapper, (wrap ? "wrap" : ""));
    }

    private String getToolTipText(String cmd) {
        switch (cmd) {
            case "Filter":
                return "Alt to Reset filter; Ctrl to Refresh current filtered view; Shift ...";
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clickModeBox) {
            MusicMouseListener.setClickMode((CLICK_MODE) clickModeBox.getSelectedItem());
            return;
        }
        if (e.getSource() == sortBox) {
            doSort(e, (VALUE) sortBox.getSelectedItem());
            return;
        }
        if (e.getSource() == lastPlayed) {
            MusicList list = (MusicList) lastPlayed.getSelectedItem();
            list.getMouseListener()
                    .handleClick(e.getModifiers(), MusicMouseListener.getClickMode());
            return;
        }
        if (e.getSource() == playBox) {
            MusicMouseListener.setPlayMode((PLAY_MODE) playBox.getSelectedItem());
            return;
        }
        MusicList list = MusicMouseListener.getSelectedList();
        boolean alt = MathMaster.isMaskAlt(e.getModifiers());
        boolean shift = MathMaster.isShiftMask(e.getModifiers());
        boolean ctrl = MathMaster.isCtrlMask(e.getModifiers());

        List<ObjType> types;
        switch (e.getActionCommand()) {
			case "Filters":
				doAddRemoveFilters(alt, shift, ctrl, list, false);
				break;

			case "Dialog":
				doDialog(alt, shift, ctrl, list, false);
				break;
            case "Prioritize":
                prioritize(ctrl, shift);
                break;
            case "Repair":
                M3uGenerator.repairM3uLists();
                break;
            case "Find":
                doFind(alt);

                break;
            case "Filter":
                doFilter(alt, shift);
                break;
            case "Visual": //
                if (alt)
                    MusicCore.newGroupView(0);
                else {
                    int choice = DialogMaster.optionChoice("Filtered by Music Type?"
                            // , MusicCore.std_groups
                            , "Full", "Day", "Gym", "Night");
                    if (choice > -1) {
                        MusicCore.newGroupView(choice);
                        break;
                    }

                    choice = DialogMaster.optionChoice("Filtered by Music Tag?", "Day",
                            "Afternoon", "Dusk", "Night");
                    if (choice > -1)
                        MusicCore.newFilteredView(choice, MUSIC_TAGS.class);
                }
                break;
            case "Random":
                doRandom(alt);
                break;
            case "New":
                // "put together similar tracks"
                // TODO from multiple selected lists?
                MusicListMaster.newList(list, alt);
                break;
            case "Save":
                if (!ctrl)
                    MusicCore.saveAll();
                if (alt) {
                    boolean export = DialogMaster.confirm("Export or process?");
                    String inputText = DialogMaster.inputText("folder relative to "
                            + AHK_Master.SYSTEM_LISTS_FOLDER)
                            + "\\";
                    String path = AHK_Master.SYSTEM_LISTS_FOLDER;
                    VALUE p = export ? getMassFilterValue() : getMassEditValue();
                    if (export)
                        M3uMaster.exportListsIntoFolder(path + "export\\" + inputText,
                                (PROPERTY) p, !shift);
                    else {
                        M3uMaster.processMetaListFolder(path + inputText, (PROPERTY) p);
                    }
                }
                break;
            case "Edit":
                doEdit(list, alt);
                break;
            case "Mass Edit":
                // MusicMouseListener.setClickMode(CLICK_MODE.TAG);
                doMassEdit(alt);
                break;
        }

    }

	private void
	doAddRemoveFilters(boolean alt, boolean shift, boolean ctrl,
					   MusicList list, boolean b) {
		if (shift)
			MusicCore.setFilterOut(!MusicCore.isFilterOut());
if (!alt)
		MusicCore.addFilterValue();else
		MusicCore.removeFilterValue();
	}

	public void cycleSort() {
        cycle(sortBox);
    }

    public void cyclePlayMode() {
        cycle(playBox);
    }

    public void cycle(JComboBox<?> box) {
        int i = box.getSelectedIndex() + 1;
        if (i >= box.getModel().getSize())
            i = 0;
        box.setSelectedIndex(i);

    }

}
