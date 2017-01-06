package main.game.logic.dungeon.editor.gui;

import main.client.cc.gui.neo.tabs.HC_Tab;
import main.client.cc.gui.neo.tabs.HC_TabPanel;
import main.client.cc.gui.neo.tabs.TabChangeListener;
import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.game.logic.dungeon.editor.LE_DataMaster;
import main.game.logic.dungeon.editor.Level;
import main.game.logic.dungeon.editor.LevelEditor;
import main.game.logic.dungeon.editor.Mission;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.editors.ImageChooser;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;
import main.utilities.workspace.Workspace;

import net.miginfocom.swing.MigLayout;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LE_Palette extends G_Panel implements TabChangeListener {
	static final OBJ_TYPES[] default_palette = { OBJ_TYPES.BF_OBJ, OBJ_TYPES.UNITS,
			OBJ_TYPES.CHARS, OBJ_TYPES.ENCOUNTERS, OBJ_TYPES.ITEMS, OBJ_TYPES.ARMOR,
			OBJ_TYPES.WEAPONS, OBJ_TYPES.JEWELRY, };
	static final C_OBJ_TYPE[] multi_types = { C_OBJ_TYPE.UNITS_CHARS, C_OBJ_TYPE.BF_OBJ,
			C_OBJ_TYPE.ITEMS, C_OBJ_TYPE.SLOT_ITEMS, };
	private static final int PAGE_SIZE = 11;
	// default_palette.length + 1;
	private static final String NAME = "Name: ";
	private static final String IMAGE = "Image: ";

	private Mission mission;
	private Level level;
	int brushSize = 1;
	ObjType selectedType;
	HC_TabPanel paletteTabs;
	private G_Component workspaceControlTab;

	List<PaletteWorkspace> palettes;
	private String imagePath;
	private List<PaletteWorkspace> workspaces;

	private PaletteWorkspace activePalette;
	static {
		UPPER_PALETTE.Chars.upper = true;
	}

	public enum UPPER_PALETTE {
		Full(G_PROPS.BF_OBJECT_TYPE, "Structure, Prop, Natural, Special"),
		Props(G_PROPS.BF_OBJECT_TYPE, "Prop", G_PROPS.BF_OBJECT_GROUP, "interior, statues, hanging, magical, mechanical"),
		Structures(G_PROPS.BF_OBJECT_TYPE, "Structure", G_PROPS.BF_OBJECT_GROUP, "wall, structure, columns, statues, ruins, graves"),
		Natural(G_PROPS.BF_OBJECT_TYPE, "Natural", G_PROPS.BF_OBJECT_GROUP, "dungeon, trees, rocks, water, remains, vegetation, crystal"),
		Special(G_PROPS.BF_OBJECT_TYPE, "Special", G_PROPS.BF_OBJECT_GROUP, "entrance, container, treasure, trap, door, light emitter, windows"),
		// Theme(), // castle, outdoor, interior
		// Units(OBJ_TYPES.UNITS, G_PROPS.DEITY,
		// DC_ContentManager.getStandardDeitiesString(", ")),

		Units(OBJ_TYPES.UNITS, G_PROPS.ASPECT, "Neutral", G_PROPS.UNIT_GROUP, "Humans, Knights, Bandits, Greenskins, Dwarves, Undead, Dark, Demons, Animals, "
				+ "Light, Constructs, Magi, North, Critters, Dungeon"),
		Chars(OBJ_TYPES.CHARS, G_PROPS.GROUP, "Preset", G_PROPS.RACE, "Human, Dwarf, Elf, Demon, "
				+ "Goblinoid, Vampire"),
		// ENCOUNTER_SUBGROUP
		All(OBJ_TYPES.UNITS, OBJ_TYPES.ENCOUNTERS, OBJ_TYPES.CHARS),
		// CHARS(OBJ_TYPES.UNITS, OBJ_TYPES.ENCOUNTERS, OBJ_TYPES.CHARS),
		Items(OBJ_TYPES.WEAPONS, OBJ_TYPES.ARMOR, OBJ_TYPES.ITEMS, OBJ_TYPES.JEWELRY),

		;

		public PROPERTY groupProp;
		public String subPalettes;
		public PROPERTY filterProp;
		public String filterValue;
		public OBJ_TYPE[] TYPES;
		public boolean upper;
		OBJ_TYPES TYPE = OBJ_TYPES.BF_OBJ;

		UPPER_PALETTE(PROPERTY filterProp, String filterValue, PROPERTY prop, String subPalettes) {
			this(OBJ_TYPES.BF_OBJ, filterProp, filterValue, prop, subPalettes);
		}

		UPPER_PALETTE(OBJ_TYPES TYPE, PROPERTY filterProp, String filterValue, PROPERTY prop,
				String subPalettes) {
			this.groupProp = prop;
			this.subPalettes = subPalettes;
			this.filterProp = filterProp;
			this.filterValue = filterValue;
			this.TYPE = TYPE;
		}

		UPPER_PALETTE(OBJ_TYPES TYPE, PROPERTY prop, String subPalettes) {
			this(null, null, prop, subPalettes);
			upper = true;
			this.TYPE = TYPE;
		}

		UPPER_PALETTE(PROPERTY prop, String subPalettes) {
			this(OBJ_TYPES.BF_OBJ, prop, subPalettes);
		}

		UPPER_PALETTE(OBJ_TYPE... TYPES) {
			this.TYPES = TYPES;
		}

		public OBJ_TYPE getTYPE() {
			return TYPE;
		}

	}

	public void initPalettes() {
		for (UPPER_PALETTE p : UPPER_PALETTE.values()) {
			PaletteTabPanel upperPalette = new PaletteTabPanel(p);
			paletteTabs.addTab(StringMaster.getWellFormattedString(p.name()), "", upperPalette);

		}

		// TODO subpalettes? use ws ?
		// for (PALETTE p : PALETTE.values())
		// list.add(new Palette(p));
		//
		// for (OBJ_TYPES TYPE : default_palette) {
		// List<ObjType> types = DataManager.getTypes(TYPE);
		// for (ObjType t : types)
		// for (Palette p : list) {
		// if (!checkPaletteForType(p, t))
		// continue;
		// p.add(t);
		// }
		// }
	}

	public LE_Palette() {
		paletteTabs = new HC_TabPanel();
		paletteTabs.setPageSize(PAGE_SIZE);
		initPalettes();
		List<OBJ_TYPES> mergedTypes = new LinkedList<>();
		// for (OBJ_TYPES type : default_palette) {
		// // G_Panel panel = new G_Panel();
		// // panel.add(new JScrollPane(new PaletteList(DataManager
		// // .getTypes(type))));
		// G_Component comp = new PagedPaletteTab(type);
		// if (isGroupingOn()) {
		// Boolean grouping = getTypeGrouping(type);
		// if (grouping != null)
		// if (!grouping) {
		// mergedTypes.add(type);
		// continue;
		// } else {
		// comp = new PaletteTabPanel(type);
		// }
		//
		// }
		// paletteTabs.addTab(type.getName(), type.getImage(), comp);
		//
		// }

		add(paletteTabs);
		paletteTabs.setOpaque(false);
		setOpaque(false);

		paletteTabs.setChangeListener(this);

		loadWorkspaces();
		addAllWorkspaces();
	}

	private boolean isGroupingOn() {
		return false;
	}

	// true/false/null - grouped, merged, solo
	Boolean getTypeGrouping(OBJ_TYPES type) {
		switch (type) {
			case ENCOUNTERS:
				return null;
			case BF_OBJ:
			case UNITS:
			case CHARS:
				return true;
		}
		return false;
	}

	@Override
	public boolean isAutoSizingOn() {
		return true;
	}

	public void createObjGroup() {
		Map<ObjType, Integer> map = new HashMap<>();
		int optionChoice = DialogMaster.optionChoice("Choose object TYPE...", default_palette);
		if (optionChoice == -1)
			return;
		OBJ_TYPE TYPE = default_palette[optionChoice];
		Integer chance = 0;
		boolean randomOff = false;
		while (true) {
			String type = ListChooser.chooseType(TYPE);
			if (type == null)
				break;
			if (!randomOff) {
				chance = DialogMaster
						.inputInt(
								"Set chance for object to be there..."
										+ " (set negative to add '1 object only' rule for this group; use 100+ if more than one object is to be created)",
								chance);
				if (chance == null) {
					Boolean choice = DialogMaster
							.askAndWait("", "Continue", "Random off", "Cancel");
					if (choice == null)
						return;
					if (choice)
						chance = 0;
					else {
						randomOff = true;
						chance = 100;
					}
				}
			}
			map.put(DataManager.getType(type, TYPE), chance);
		}
		boolean addToMainPalette = false;

		String data = null;
		for (ObjType type : map.keySet()) {
			chance = map.get(type);
			if (chance != 100) {

			} // weight map format? =,
			data += type.getName() + ";";
		}
		String path = null;
		String fileName = null;
		XML_Writer.write(data, path, fileName);
		// write to workspace?
	}

	public void newPalette() {
		int optionChoice = DialogMaster.optionChoice("Choose object TYPE...", default_palette);
		OBJ_TYPE TYPE = null;
		List<ObjType> typeList = new LinkedList<>();
		if (optionChoice == -1) {
			if (DialogMaster.confirm("Multi-type Palette?")) {
				optionChoice = DialogMaster
						.optionChoice("Choose object multi TYPE...", multi_types);
				if (optionChoice == -1)
					return;
				TYPE = multi_types[optionChoice];
				typeList = DataManager.getTypes(TYPE);
			} else
				return;
		} else {
			TYPE = default_palette[optionChoice];
			typeList = DataManager.getTypes(TYPE);
		}

		// int index = DialogMaster.optionChoice("Choose object TYPE...",
		// palettes.toArray());
		// PaletteWorkspace ws = palettes.getOrCreate(index);
		List<String> listData = DataManager.toStringList(typeList);
		List<String> secondListData = (TYPE instanceof C_OBJ_TYPE) ? new LinkedList<String>()
				: DataManager.toStringList(typeList);
		// if (ws != null) {
		// secondListData = DataManager.convertToStringList(ws.getTypeList());
		// }
		String data = new ListChooser(listData, secondListData, false, TYPE).choose();
		if (data == null)
			return;
		List<ObjType> list = DataManager.toTypeList(data, TYPE);
		String name = DialogMaster.inputText();
		imagePath = new ImageChooser().launch(imagePath, "");
		PaletteWorkspace ws = new PaletteWorkspace(name, list, imagePath);
		addWorkspaceTab(ws);
		saveWorkspace(ws);
	}

	public void loadWorkspaces() {
		workspaces = new LinkedList<>();
		List<File> files = FileManager.findFiles(FileManager.getFile(getWorkspaceFolder()), ".xml",
				false, false);
		for (File file : files) {
			// String data = FileManager.readFile(file);
			// new PaletteWorkspace(name, typeList, data);
			// )
			Workspace ws = LE_DataMaster.getWorkspaceManager().loadWorkspace(file.getPath());

			workspaces.add(new PaletteWorkspace(ws.getName(), ws.getTypeList(),

			ws.getMetaData()));
		}

	}

	public List<PaletteWorkspace> chooseWorkspaces() {
		List<String> list = new LinkedList<>();
		List<PaletteWorkspace> chosenPalettes = new LinkedList<>();
		for (PaletteWorkspace ws : workspaces) {
			ObjType type = new ObjType(ws.getName());
			type.setOBJ_TYPE_ENUM(OBJ_TYPES.META);
			type.setImage(imagePath);
			list.add(type.getName());
		}
		List<String> chosen = StringMaster.openContainer(new ListChooser(list,
				new LinkedList<String>(), false, OBJ_TYPES.META).choose());
		for (String name : chosen) {
			for (PaletteWorkspace p : palettes) {
				if (p.getName().equals(name))
					chosenPalettes.add(p);
			}
		}

		return chosenPalettes;

	}

	public void addAllWorkspaces() {
		if (workspaces != null)
			for (PaletteWorkspace p : workspaces)
				addWorkspaceTab(p);
	}

	public void addWorkspaces() {
		List<PaletteWorkspace> list = chooseWorkspaces();
		for (PaletteWorkspace p : list)
			addWorkspaceTab(p);
	}

	public void addWorkspaceTab(PaletteWorkspace ws) {
		G_Component comp = new PagedPaletteTab(ws);
		String string = ws.getImagePath();
		if (imagePath == null)
			string = ws.getName();
		paletteTabs.addTab(string, comp);
		refresh();

	}

	public void saveWorkspaces() {
		for (PaletteWorkspace ws : workspaces)
			saveWorkspace(ws);
	}

	public void saveWorkspace(PaletteWorkspace ws) {
		String metadata = ws.getImagePath();
		// String metadata = NAME + ws.getName() + ";";
		// metadata += IMAGE + ws.getImagePath();
		LE_DataMaster.getWorkspaceManager().saveWorkspace(getWorkspaceFolder(), ws, metadata);
	}

	private String getWorkspaceFolder() {
		return PathFinder.getLevelEditorPath() + "workspaces\\palettes\\";
	}

	public void refresh() {
		if (workspaceControlTab == null) {
			workspaceControlTab = new G_Panel(VISUALS.H_LIST_2_8);
			workspaceControlTab.setLayout(new MigLayout(

			"fill, insets 5 2 20 0"));
			CustomButton newButton = new CustomButton("New") {
				public void handleClick() {
					newPalette();
				}
			};
			workspaceControlTab.add(newButton);
			CustomButton addButton = new CustomButton("Add") {
				public void handleClick() {
					addWorkspaces();
				}
			};
			workspaceControlTab.add(addButton);
			CustomButton deleteButton = new CustomButton("Delete") {
				public void handleClick() {
					deletePalettes(false);
				}

				public void handleAltClick() {
					deletePalettes(true);
				}
			};
			workspaceControlTab.add(deleteButton);

			paletteTabs.addTab(
			// PAGE_SIZE - 1, "Custom",
					"Custom", workspaceControlTab);
		}
		paletteTabs.refresh();
	}

	protected void deletePalettes(boolean removeFile) {

		// int i = DialogMaster.optionChoice(paletteTabs.getTabs().toArray(),
		// "choose palette to remove");
		// paletteTabs.removeTab(i);
		List<String> list = new LinkedList<>();
		for (HC_Tab tab : paletteTabs.getTabs()) {
			list.add(tab.getName());
		}
		String string = new ListChooser(SELECTION_MODE.MULTIPLE, list, false).choose();
		for (String ws : StringMaster.openContainer(string)) {
			int i = list.indexOf(ws);
			paletteTabs.removeTab(i);
			if (removeFile)
				FileManager.getFile(getWorkspaceFolder() + ws + ".xml").delete();
		}
		// if (!removeFile) {
		// if (DialogMaster.confirm("Remove File?"))
		// FileManager.getFile(getWorkspaceFolder()).delete();
		// } else

		refresh();

	}

	public Mission getMission() {
		return mission;
	}

	public void setMission(Mission mission) {
		this.mission = mission;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public int getBrushSize() {
		return brushSize;
	}

	public ObjType getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(ObjType selectedType) {
		this.selectedType = selectedType;
		if (selectedType != null)
			LevelEditor.setMouseAddMode(true);
	}

	public class PaletteWorkspace extends Workspace {

		private String imagePath;

		public PaletteWorkspace(String name, List<ObjType> typeList, String imagePath) {
			super(name, typeList, false);
			if (ImageManager.isImage(imagePath))
				this.imagePath = imagePath;
		}

		public String getImagePath() {
			return imagePath;
		}
	}

	public void checkRemoveOrAddToPalette(ObjType selectedType) {
		if (activePalette == null)
			return;
		activePalette.addType(selectedType);
		((PagedPaletteTab) paletteTabs.getSelectedTabComponent()).refresh();
		// TODO update
		// if (activePalette.getTypeList().contains(selectedType)) {
		// }

	}

	public PaletteWorkspace getActivePalette() {
		return activePalette;
	}

	@Override
	public void tabSelected(int index) {

	}

	@Override
	public void tabSelected(String name) {
		activePalette = null;
		for (PaletteWorkspace ws : workspaces) {
			if (ws.getName().equals(name))
				activePalette = ws;
		}

	}
}
