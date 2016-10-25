package main.game.logic.dungeon.editor;

import main.content.MACRO_OBJ_TYPES;
import main.content.VALUE;
import main.content.parameters.PARAMETER;
import main.content.properties.MACRO_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.logic.dungeon.Location;
import main.game.player.Player;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;

import java.util.Map;

public class Mission {
	DequeImpl<Level> levels = new DequeImpl<>();
	Location location; // is it not the same?
	private String name;
	private Obj obj;
	private ObjType type;

	/*
	 * mission values: 
	 * 
	 * SUBLEVELS 
	 * 
	 * 
	 * location constructed from mission...
	 * 
	 * concrete sublevels 
	 * >> between-levels 
	 * ++ rotations/flipping
	 * 
	 * resolve all random-types 
	 * 
	 * 
	 */

	public Mission(String name) {
		this(name, "");
	}

	public Mission(String baseType, String data) {
		LevelEditor.getMainPanel().setCurrentMission(this);
		this.name = baseType;
		type = DataManager.getType(name, MACRO_OBJ_TYPES.MISSIONS);
		if (type == null)
			throw new RuntimeException();

		// LevelEditor.initSimulation(this);

		obj = new Obj(type, Player.NEUTRAL, LevelEditor.getSimulation(), new Ref());
		Map<VALUE, String> map = new RandomWizard<VALUE>().constructStringWeightMap(data,
				VALUE.class);
		for (VALUE v : map.keySet()) {
			obj.setValue(v, map.get(v));
		}
		obj.toBase();
		// initLevels();

	}

	public void initLevels() {
		for (String levelPath : StringMaster.openContainer(obj
				.getProperty(MACRO_PROPS.DUNGEON_LEVELS))) {
			LE_DataMaster.loadLevel(PathFinder.getDungeonLevelFolder() + levelPath);
		}
	}

	public Location generateLocation() {
		// originally, the idea was to add lots of randomized sublevels, right?
		// maybe it should be included into Mission Parameters!

		return location;

		// that's where playability comes
		// real dungeons with entrances, sublevels
	}

	public String getData() {
		String data = "";
		// obj.setProperty(MACRO_PROPS.DUNGEON_LEVELS,
		// StringMaster.constructContainer(ListMaster
		// .toStringList(levels.toArray())));
		data += XML_Converter.openXmlFormatted("Custom Props");
		for (PROPERTY p : obj.getPropMap().keySet()) {
			String value = obj.getProperty(p);
			if (!StringMaster.compareByChar(value, obj.getType().getProperty(p))) {
				// data += p.getName() + "=" + value + ",";
				data += XML_Converter.wrapLeaf(p.getName(), value);
			}
		}
		data += XML_Converter.closeXmlFormatted("Custom Props");
		data += XML_Converter.openXmlFormatted("Custom Params");
		for (PARAMETER p : obj.getParamMap().keySet()) {
			String value = obj.getParam(p);
			if (!StringMaster.compareByChar(value, obj.getType().getParam(p))) {
				// data += p.getName() + "=" + value + ";";
				data += XML_Converter.wrapLeaf(p.getName(), value);
			}
		}
		data += XML_Converter.closeXmlFormatted("Custom Params");

		// for (Level level : levels) {
		// // level.save();
		// data += level.getXml(); // custom types, dungeon plan params
		// }
		return data;
	}

	public void save() {

		// StringMaster.constructContainer(levels);
		// ListMaster.toStringList(values)
		// place.setProperty(props.mission_data, data);
		// place.setProperty(props.random_sublevel_types, data);
		//
		// // could be multiple perhaps? after all, PLACE != MISSION...
		// for (Level level : levels) {
		// level.save();
		// data += level.getXml(); // custom types, dungeon plan params
		// }

	}

	public void addLevel(Level l) {
		if (levels.contains(l))
			return;
		levels.add(l);
	}

	public String toString() {
		return name;
	}

	public Level getFirstLevel() {
		if (ListMaster.isNotEmpty(levels))
			return levels.get(0);
		return null;
	}

	public String getName() {
		return name;
	}

	public DequeImpl<Level> getLevels() {
		return levels;
	}

	public Location getLocation() {
		return location;
	}

	public Obj getObj() {
		return obj;
	}

	public void removeLevel(Level level) {
		levels.remove(level);

	}
}
