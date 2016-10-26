package main.logic.entity;

import main.entity.type.ObjType;
import main.logic.AT_PARAMS;
import main.logic.AT_PROPS;
import main.logic.ArcaneEntity;
import main.session.Session;

public class Day extends ArcaneEntity {

	public Day(ObjType type) {
		super(type);
	}

	public void sessionDone(Session session) {
		incrementParam(AT_PARAMS.SESSIONS_COMPLETED);
		addProperty(AT_PROPS.DAY_SESSIONS, session.getName());

	}

}
