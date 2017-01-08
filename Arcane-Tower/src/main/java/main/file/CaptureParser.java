package main.file;

import main.ArcaneTower;
import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.enums.StatEnums.TASK_TYPE;
import main.io.PromptMaster;
import main.logic.*;
import main.session.Session;
import main.session.SessionMaster;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.SortMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class CaptureParser {

	private static final String BLOCK_DESCRIPTION_SEPARATOR = "�";
	private static final String BLOCK_DESCRIPTION_SEPARATOR_ALT = " - ";
	private static final String DETAILS_SEPARATOR = ">>";
	private static final String DETAILS_VALUE_SEPARATOR = "=";
	static boolean updateMode = true;
	static Boolean promptsMode = null;
	static List<Goal> goals;
	static List<Task> tasks;
	static Direction direction;
    private static String SUPER_HEADER = "^";
    private static String HEADER = "*";
    private static String BLOCK_SEPARATOR = "::";
    private static String directionHeader;
    private static ObjType enclosingType;

	private static void initScope(AT_OBJ_TYPE scope) {

	}

	public static void initUpdateCapture(String data) {
		// TODO add() ,
	}

	public static void initSessionCapture(Session session, String data) {
		// TODO directly into session to save...
		// first parse goals/tasks... keep them in lists ...

		init();
		// if (promptsMode)
		parse(data, AT_OBJ_TYPE.GOAL);
		direction = null;
		boolean multiDirection = false;
		if (CaptureParser.directionHeader != null) {
			direction = (Direction) ArcaneTower.getEntity(AT_OBJ_TYPE.DIRECTION,
					CaptureParser.directionHeader);
		} else {
			for (Goal sub : goals)
				if (sub.getDirection() != null) {
					direction = sub.getDirection();
					multiDirection = true;
					break;
				}
		}
		if (direction == null)
			PromptMaster.chooseDirection(session);
		session.setDirection(direction);
		session.setGoals(goals);
		session.setTasks(tasks);
		session.setMultiDirection(multiDirection);
		ArcaneTower.initDynamicEntities();
		cleanUp();
	}

	private static void init() {
		goals = new LinkedList<>();
		tasks = new LinkedList<>();
	}

	private static void cleanUp() {
		direction = null;
		goals = null;
		tasks = null;
		promptsMode = null;
		CaptureParser.directionHeader = null;
	}

	private static void checkAddDynamicObjToList(ObjType type) {
		if (type.getOBJ_TYPE_ENUM() == AT_OBJ_TYPE.TASK)
			if (tasks != null)
				tasks.add((Task) ArcaneTower.getSimulation().getInstance(type));

		if (type.getOBJ_TYPE_ENUM() == AT_OBJ_TYPE.GOAL)
			if (goals != null)
				goals.add((Goal) ArcaneTower.getSimulation().getInstance(type));

	}

	public static String autodetectTaskGroup(String text, AT_OBJ_TYPE TYPE) {
		for (TASK_TYPE t : new EnumMaster<TASK_TYPE>().getEnumList(TASK_TYPE.class)) {
			if (StringMaster.contains(text, t.name(), true, false))
				return t.prefix + " " + text;
		}
		return text;

	}

	public static void newCapture(AT_OBJ_TYPE scope) {
		init();
		promptsMode = null;
		// updateMode = DialogMaster.confirm("Update mode?");
		if (scope == null) {
			AT_OBJ_TYPE[] array = { AT_OBJ_TYPE.DIRECTION, AT_OBJ_TYPE.GOAL };
			int i = DialogMaster.optionChoice("What's the upper scope of this capture?", array);
			if (i == -1)
				return;
			scope = array[i];
		}
		initScope(scope);
		String data = DialogMaster.inputText("<Paste capture text>").trim();
		parse(data, scope);
		if (DialogMaster.confirm("Save now?"))
			ArcaneTower.saveAll();
		prioritize();
		cleanUp();

	}

	private static void prioritize() {
		SortMaster.sortByValue(tasks, AT_PARAMS.PRIORITY, true);
		SortMaster.sortByValue(goals, AT_PARAMS.PRIORITY, true);

	}

	public static ObjType parse(String data, AT_OBJ_TYPE scope) {
		if (scope == AT_OBJ_TYPE.DIRECTION) {
			for (String part : StringMaster.openContainer(data, SUPER_HEADER)) {
				if (part.isEmpty())
					continue;
				String[] blocks = part.split(Pattern.quote(HEADER));
				directionHeader = blocks[0];
				try {
					parse(part, AT_OBJ_TYPE.GOAL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			ObjType type = DataManager.getType(directionHeader, AT_OBJ_TYPE.DIRECTION);
			checkAddDynamicObjToList(type);
			return type;
		}
		if (data.contains(HEADER)) {
			for (String block : StringMaster.openContainer(data, HEADER)) {
				if (block.isEmpty())
					continue;
				if (block.equals(directionHeader))
					continue;
				parse(block, scope);
				checkAddDynamicObjToList(enclosingType);
			}
			return enclosingType;
		}
		String[] blocks = data.split(BLOCK_SEPARATOR);
		String header = blocks[0];

		// String type = TextParser.extractBraceEnclosed(header); TODO

		enclosingType = getType(scope, header, null, updateMode);
		if (enclosingType == null)
			return null;

		for (String block : blocks) {
			if (block == blocks[0])
				continue;
			try {
				parseBlock(block, scope, header, enclosingType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// XML_Writer.backUp();
		return enclosingType;
	}

	public static Goal parseGoal(String inputText) {
		return (Goal) ArcaneTower.getEntity(parse(inputText, AT_OBJ_TYPE.GOAL));
	}

	public static Task parseTask(String inputText) {
		return (Task) ArcaneTower.getEntity(parseBlock(inputText, AT_OBJ_TYPE.GOAL, null,
				SessionMaster.getSession().getCurrentlyDisplayedGoal().getType()));

	}

	private static ObjType parseBlock(String data, AT_OBJ_TYPE scope, String header,
			ObjType enclosingType) { // static header?!
		String group = getGroupPrefix(scope, data);
		data = data.replace(group, "");
		String[] parts = data.split(BLOCK_DESCRIPTION_SEPARATOR);
		if (parts.length == 1)
			parts = data.split(BLOCK_DESCRIPTION_SEPARATOR_ALT);
		String name = formatBlockName(parts);
		String description = "";
		if (parts.length > 1)
			description = parts[1].trim();
		// String[] lines = blockValue.split(DETAILS_SEPARATOR);
		// if (lines.length > 1) {
		// blockValue = blockValue.substring(0,
		// blockValue.indexOf(DETAILS_SEPARATOR));
		// }
		String[] lines = StringMaster.getLastPart(data, DETAILS_SEPARATOR).split(";");
		group = getGroupForPrefix(group, scope.getChildType());
		if (data.indexOf(DETAILS_SEPARATOR) > 0)
			data = data.substring(0, data.indexOf(DETAILS_SEPARATOR));
		ObjType type = createType(enclosingType, scope, header, name, description, lines, group);

		try {
			XML_Converter.getDoc(XML_Writer.getTypeXML(type));
			DataManager.addType(type);
			main.system.auxiliary.LogMaster.log(1, "Capture type added " + type + " from text"
					+ data);
		} catch (Exception e) {
			e.printStackTrace();
			main.system.auxiliary.LogMaster.log(1, "*********Broken Capture type from text: "
					+ data);
		}
		return type;
	}

	private static String getGroupForPrefix(String group, AT_OBJ_TYPE scope) {
		if (scope == AT_OBJ_TYPE.TASK) {
			for (TASK_TYPE t : TASK_TYPE.values()) {
				if (group.equals(t.prefix)) {
					return StringMaster.getWellFormattedString(t.toString());
				}
			}
		}
		return null;
	}

	private static String getGroupPrefix(AT_OBJ_TYPE parent_scope, String header) {
		if (parent_scope == AT_OBJ_TYPE.GOAL) {
			for (TASK_TYPE t : TASK_TYPE.values()) {
				if (header.contains(t.prefix)) {
					return t.prefix;
				}
			}
		}
		return "";
	}

	private static void initGroup(ObjType type, String group) {
		AT_OBJ_TYPE TYPE = (AT_OBJ_TYPE) type.getOBJ_TYPE_ENUM();
		type.setProperty(TYPE.getGroupingKey(), group);
	}

	private static String formatBlockName(String[] parts) {
		String string = parts[0].trim();
		string = string.replace(("�"), "-");
		string = string.replace(("�"), "...");
		string = string.replace(("?"), "...");
		string = string.replace(("�"), "'");
		string = string.replace(("�"), "'");
		// String numbers = "";
		// while (true) {
		// int i = StringMaster.getFirstNumberIndex(string);
		// if (i != 0) {
		// numbers += "" + string.charAt(0);
		// break;
		// }
		// string = string.substring(1);
		// }
		if (StringMaster.getFirstNumberIndex(string) == 0)
			// DUMMY_CHAR;
			string = "Task: " + string;
		return string;
	}

	private static ObjType createType(ObjType enclosingType, AT_OBJ_TYPE scope, String header,
			String blockName, String blockValue, String[] lines, String group) {
		AT_OBJ_TYPE blockType = getBlockType(scope);
		ObjType type = getType(blockType, blockName, enclosingType, updateMode);
		if (type == null)
			return null;
		initGroup(type, group);
		// copy from template type per parent
		if (enclosingType != null)
			type.setValue(AT_OBJ_TYPE.getParentValue(blockType), enclosingType.getName());
		// group/generic
		for (String valString : lines) {
			if (StringMaster.isEmpty(valString))
				continue;
			String[] parts = valString.split(DETAILS_VALUE_SEPARATOR);
			String name = parts[0];
			String value = parts[1];
			type.setValue(name, value);
		}
		type.setImage(getImage(type));
		type.setProperty(G_PROPS.DESCRIPTION, blockValue);
		checkAddDynamicObjToList(type);
		return type;
	}

	private static String getImage(ObjType type) {
		String imgPath = ArcaneTower.IMG_PATH + type.getOBJ_TYPE_ENUM() + "\\"
				+ type.getGroupingKey();
		if (!ImageManager.isImage(imgPath))
			imgPath = ArcaneTower.IMG_PATH + type.getOBJ_TYPE_ENUM() + "\\default.";

		return imgPath;// return
        // DataManager.getTypes(type.getOBJ_TYPE_ENUM()).getOrCreate(0).getImagePath();
    }

	private static ObjType getType(AT_OBJ_TYPE scope, String header, ObjType enclosingType,
			boolean update) {
		ObjType type = DataManager.getType(header, scope);
		if (type == null) {
			type = new ObjType(header, scope); // prompts
			DataManager.addType(type);
		} else {
			if (!update) {
				header = DialogMaster
						.inputText(
								"Name already exists! Delete to turn on Update Mode or input a different name!",
								header + " New");
				if (StringMaster.isEmpty(header)) {
					updateMode = true;
					return type;
				}
				type = new ObjType(header, scope);
				DataManager.addType(type);
			}
		}
		if (enclosingType == null)
			if (CaptureParser.directionHeader != null) {
				enclosingType = DataManager.getType(CaptureParser.directionHeader, scope
						.getParentType());
				if (enclosingType == null)
					enclosingType = DataManager.addType(CaptureParser.directionHeader, scope
							.getParentType());
			} else
			// group = CreationHelper.getInput(groupValue, type, group);
			{
				if (promptsMode == null)
					promptsMode = DialogMaster.confirm(type
							+ " has no enclosingType; choice prompts on?");
				if (promptsMode)
					enclosingType = ListChooser.chooseTypeFromSubgroup_(scope.getParentType(), "");
                // else enclosingType = DataManager.getTypes(scope).getOrCreate(0);
            }
        if (enclosingType != null)
            type.setProperty(AT_OBJ_TYPE.getParentValue((AT_OBJ_TYPE) type.getOBJ_TYPE_ENUM()),
                    enclosingType.getName());
        return type;
    }

	private static AT_OBJ_TYPE getBlockType(AT_OBJ_TYPE scope) {
		switch (scope) {
			case DIRECTION:
				return AT_OBJ_TYPE.GOAL;
			case GOAL:
				return AT_OBJ_TYPE.TASK;
				// case SESSION:
				// return;
				// case TASK:
				// return;
		}
		return null;
	}

}
