package main.logic.value;

import main.content.ContentManager;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.ValuePageManager;
import main.content.parameters.G_PARAMS;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.enums.StatEnums;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PARAMS;
import main.logic.AT_PROPS;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.TimeMaster;

import java.util.ArrayList;
import java.util.Arrays;

public class AT_ContentManager extends ContentManager {

	public AT_ContentManager(boolean dcSync) {
		super();
	}

	@Override
	public void init() {
		super.init();
		contentInit();
	}

	public enum TIME_PERIOD {
		DAY, HOUR, WEEK, MONTH, YEAR

	}

	public int getPeriodMaxValueToDisplay(TIME_PERIOD v) {
		switch (v) {
			case DAY:
				return 6;
			case YEAR:
				return 999;
			case MONTH:
				return 11;
			case HOUR:
				return 23;
			case WEEK:
				return 3;
		}
		return 0;
	}

	@Override
	public String getFormattedVal(VALUE v, String value) {
		if (v instanceof AT_PARAMS) {
			AT_PARAMS param = (AT_PARAMS) v;
			if (v.name().startsWith("TIME_")) {
				boolean time = false;
				return TimeMaster.getFormattedDate(time, value);
				// " ago"
			}
			switch (param) {
				case TIME_CREATED:

			}
		}
		return super.getFormattedVal(v, value);
	}

	@Override
	public boolean checkAllApplies(VALUE p, String type) {
		if (p instanceof G_PARAMS) {
			G_PARAMS param = (G_PARAMS) p;
			return checkParamForType(param, type);
		}
		if (p instanceof G_PROPS) {
			G_PROPS prop = (G_PROPS) p;
			return checkPropForType(prop, type);
		}
		return super.checkAllApplies(p, type);
	}

	private boolean checkPropForType(G_PROPS prop, String type) {
		switch (prop) {

		}
		return true;
	}

	private boolean checkParamForType(G_PARAMS param, String type) {
		switch (param) {

		}
		return true;
	}

	public boolean isTextAlwaysShownInListItems(OBJ_TYPE TYPE) {
		if (TYPE instanceof AT_OBJ_TYPE) {
			AT_OBJ_TYPE at_OBJ_TYPE = (AT_OBJ_TYPE) TYPE;
			switch (at_OBJ_TYPE) {
				case TASK:
					return true;
				case GOAL:
					return true;
			}
		}
		return false;
	}

	public void contentInit() {

		ArrayList<PARAMETER> params = new ArrayList<PARAMETER>();
		params.addAll(Arrays.asList(G_PARAMS.values()));
		params.addAll(Arrays.asList(AT_PARAMS.values()));

		ArrayList<PROPERTY> props = new ArrayList<PROPERTY>();
		props.addAll(Arrays.asList(G_PROPS.values()));
		props.addAll(Arrays.asList(AT_PROPS.values()));

		ContentManager.init(props, params);
		ContentManager.setParamEnumClasses(new Class[] { G_PARAMS.class, AT_PARAMS.class });
		ContentManager.setPropEnumClasses(new Class[] { G_PROPS.class, AT_PROPS.class });

		ValuePageManager.init();

		EnumMaster.setALT_CONSTS_CLASS(StatEnums.class);
	}
}
