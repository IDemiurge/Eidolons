package main.logic;

import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;

public enum AT_OBJ_TYPE implements OBJ_TYPE {
	TASK("Task", AT_PROPS.TASK_TYPE, 0, true, AT_PROPS.TASK_STATUS),
	GOAL("Goal", AT_PROPS.GOAL_TYPE, 1, true, AT_PROPS.GOAL_STATUS),

	SESSION("Session", AT_PROPS.SESSION_TYPE, 2, false, G_PROPS.GROUP),
	DIRECTION("Direction", G_PROPS.GROUP, 3, true, G_PROPS.GROUP),
	DAY("Day", AT_PROPS.DAY_TYPE, 4, false, G_PROPS.GROUP),
	WEEK("Week", AT_PROPS.DAY_TYPE, 5, false, G_PROPS.GROUP),
	MONTH("Month", AT_PROPS.DAY_TYPE, 6, false, G_PROPS.GROUP),
	ERA("Era", AT_PROPS.DAY_TYPE, 7, false, G_PROPS.GROUP),
	TRACK("Track", AT_PROPS.MUSIC_GENRE, 8, false, G_PROPS.GROUP),
	MUSIC_LIST("Music List", AT_PROPS.MUSIC_TYPE, 9, false, AT_PROPS.MUSIC_GENRE),
	SCRIPT("Script", AT_PROPS.MUSIC_GENRE, 10, false, G_PROPS.GROUP),
	// PROBLEM(){
	//
	// },
	// VISION(){
	//
	// },
	// DAY(){
	//
	//
	// },
	// DECISION(){
	// },
	// RULE(){
	// APPLY_CASES,
	// REMIND_CASES,
	// },
	// PROFILE(){
	// CURRENT_, ASSIGNED, PERFORMANCE,
	// },

	// ASPECT, REPORT,
	;

	private String name;
	private PROPERTY groupingKey;
	private PROPERTY subGroupingKey;
	private PROPERTY upgradeRequirementProp;
	private int code = -1;
	private String image;
	private boolean hidden;
	private PARAMETER param;
	private boolean versioned;

	AT_OBJ_TYPE(String name, PROPERTY groupingKey, int code, boolean versioned, PROPERTY subgroup) {
		this.versioned = versioned;
		this.name = name;
		this.subGroupingKey = G_PROPS.GROUP;
		this.subGroupingKey = subgroup;
		this.code = code;
		this.setImage("UI\\" + name + ".jpg");
		this.groupingKey = groupingKey;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public PROPERTY getGroupingKey() {
		return groupingKey;
	}

	@Override
	public PROPERTY getSubGroupingKey() {
		return subGroupingKey;
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String getImage() {
		return image;
	}

	@Override
	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public void setHidden(boolean hidden) {

	}

	public static PROPERTY getParentValue(AT_OBJ_TYPE blockType) {
		switch (blockType) {
			case GOAL:
				return AT_PROPS.DIRECTION;
			case TASK:
				return AT_PROPS.GOAL;
				// case DAY:
				// return SESSION;

		}
		return null;
	}

	public static PROPERTY getChildValue(AT_OBJ_TYPE blockType) {
		switch (blockType) {
			case GOAL:
				return AT_PROPS.TASKS;
			case DIRECTION:
				return AT_PROPS.GOALS;
			case DAY:
				return AT_PROPS.DAY_SESSIONS;
			case SESSION:
				return AT_PROPS.SESSION_DIRECTIONS;

		}
		return null;
	}

	public PARAMETER getCountParam() {
		switch (this) {
			case GOAL:
				return AT_PARAMS.GOALS_COMPLETED;
			case SESSION:
				return AT_PARAMS.SESSIONS_COMPLETED;
			case TASK:
				return AT_PARAMS.TASKS_COMPLETED;
		}
		return null;
	}

	public static AT_OBJ_TYPE getChildType(AT_OBJ_TYPE TYPE) {
		return TYPE.getChildType();
	}

	public AT_OBJ_TYPE getChildType() {
		switch (this) {
			case DAY:
				return SESSION;
			case DIRECTION:
				return GOAL;
			case GOAL:
				return TASK;
			case SESSION:
				return DIRECTION;
		}
		return null;
	}

	public AT_OBJ_TYPE getParentType() {
		switch (this) {
			case DIRECTION:
				return SESSION;
			case GOAL:
				return DIRECTION;
			case SESSION:
				return DAY;
			case TASK:
				return GOAL;
		}
		return null;
	}

	public VALUE getParentValue() {
		return AT_OBJ_TYPE.getParentValue(this);
	}

	public PROPERTY getChildValue() {
		return AT_OBJ_TYPE.getChildValue(this);
	}

	@Override
	public boolean isTreeEditType() {
		return false;
	}

	@Override
	public PARAMETER getParam() {
		return param;
	}

	@Override
	public PROPERTY getUpgradeRequirementProp() {
		return null;
	}

	@Override
	public boolean isHeroTreeType() {
		return false;
	}

	public boolean isVersioned() {
		return versioned;
	}

}
