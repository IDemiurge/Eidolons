package main.gui.sub;

import main.ArcaneMaster;
import main.ArcaneTower;
import main.data.DataManager;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.enums.StatEnums.TASK_STATUS;
import main.file.CaptureParser;
import main.gui.SessionWindow.VIEW_OPTION;
import main.io.PromptMaster;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PROPS;
import main.logic.CreationHelper;
import main.logic.Goal;
import main.logic.Task;
import main.session.Session;
import main.session.SessionMaster;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;
import main.time.ZeitMaster;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;

public class SessionControlPanel extends G_Panel implements ActionListener {

	private static final String EDIT = "Edit";
	private static final String NEW = "New";
	private static final String REMOVE = "Remove";
	private static final String COMPILE = "Compile";
	private static final String ADD = "Add";
	// private static final String MODIFY_GOAL = "modify goal";
	// private static final String ADD_GOAL = "add goal";
	// private static final String MODIFY_TASK = "modify task";
	// private static final String ADD_TASK = "add task";
	private static final String RESUME = "Resume";
	private static final String PAUSE = "Pause";
	private static final String END_SESSION = "End Session";
	private static final String START = "Start";
	private static final String SAVE = "Save";
	private static final String PIN = "Pin";
	public static final String[] BUTTONS = { START, PAUSE, RESUME, EDIT, NEW, ADD, REMOVE,
			// COMPILE,
			// CHECK_IN, CHECK_OUT, ADD_TASK,
			// MODIFY_TASK, ADD_GOAL, MODIFY_GOAL,
			END_SESSION, SAVE, PIN };
	private Session session;

	public SessionControlPanel(Session session) {
		this.session = session;
		init();
	}

	private void init() {
		int i = 0;
		for (String command : BUTTONS) {
			command = StringMaster.getWellFormattedString(command);
			Component btn = createButton(command);
			add(btn);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean alt = MathMaster.isMaskAlt(e.getModifiers());
		switch (e.getActionCommand()) {
			case SAVE:
				ObjType type = session.getType();
				if (alt) {
					type = new ObjType(session.getType());
					type.setName(type.getName() + " " + SessionMaster.TEMPLATE);
					type.setProperty(AT_PROPS.SESSION_TYPE, SessionMaster.TEMPLATE);
				}
				XML_Writer.writeXML_ForType(type);
				if (alt)
					DialogMaster.inform(session + " saved as Template!");
				break;
			case PIN:
				if (alt) {
					// TODO goals
				}
				List<Task> tasks = PromptMaster.taskPrompt(true, TASK_STATUS.PINNED);
				for (Task t : tasks) {
					t.setStatus(TASK_STATUS.PINNED);
					// t.setProperty(AT_PROPS.SESSION, value)
					session.pinTask(t);
					t.toBase();
				}
				if (DialogMaster.confirm("Set Pinned Custom View?")) {
					session.getWindow().getGoalsPanel().setViewOption(VIEW_OPTION.PINNED);
				}
				break;

			case COMPILE:
				// SessionMaster.createTaskCompilation();
				break;
			case PAUSE:

				session.getWindow().getTimer().pause();
				ZeitMaster.paused(session);
				break;
			case RESUME:
				session.getWindow().getTimer().resume();
				ZeitMaster.resumed(session);
				break;
			case START:
				SessionMaster.sessionStarted(session);
				ZeitMaster.started(session);
				break;
			case END_SESSION:
				SessionMaster.sessionFinished(session);
				ZeitMaster.finished(session);
				break;

			case ADD:
				// TODO
				doAdd(alt);
				break;

			case REMOVE:
				if (ArcaneTower.getSelectedEntity() != null) {
					DataManager.removeType(ArcaneTower.getSelectedEntity().getType());
					ArcaneTower.initDynamicEntities();
				}
				break;
			case EDIT:
				// TODO edit goal?
				List<String> listData = DataManager.getTypeNames(AT_OBJ_TYPE.GOAL);
				List<String> secondListData = DataManager.toStringList(session.getDisplayedGoals());
				if (alt) {

				}
				String result = ListChooser.chooseTypes(AT_OBJ_TYPE.GOAL, listData, secondListData);

				if (result == null) {
					return;
				}
				List<Goal> goals = ArcaneTower.getGoals(DataManager.toTypeList(result,
						AT_OBJ_TYPE.GOAL));
				session.setGoals(goals);
				session.toBase();
				break;
			case NEW:
				Task task = null;
				if (alt) {
				}
				task = (Task) ArcaneTower.getEntity(CreationHelper.create(AT_OBJ_TYPE.TASK));
				Goal goal = // null;
				ArcaneMaster.getCurrentGoal();
				// if (ArcaneTower.getSelectedEntity() instanceof Goal)
				// goal = (Goal) ArcaneTower.getSelectedEntity();
				// else
				// goal = ArcaneMaster.chooseGoal();
				goal.addTask(task);
				break;
		}
		session.getWindow().refresh();
	}

	private void doAdd(boolean alt) {
		if (alt) {
			Goal goal = CaptureParser.parseGoal(DialogMaster
					.inputText("Input Capture formatted text for new Goal..."));
			session.addGoal(goal);
			// if (ArcaneTower.getSelectedEntity() instanceof Task) {
			// addSelectedToTaskGroup();
			// } else if (ArcaneTower.getSelectedEntity() instanceof Goal) {
			// Goal goal = (Goal) ArcaneTower.getSelectedEntity();
			// goal.addTask(task);
			// }
		} else {
			Task task = CaptureParser.parseTask(DialogMaster
					.inputText("Input Capture formatted text for new Task..."));
			ArcaneMaster.getCurrentGoal().addTask(task);

		}
		ArcaneTower.initDynamicEntities();
		// ObjType type;
		// boolean addToSession = ArcaneTower.getSelectedEntity() == null;
		// if (!addToSession)
		// addToSession = !ArcaneTower.getSelectedEntity().canHaveChildren();
		// if (addToSession) {
		// session.addChildren();
		//
		// } else

	}

	private void addSelectedToTaskGroup() {
		ObjType type;
		Task task = (Task) ArcaneTower.getSelectedEntity();
		type = ListChooser.chooseTypeFromSubgroup_(AT_OBJ_TYPE.GOAL,
				CreationHelper.TASK_COMPILATION);
		Goal goal = (Goal) ArcaneTower.getSimulation().getInstance(type);
		goal.addTask(task);
	}

	private Component createButton(String command) {
		JButton btn = new JButton(command);
		btn.setActionCommand(command);
		btn.addActionListener(this);
		return btn;
	}

}
