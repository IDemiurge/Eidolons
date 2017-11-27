package main.gui.sub;

import main.content.OBJ_TYPE;
import main.content.values.properties.PROPERTY;
import main.entity.type.ObjType;
import main.file.ReportGenerator.REPORT_TYPE;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PROPS;
import main.logic.ArcaneEntity;
import main.logic.Task;

import java.util.ArrayList;
import java.util.List;

public class ReportView {
    REPORT_TYPE type;
    // timeline? table? selectable comps for tasks, goals, sessions...
    ArcaneEntity entity;

    public ReportView() {
//		entity = getEntityForReport(type);

    }

    private REPORT_TYPE getEntityForReport(REPORT_TYPE type) {
        switch (type) {
            case DAILY:
//				return 	ArcaneTower.getEntity()
            case MILESTONE:
                break;
            case SESSION:
                break;
            case WEEKLY:
                break;

        }
        return null;
    }

    public void init() {
        PROPERTY prop = AT_PROPS.COMPLETED_TASKS;
        OBJ_TYPE TYPE = AT_OBJ_TYPE.TASK;
        List<ObjType> workedTasks = getTypeList(prop, TYPE);
        List<Task> completedTasks = new ArrayList<>();
        List<Task> failedTasks = new ArrayList<>();

        // new ListMaster<>().toList(string)

        // task comps? group by goals
        // time spent,
//		new G_ScrolledPanel<E>() {
//			@Override
//			protected G_Panel createComponent(E d) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		};
        // state
        // style

    }

    private List<ObjType> getTypeList(PROPERTY prop, OBJ_TYPE TYPE) {
//		return DataManager.convertToTypeList(entity.getProperty(prop), TYPE);
        return null;
    }

}
