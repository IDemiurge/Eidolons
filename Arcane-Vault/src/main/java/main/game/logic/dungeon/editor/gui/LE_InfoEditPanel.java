package main.game.logic.dungeon.editor.gui;

import main.content.MACRO_OBJ_TYPES;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.game.logic.dungeon.editor.Level;
import main.game.logic.dungeon.editor.LevelEditor;
import main.game.logic.dungeon.editor.Mission;
import main.game.logic.macro.gui.MacroGuiManager;
import main.gui.builders.EditViewPanel;
import main.launch.ArcaneVault;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.GuiManager;

public class LE_InfoEditPanel extends EditViewPanel {
	private ObjType selectedType;
	private Mission mission;
	private Level level;

	// maybe TREE is not necessary for now?

	public LE_InfoEditPanel() {
		super();
		twoTableMode = false;
	}

	@Override
	public void setPanel(G_Panel panel) {
		this.panel = new G_Panel(
		// VISUALS.FRAME_MENU
		);
	}

	protected void initTable(boolean second) {
		super.initTable(second);
		table.setTableHeader(null);
		table.setIgnoreRepaint(true);
		// scrollPane
		// .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	}

	protected int getHeight() {
		return (int) MacroGuiManager.getMapHeight();
	}

	public boolean isMenuHidden() {
		return true;
	}

	protected int getWidth() {
		return (int) ((GuiManager.getScreenWidth() - MacroGuiManager.getMapWidth()) / 2);
	}

	// SYNC WITH MAP?
	public void selectType(ObjType type) {
		type = LevelEditor.checkSubstitute(type);
		ArcaneVault.setSimulationOn(false);
		super.selectType(type);
		ArcaneVault.setSimulationOn(true);
		setSelectedType(type);

	}

	@Override
	public boolean isLevelEditor() {
		return true;
	}

	protected boolean modified(ObjType type, String valName, String newValue) {
		if (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.DUNGEONS) {
			// if (valName.equalsIgnoreCase(G_PROPS.NAME.getName())) {
			// String prevName =
			// LevelEditor.getCurrentLevel().getDungeon().getOriginalName();
			// modify(LevelEditor.getCurrentLevel().getDungeon(), valName,
			// newValue);
			// LevelEditor.getCurrentLevel().getDungeon().setProperty(G_PROPS.DISPLAYED_NAME,
			// prevName);
			// } else
			// if (!valName.equalsIgnoreCase(G_PROPS.NAME.getName()))
			modify(LevelEditor.getCurrentLevel().getDungeon(), valName, newValue);

			if (valName.equalsIgnoreCase(PROPS.MAP_BACKGROUND.getName())) {
				LevelEditor.getMainPanel().setBackgroundImage(newValue);
			} else if (valName.equalsIgnoreCase(PARAMS.BF_HEIGHT.getName())) {
				LevelEditor.getMapMaster().alterSize(false, newValue);
			} else if (valName.equalsIgnoreCase(PARAMS.BF_HEIGHT.getName())) {
				LevelEditor.getMapMaster().alterSize(true, newValue);
			} else if (valName.equalsIgnoreCase(PROPS.COLOR_THEME.getName())) {
				LevelEditor.getCurrentLevel().getDungeon().setColorTheme(null);
			}
		} else

		if (type.getOBJ_TYPE_ENUM() == MACRO_OBJ_TYPES.MISSIONS) {
			modify(
			// overwriteType? :
			LevelEditor.getCurrentMission().getObj().getType() // no custom
																// props
			// LevelEditor.getCurrentMission().getObj()
			, valName, newValue);

		}
		// what about dynamic types and values?
		return false;
	}

	private void modify(Entity entity, String valName, String newValue) {
		entity.setValue(valName, newValue);

	}

	public void resetData(boolean quietly, Entity type) {
		if (type.getOBJ_TYPE_ENUM() == MACRO_OBJ_TYPES.MISSIONS) {
			super.resetData(quietly, LevelEditor.getCurrentMission().getObj());
		} else
			super.resetData(quietly, type);
	}

	public ObjType getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(ObjType selectedType) {
		this.selectedType = selectedType;
		if (selectedType != null) {
			LevelEditor.setMouseAddMode(false);
			if (selectedType.getOBJ_TYPE_ENUM() != OBJ_TYPES.DUNGEONS)
				if (selectedType.getOBJ_TYPE_ENUM() != MACRO_OBJ_TYPES.MISSIONS)
					LevelEditor.setMouseInfoMode(true);
		}
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
}
