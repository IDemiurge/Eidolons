package main.logic;

import main.content.C_OBJ_TYPE;
import main.content.Metainfo;
import main.content.OBJ_TYPE;
import main.content.parameters.PARAMETER;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public enum AT_PARAMS implements PARAMETER {
	COMPLETENESS(INPUT_REQ.INTEGER, 0, AT_OBJ_TYPE.DIRECTION, AT_OBJ_TYPE.GOAL, AT_OBJ_TYPE.TASK, AT_OBJ_TYPE.SESSION),

	GLORY(INPUT_REQ.INTEGER, 0, AT_OBJ_TYPE.DIRECTION, AT_OBJ_TYPE.GOAL, AT_OBJ_TYPE.TASK, AT_OBJ_TYPE.SESSION),
	TIME_STARTED(INPUT_REQ.INTEGER, 0, AT_OBJ_TYPE.DIRECTION, AT_OBJ_TYPE.GOAL, AT_OBJ_TYPE.TASK, AT_OBJ_TYPE.SESSION),
	TIME_PAUSED(INPUT_REQ.INTEGER, 0, AT_OBJ_TYPE.DIRECTION, AT_OBJ_TYPE.GOAL, AT_OBJ_TYPE.TASK, AT_OBJ_TYPE.SESSION),
	TIME_RESUMED(INPUT_REQ.INTEGER, 0, AT_OBJ_TYPE.DIRECTION, AT_OBJ_TYPE.GOAL, AT_OBJ_TYPE.TASK, AT_OBJ_TYPE.SESSION),

	TIME_SPENT(INPUT_REQ.INTEGER, 0, AT_OBJ_TYPE.DIRECTION, AT_OBJ_TYPE.GOAL, AT_OBJ_TYPE.TASK, AT_OBJ_TYPE.SESSION),
	TIME_ESTIMATED(INPUT_REQ.INTEGER, 0, AT_OBJ_TYPE.GOAL, AT_OBJ_TYPE.TASK),

	TIME_FINISHED(INPUT_REQ.INTEGER, "Task", "Goal"),
	TIME_LAST_MODIFIED("all"),
	TIME_CREATED("all"),
	TIME_TOTAL_ACTIVE(INPUT_REQ.INTEGER, "Task", "Goal"),
	TIME_TOTAL_PAUSED(INPUT_REQ.INTEGER, "Task", "Goal"),
	DEADLINE(INPUT_REQ.INTEGER, "Task", "Goal"),
	TIMES_COMPLETED(INPUT_REQ.INTEGER, "Task"),
	DEFAULT_DURATION,

	TASKS_COMPLETED,
	TASKS_CREATED,
	TASKS_STARTED,
	TASKS_BLOCKED,
	TASKS_MODIFIED,
	TASKS_REMOVED,
	GOALS_COMPLETED(INPUT_REQ.INTEGER, 0, AT_OBJ_TYPE.SESSION),
	SESSIONS_COMPLETED(INPUT_REQ.INTEGER, 0, AT_OBJ_TYPE.DAY),
	SESSION_TIME(INPUT_REQ.INTEGER, 90, AT_OBJ_TYPE.SESSION),
	PRIORITY(INPUT_REQ.INTEGER, "Task", "Goal", "Music List", "Direction"),
	DYNAMIC_PRIORITY(INPUT_REQ.INTEGER, "Task", "Goal", "Music List", "Direction"),

	;
	static {
		DYNAMIC_PRIORITY.setDynamic(true);
	}
	private String name;
	private String shortName;
	private String descr;
	private String entityType;
	private String[] entityTypes;
	private String[] sentityTypes;
	private int AV_ID;
	private int defaultValue;
	private boolean dynamic = false;
	private Metainfo metainfo;
	private boolean lowPriority = false;
	private boolean attr = false;
	private boolean superLowPriority;
	private boolean mastery;
	private boolean highPriority;
	private String fullName;
	private Map<OBJ_TYPE, Object> defaultValuesMap;
	private boolean mod;
	boolean writeToType;
	Color color;
	INPUT_REQ inputReq;

	@Override
	public INPUT_REQ getInputReq() {
		return inputReq;
	}

	public Map<OBJ_TYPE, Object> getDefaultValuesMap() {
		if (defaultValuesMap == null)
			defaultValuesMap = new HashMap<OBJ_TYPE, Object>();
		return defaultValuesMap;
	}

	AT_PARAMS(boolean highPriority, INPUT_REQ inputReq, int defaultValue, AT_OBJ_TYPE... types) {
		this(null, null, false, defaultValue, types.length == 0 ? new String[] { "all" }
				: ListMaster.toStringList(types).toArray(new String[types.length]));
		setHighPriority(highPriority);

	}

	AT_PARAMS(INPUT_REQ inputReq, String... types) {
		this(null, null, false, 0, types);
		this.inputReq = inputReq;
	}

	AT_PARAMS(INPUT_REQ inputReq, int defaultValue, AT_OBJ_TYPE... types) {
		this(null, null, false, defaultValue, types.length == 0 ? new String[] { "all" }
				: ListMaster.toStringList(types).toArray(new String[types.length]));
		this.inputReq = inputReq;
	}

	AT_PARAMS(String shortName, String descr, boolean dynamic, int defaultValue, C_OBJ_TYPE type,
			OBJ_TYPE... types) {
		this(type.getTypes()[0].getName(), shortName, descr, dynamic, defaultValue,
				Integer.MAX_VALUE);
		String[] ENTITY_TYPES = new String[type.getTypes().length + types.length];
		int i = 0;
		for (OBJ_TYPE t : type.getTypes()) {
			ENTITY_TYPES[i] = t.getName();
			i++;
		}
		for (OBJ_TYPE t : types) {
			ENTITY_TYPES[i] = t.getName();
			i++;
		}
		this.entityTypes = ENTITY_TYPES;
	}

	@Override
	public void addSpecialDefault(OBJ_TYPE type, Object value) {
		getDefaultValuesMap().put(type, value);

	}

	@Override
	public Object getSpecialDefault(OBJ_TYPE type) {
		return getDefaultValuesMap().get(type);

	}

	AT_PARAMS(boolean mod, String shortName, String descr, boolean dynamic, int defaultValue,
			String... entityTypes) {
		this(shortName, descr, dynamic, defaultValue, entityTypes);
		this.mod = mod;
	}

	AT_PARAMS(String shortName, String descr, boolean dynamic, int defaultValue,
			String... entityTypes) {
		this(entityTypes[0], shortName, descr, dynamic, defaultValue, Integer.MAX_VALUE);
		this.entityTypes = entityTypes;
	}

	AT_PARAMS(String entityType, String shortName, String descr, boolean dynamic, int defaultValue,
			Color c) {
		this(entityType, shortName, descr, dynamic, defaultValue, Integer.MAX_VALUE);
		this.metainfo = new Metainfo(c);
	}

	AT_PARAMS(String entityType, String shortName, String descr, boolean dynamic, int defaultValue) {
		this(entityType, shortName, descr, dynamic, defaultValue, Integer.MAX_VALUE);
	}

	AT_PARAMS(String entityType, String shortName, String descr, boolean dynamic, int defaultValue,
			int AV_ID) {
		this.name = StringMaster.getWellFormattedString(name());
		this.fullName = name();
		if (shortName == null)
			setShortName(name);
		else
			this.setShortName(shortName);
		this.descr = descr;
		this.entityType = entityType;
		this.dynamic = dynamic;
		this.defaultValue = defaultValue;
		this.AV_ID = AV_ID;
	}

	AT_PARAMS() {
		this.name = StringMaster.getWellFormattedString(name());
	}

	AT_PARAMS(boolean attr) {
		this();
		this.entityType = "chars";

	}

	AT_PARAMS(String str) {
		this(str, null, "", false, 0, Integer.MAX_VALUE);
		inputReq = INPUT_REQ.INTEGER;
	}

	// getOrCreate(base)

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @return the entityTypes
	 */

	public String[] getEntityTypes() {
		return entityTypes;
	}

	@Override
	public Metainfo getMetainfo() {
		return metainfo;
	}

	@Override
	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	@Override
	public String getFullName() {
		return fullName;
	}

	@Override
	public String getDescription() {
		return descr;
	}

	@Override
	public String getEntityType() {

		return entityType;
	}

	@Override
	public String getDefaultValue() {
		return String.valueOf(defaultValue);
	}

	@Override
	public boolean isHighPriority() {
		return highPriority;
	}

	@Override
	public void setHighPriority(boolean highPriority) {
		this.highPriority = highPriority;
	}

	public boolean isLowPriority() {
		return lowPriority;
	}

	public void setLowPriority(boolean lowPriority) {
		this.lowPriority = lowPriority;
	}

	public void setMastery(boolean mastery) {
		this.mastery = mastery;
		this.shortName = shortName.replace("Mastery", "");
	}

	@Override
	public boolean isMastery() {
		return this.mastery;
	}

	@Override
	public boolean isAttribute() {
		return attr;
	}

	public void setAttr(boolean attr) {
		this.attr = attr;
	}

	@Override
	public boolean isSuperLowPriority() {
		return superLowPriority;
	}

	@Override
	public void setSuperLowPriority(boolean superLowPriority) {
		this.superLowPriority = superLowPriority;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Override
	public boolean isWriteToType() {
		return writeToType;
	}

	@Override
	public void setWriteToType(boolean writeToType) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isMod() {
		// TODO Auto-generated method stub
		return false;
	}

}
