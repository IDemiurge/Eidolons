package main.logic;

import main.entity.Ref;

public class ArcaneRef extends Ref {
	public enum AT_KEYS {
		SESSION, DIRECTION, GOAL, TASK,
	}

	public ArcaneRef(Ref ref) {
		cloneMaps(ref);
	}

	public ArcaneRef(AT_Simulation sim) {
		super(sim);
	}

	public void setObj(AT_KEYS key, Integer val) {
		super.setID(key.toString(), val);
	}

	@Override
	public AT_Simulation getGame() {
		return (AT_Simulation) super.getGame();
	}

	public ArcaneEntity getObj(AT_KEYS key) {
		return getGame().getEntity(getInteger(key.toString()));
	}
}
