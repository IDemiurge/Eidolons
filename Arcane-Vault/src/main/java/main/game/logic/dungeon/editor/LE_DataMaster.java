package main.game.logic.dungeon.editor;

import main.content.CONTENT_CONSTS.DUNGEON_SUBFOLDER;
import main.content.CONTENT_CONSTS.WORKSPACE_GROUP;
import main.content.MACRO_OBJ_TYPES;
import main.content.OBJ_TYPES;
import main.content.PROPS;
import main.content.properties.G_PROPS;
import main.content.properties.MACRO_PROPS;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Writer;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.battlefield.DC_ObjInitializer;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.dungeon.DungeonLevelMaster;
import main.swing.generic.components.editors.FileChooser;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.datatypes.DequeImpl;
import main.system.text.NameMaster;
import main.utilities.workspace.Workspace;
import main.utilities.workspace.WorkspaceManager;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class LE_DataMaster {

	private static final String MISSIONS_WORKSPACE_NAME = "missions workspace";
	private static final FileChooser LEVEL_CHOOSER = new FileChooser(getDungeonLevelFolder());
	private static final FileChooser MISSION_CHOOSER = new FileChooser(getDungeonMissionFolder());
	private static Workspace missionsWorkspace;
	private static WorkspaceManager workspaceManager;

	public static WorkspaceManager getWorkspaceManager() {
		return workspaceManager;
	}

	public static void saveUnitGroup(String name, List<ObjAtCoordinate> units, String pathPrefix) {
		String content = "";
		for (ObjAtCoordinate obj : units) {
			content += DC_ObjInitializer.getObjString(obj) + DC_ObjInitializer.OBJ_SEPARATOR;
		}
		XML_Writer.write(content, PathFinder.getUnitGroupPath() + pathPrefix, name + ".xml");
	}

	public static void initMissionsCustomWorkspace() {
		String path = getMissionsWorkspacePath() + MISSIONS_WORKSPACE_NAME + ".xml";
		File file = FileManager.getFile(path);
		String data = FileManager.readFile(file);
		if (!data.contains(";"))
			return;
		for (String missionName : StringMaster.openContainer(data)) {
			loadMission(getDungeonMissionFolder() + missionName);
		}
	}

	public static List<Level> getLevelsWorkspace() {
		List<Level> levels = new LinkedList<>();
		String path = getLevelsWorkspacePath() + MISSIONS_WORKSPACE_NAME + ".xml";
		File file = FileManager.getFile(path);
		String data = FileManager.readFile(file);
		if (!data.contains(";"))
			return levels;
		for (String missionPath : StringMaster.openContainer(data)) {
			levels.add(new Level(missionPath, null, data));
		}
		return levels;
	}

	public static void initDefaultWorkspaces() {
		// last workspace!
		workspaceManager = new WorkspaceManager(null, null);
		// load them all?
		String path = getPaletteWorkspacePath() + MISSIONS_WORKSPACE_NAME + ".xml";
		try {
			missionsWorkspace = workspaceManager.loadWorkspace(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void missionSaved(Mission mission) {
		missionSaved(mission, true);
	}

	public static void missionSaved(Mission mission, boolean overwrite) {
		// DataManager.addType(type);
		// custom mission-data - into a data file? then combine with Place data
		// XML_Writer
		// for (Level level: mission.getLevels()){
		// }
		String xml = mission.getData();

		int i = 0;
		XML_Writer.write(xml, getDungeonMissionFolder(), mission.getName() + ".xml"); // needed?
		String bossLevel = null;
		String enterLevel = null;
		String sublevels = "";
		String levels = "";
		for (Level level : mission.getLevels()) {
			levelSaved(level);
			if (i == 0)
				// if (level.isBoss())
				enterLevel = level.getPath(); // just sort them always ...
			// if (level.isFirst())
			else if (i == mission.getLevels().size() - 1)
				bossLevel = level.getPath();
			else
				sublevels += level.getPath() + ";"; // getpath!
			i++;
			levels += level.getPath() + ";";
		}
		mission.getObj().setProperty(MACRO_PROPS.PRESET_SUBLEVELS, sublevels, overwrite);
		mission.getObj().setProperty(MACRO_PROPS.BOSS_LEVEL, bossLevel, overwrite);
		mission.getObj().setProperty(MACRO_PROPS.ROOT_LEVEL, enterLevel, overwrite);
		mission.getObj().setProperty(MACRO_PROPS.DUNGEON_LEVELS, levels, overwrite);
		if (overwrite) // name changed
			XML_Writer.writeXML_ForType(LevelEditor.getCurrentMission().getObj().getType());
	}

	public static String getDungeonFolder() {
		return PathFinder.getDungeonFolder();
	}

	public static String getDungeonMissionFolder() {
		return PathFinder.getDungeonMissionFolder();
	}

	public static String getDungeonLevelFolder() {
		return PathFinder.getDungeonLevelFolder();
	}

	public static void edit() {
		ObjType type = LevelEditor.getMainPanel().getInfoPanel().getSelectedType();
		if (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.DUNGEONS)
			editLevel();
		else if (type.getOBJ_TYPE_ENUM() == MACRO_OBJ_TYPES.PLACE)
			editMission();
		else
			editSelectedObj();
	}

	private static void editMission() {
		// TODO random levels,

	}

	private static void editLevel() {
		// TODO default filler

	}

	private static void editSelectedObj() {
		// LevelEditor.getMouseMaster().getSelectedObj();
		// random, ai behavior, other dynamic customizations

	}

	public static void removeSelected() {
		ObjType type = LevelEditor.getMainPanel().getInfoPanel().getSelectedType();
		if (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.DUNGEONS)
			removeLevel(LevelEditor.getCurrentLevel());
		else if (type.getOBJ_TYPE_ENUM() == MACRO_OBJ_TYPES.PLACE)
			removeMission();
		else
			removeSelectedObj();
	}

	private static void removeSelectedObj() {
		LevelEditor.getObjMaster().removeObj(
				LevelEditor.getMouseMaster().getSelectedObj().getCoordinates());
	}

	private static void removeLevel(Level level) {
		if (LevelEditor.getMainPanel().getCurrentMission() != null)
			LevelEditor.getMainPanel().getCurrentMission().removeLevel(level);

		LevelEditor.getSimulation().destruct();
		LevelEditor.levelRemoved(level);
		LevelEditor.getMainPanel().getMapViewComp().removeLevel(level);
	}

	private static void removeMission() {
		Mission mission = LevelEditor.getCurrentMission();
		LevelEditor.getMainPanel().removeMission(mission);
		resetMissionWorkspace();

		for (Level lvl : mission.getLevels()) {

			LevelEditor.getSimulation(lvl).destruct();
		}

	}

	public static void loadLevel() {
		String path = LEVEL_CHOOSER.launch(getDungeonLevelFolder(), "");
		if (path == null)
			return;
		loadLevel(path);
	}

	public static void loadLevel(String path) {
		File file = FileManager.getFile(path);
		if (!file.isFile())
			return;
		String data = FileManager.readFile(file);
		String fileName = StringMaster.cropFormat(StringMaster.getLastPathSegment(path)); // TODO
		String baseDungeonType = VariableManager.removeVarPart(fileName);
		// fileName.split(DungeonLevelMaster.LEVEL_NAME_SEPARATOR)[0];
		if (baseDungeonType.contains(NameMaster.VERSION))
			baseDungeonType = baseDungeonType.split(NameMaster.VERSION)[0];
		Level level = LevelEditor.newLevel(baseDungeonType, data, false);
		level.setName(fileName);

		level.getDungeon().setLevelFilePath(path);

		path = StringMaster.cropFormat(path.replace(fileName, ""));
		level.getDungeon().setProperty(G_PROPS.DUNGEON_SUBFOLDER,
				StringMaster.getLastPathSegment(path));

	}

	public static void loadMission() {
		String path = MISSION_CHOOSER.launch(getDungeonMissionFolder(), "");
		if (path == null || path.equals(getDungeonMissionFolder()))
			return;
		loadMission(path);

	}

	public static void loadMission(String path) {
		File file = FileManager.getFile(path);
		String data = FileManager.readFile(file);
		String baseType = StringMaster.cropFormat(StringMaster.getLastPathSegment(path));
		// TODO getOrCreate name from data!
		Mission mission = new Mission(baseType, data);
		LevelEditor.getMainPanel().newMission(mission);
		resetMissionWorkspace();
		mission.initLevels();
	}

	public static void resetMissionWorkspace() {
		String data = "";
		for (Mission mission : LevelEditor.getMainPanel().getMissions())
			data += mission.getName() + ".xml;";
		XML_Writer.write(data, getMissionsWorkspacePath(), MISSIONS_WORKSPACE_NAME + ".xml");

	}

	private static String getPaletteWorkspacePath() {
		return PathFinder.getLevelEditorPath() + "workspaces\\palette sets\\";
	}

	private static String getLevelsWorkspacePath() {
		return PathFinder.getLevelEditorPath() + "workspaces\\levels\\";
	}

	private static String getMissionsWorkspacePath() {
		return PathFinder.getLevelEditorPath() + "workspaces\\missions\\";
	}

	public static void levelSaved(Level level) {
		String dungeon_main_entrances = "";
		Dungeon dungeon = level.getDungeon();
		if (dungeon.getMainEntrance() != null) {
			dungeon_main_entrances += dungeon.getMainEntrance().getNameAndCoordinate()
					+ DungeonLevelMaster.ENTRANCE_SEPARATOR;
		}
		if (dungeon.getMainExit() != null) {
			dungeon_main_entrances += dungeon.getMainExit().getNameAndCoordinate();
		}
		dungeon.setProperty(PROPS.DUNGEON_MAIN_ENTRANCES, dungeon_main_entrances);

		String subFolder = dungeon.getProperty(G_PROPS.DUNGEON_SUBFOLDER);
		if (subFolder.isEmpty()) {
			subFolder = ListChooser.chooseEnum(DUNGEON_SUBFOLDER.class);
		}
		if (!StringMaster.isEmpty(subFolder)) {
			// subFolder = subFolder.replace(getDungeonLevelFolder(), ""); TODO
			// slashes FAILED!
			subFolder = StringMaster.getLastPathSegment(subFolder);
			subFolder = subFolder.replace(";", "");
			dungeon.setProperty(G_PROPS.DUNGEON_SUBFOLDER, subFolder);
			// path = subFolder + "\\" + path; // by class?
		}
		String xml = level.getXml();
		String fileName = dungeon.getName();

		String path = fileName;
		if (!StringMaster.isEmpty(subFolder))
			path = subFolder + "\\" + path;
		if (dungeon.getWorkspaceGroup() == null) {
			dungeon.setWorkspaceGroup(WORKSPACE_GROUP.IMPLEMENT);

		}
		fileName += InfoMaster.getWorkspaceTip(dungeon);
		if (dungeon.getWorkspaceGroup() == null
				|| dungeon.getWorkspaceGroup() == WORKSPACE_GROUP.COMPLETE) {
			deleteIncompleteVersions(fileName);
		}
		// if (level.getDungeon().isSurface()) {
		// TODO battlefield prefix
		// else {
		// // TODO all in 'sublevels'
		// if (!level.getDungeon().getProperty(PROPS.SUBDUNGEON_TYPE).isEmpty())
		// {
		// path = level.getDungeon().getProperty(PROPS.SUBDUNGEON_TYPE) + "\\" +
		// path;
		// }
		// }
		path += ".xml";
		File file = FileManager.getFile(getDungeonLevelFolder() + "\\" + path); // level.getVersion()

		if (file.isFile())
			if (level.getMission() == null)
				if (!DialogMaster.confirm("Overwrite?")) {
					int version = 2;
					while (true) {
						String newPath = getDungeonLevelFolder() + fileName + NameMaster.VERSION
								+ version + ".xml";
						file = FileManager.getFile(newPath);
						if (!file.isFile())
							break;
						version++;
					}
					path = StringMaster.replaceLast(path, fileName, fileName + NameMaster.VERSION
							+ version);
					fileName += NameMaster.VERSION + version;
				}
		// fileName += ".xml";
		level.setPath(path);
		level.setName(fileName);
		if (!StringMaster.isEmpty(subFolder))
			if (!new File(getDungeonLevelFolder() + subFolder + "\\").exists()) {
				new File(getDungeonLevelFolder() + subFolder + "\\").mkdir();
			}
		FileManager.write(xml, getDungeonLevelFolder() + "\\" + path);
	}

	private static void deleteIncompleteVersions(String fileName) {
		for (WORKSPACE_GROUP ws : WORKSPACE_GROUP.values()) {
			if (ws == WORKSPACE_GROUP.FOCUS)
				continue;
			File file = FileManager.getFile(getDungeonLevelFolder() + "\\" + fileName
					+ InfoMaster.getWorkspaceTip(ws) + ".xml"); // level.getVersion()
			try {
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Workspace getMissionsWorkspace() {
		if (missionsWorkspace == null) {
			missionsWorkspace = new Workspace(MISSIONS_WORKSPACE_NAME, null);
		}
		return missionsWorkspace;
	}

	public static void setMissionsWorkspace(Workspace missionsWorkspace) {
		LE_DataMaster.missionsWorkspace = missionsWorkspace;
	}

	public static DequeImpl<Level> initLevelsForMission(Mission mission) {
		DequeImpl<Level> levels = new DequeImpl<>();
		while (true) {
			// data = loadLevel();
			// Level level = new Level(baseDungeonType, mission, data);
			// level.init();
			String dungeonType = ListChooser.chooseType(OBJ_TYPES.DUNGEONS);
			if (dungeonType == null)
				break;
			Level level = new Level(dungeonType, mission, null);
			levels.add(level);
		}
		// Level surfaceLevel = new Level(dungeonType, mission);
		// Level targetLevel = new Level(dungeonType, mission);
		return levels;
	}

}
