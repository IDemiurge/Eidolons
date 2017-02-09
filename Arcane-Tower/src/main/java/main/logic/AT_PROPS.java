package main.logic;

import main.content.CONTENT_CONSTS.PRINCIPLES;
import main.content.ContentManager;
import main.content.Metainfo;
import main.content.OBJ_TYPE;
import main.content.properties.PROPERTY;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.BooleanMaster;

public enum AT_PROPS implements PROPERTY {

	GOALS("Direction", INPUT_REQ.MULTI_TYPE),
	DIRECTION(true, "Goal", INPUT_REQ.SINGLE_TYPE),
	TASKS("Goal", INPUT_REQ.MULTI_TYPE),
	GOAL_TYPE(true, "Goal", INPUT_REQ.SINGLE_TYPE),
	GOAL_STATUS("Goal", INPUT_REQ.SINGLE_TYPE),
	GOAL(true, "Task", INPUT_REQ.SINGLE_TYPE),
	TASK_STATUS("Task", ""),
	TASK_TYPE(true, "Task", INPUT_REQ.SINGLE_ENUM),
	SESSION,
	SESSION_STATUS("Session", INPUT_REQ.SINGLE_ENUM),
	PINNED_TASKS("Session", INPUT_REQ.MULTI_TYPE),
	PINNED_GOALS("Session", INPUT_REQ.MULTI_TYPE),
	WORK_TYPE("Session", INPUT_REQ.SINGLE_TYPE),
	RATE_TYPE(),
	PROMPT_TYPE(),
	WORK_STYLE("Stat", INPUT_REQ.SINGLE_TYPE),
	STATE(),
	DEV_CYCLE("Session", INPUT_REQ.SINGLE_ENUM),
	SESSION_DIRECTIONS("Session", INPUT_REQ.MULTI_TYPE),
	DAY_PLAN("Day", INPUT_REQ.STRING),
	DAY_GOALS("Day", INPUT_REQ.MULTI_TYPE),
	DAY_SESSIONS("Day", INPUT_REQ.MULTI_TYPE),

	PERIOD(),
	SESSION_TYPE,

	AT_VERSION,
	TIME_MARKS, // PER DIR/GOAL/TASK/SESSION
	TASK_DETAILS,
	SESSION_DETAILS,

	REPRODUCED,
	TASK_LINK,
	SEVERITY,
	COMPLEXITY,
	PROJECT_AREA,
	PROBLEM_TYPE,
	LINKED_TASKS,
	COMPLETED_TASKS,
	LINKED_GOALS,
	DAY_TYPE,
	ERA_STATUS,
	STATE_TIMEMARKS,

	PATH("", false, "Track", "Music List", "Script"),
	ARTIST("", false, "Track"),
	TRACKS("", false, "Music List"),
	MUSIC_TYPE("", false, "Track", "Music List"),
	MUSIC_GENRE("", false, "Track", "Music List"),
	MUSIC_TAGS("", true, "Track", "Music List"), ;

    static {
        MUSIC_TAGS.setInputReq(INPUT_REQ.MULTI_ENUM);
    }

    boolean writeToType;
    INPUT_REQ inputReq;
    private Metainfo metainfo;
	private String name;
	private String descr;
	private String entityType;
	private boolean dynamic;
	private String defaultValue;
	private int AV_ID;
	private boolean lowPriority = false;
	private String[] entityTypes;
	private boolean container;
	private boolean superLowPriority = false;
	private boolean highPriority;
	private String fullName;
	private String shortName;

	AT_PROPS(Boolean priority, String entityType, INPUT_REQ inputReq) {
		this(entityType, inputReq);
		setLowPriority(BooleanMaster.isFalse(priority));
		setHighPriority(BooleanMaster.isTrue(priority));
	}

	AT_PROPS(String entityType, INPUT_REQ inputReq) {
		this(entityType, null, "", false, ContentManager.getDefaultEmptyValue(), 0);
		this.inputReq = inputReq;
	}

	AT_PROPS() {
		this(null, ""

		);
	}

	AT_PROPS(String shortName, boolean container, String... entityTypes) {
		this(entityTypes[0], shortName, "", false, ContentManager.getDefaultEmptyValue(), 0);
		this.entityTypes = entityTypes;
		this.container = container;
	}

	AT_PROPS(String entityType, String shortName) {
		this(entityType, shortName, "", false, ContentManager.getDefaultEmptyValue(), 0);
	}

	AT_PROPS(String entityType, String shortName, String descr, boolean dynamic,
			String defaultValue, int AV_ID) {
		this.name = StringMaster.getWellFormattedString(name());
		this.shortName = shortName;
        if (StringMaster.isEmpty(shortName)) {
            this.shortName = name;
        }
        // this.shortName = StringMaster.capitalizeFirstLetter(name()
		// .toLowerCase());
		this.fullName = name();
		this.descr = descr;
		this.entityType = entityType;
		this.dynamic = dynamic;
		this.defaultValue = defaultValue;
		this.AV_ID = AV_ID;
	}

    @Override
    public INPUT_REQ getInputReq() {
        return inputReq;
    }

    public void setInputReq(INPUT_REQ inputReq) {
        this.inputReq = inputReq;
    }

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @return the shortName
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName
	 *            the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the descr
	 */
	public String getDescr() {
		return descr;
	}

	/**
	 * @param descr
	 *            the descr to set
	 */
	public void setDescr(String descr) {
		this.descr = descr;
	}

	/**
	 * @return the entityType
	 */
	public String getEntityType() {
		return entityType;
	}

	/**
	 * @param entityType
	 *            the entityType to set
	 */
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	/**
	 * @return the dynamic
	 */
	public boolean isDynamic() {
		return dynamic;
	}

	/**
	 * @param dynamic
	 *            the dynamic to set
	 */
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	@Override
	public String getDefaultValue() {
		return String.valueOf(defaultValue);
	}

	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the aV_ID
	 */
	public int getAV_ID() {
		return AV_ID;
	}

	/**
	 * @param aV_ID
	 *            the aV_ID to set
	 */
	public void setAV_ID(int aV_ID) {
		AV_ID = aV_ID;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getEntityTypes() {
		return entityTypes;
	}

	@Override
	public Metainfo getMetainfo() {
		return metainfo;
	}

	@Override
	public boolean isContainer() {
		// TODO Auto-generated method stub
		return container;
	}

	public void setContainer(boolean container) {
		this.container = container;
	}

	public boolean isLowPriority() {
		return lowPriority;
	}

	public void setLowPriority(boolean lowPriority) {
		this.lowPriority = lowPriority;
	}

	@Override
	public boolean isSuperLowPriority() {
		return superLowPriority;
	}

	@Override
	public void setSuperLowPriority(boolean superLowPriority) {
		this.superLowPriority = superLowPriority;
	}

	@Override
	public boolean isHighPriority() {
		return highPriority;
	}

	@Override
	public void setHighPriority(boolean highPriority) {
		this.highPriority = highPriority;
	}

    public boolean isPrinciple() {
        for (PRINCIPLES p : PRINCIPLES.values()) {
            if (p.toString().equalsIgnoreCase(getName())) {
                return true;
            }
        }
        return false;
    }

	public boolean isWriteToType() {
		return writeToType;
	}

	public void setWriteToType(boolean writeToType) {
		this.writeToType = writeToType;
	}

	public synchronized String getShortName() {
		return shortName;
	}

    /**
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

	@Override
	public void addSpecialDefault(OBJ_TYPE type, Object value) {

	}

	@Override
	public Object getSpecialDefault(OBJ_TYPE type) {
		return null;

	}

}
