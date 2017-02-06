package main.gui;

import main.ArcaneTower;
import main.content.ContentManager;
import main.file.CaptureParser;
import main.file.MainWriter;
import main.logic.AT_OBJ_TYPE;
import main.logic.CreationHelper;
import main.session.SessionMaster;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.auxiliary.EnumMaster;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GatewayButtonHandler implements ActionListener {

	public static final String GATEWAY = "Gateway";
	public static final String LAUNCH_AV = "Launch Av";
	public static final String CLEAN_UP = "Clean Up";
	public static final String CONTINUE_SESSION = "Continue Session";
	public static final String CREATE_TYPE = "Create Type";
	public static final String SAVE_TYPES = "Save Types";
	public static final String NEW_SESSION = "New Session";
	public static final String MAKE_REPORT = "Make Report";
	public static final String DIRECTIONS = "Directions...";
	public static final String TASKS = "Tasks...";
	public static final String GOALS = "Goals...";
	public static final String TOGGLE_EDIT = "Toggle Edit";
	public static final String TOGGLE_VISUAL = "Toggle Visual";
	public static final String PARSE_CAPTURE = "Parse Capture";
	public static final String[] BUTTONS = { NEW_SESSION, CONTINUE_SESSION, MAKE_REPORT,
			CREATE_TYPE, CLEAN_UP,
			// DIRECTIONS, TASKS, GOALS,
			// TOGGLE_EDIT, TOGGLE_VISUAL,
			PARSE_CAPTURE, SAVE_TYPES, LAUNCH_AV, GATEWAY };
	public GatewayWindow window;

	public GatewayButtonHandler(GatewayWindow gatewayWindow) {
		this.window = gatewayWindow;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		handleCommand(command, e.getModifiers());

	}

	public void handleCommand(String command, int i) {
		AT_OBJ_TYPE type;
		boolean alt = ActionEvent.ALT_MASK == (i & ActionEvent.ALT_MASK);
		switch (command) {
			case PARSE_CAPTURE:
                if (alt) {
                    CaptureParser.newCapture(AT_OBJ_TYPE.DIRECTION);
                }
                CaptureParser.newCapture(null);
				break;
			case GATEWAY:
				window.getView().refresh();

				break;

			case NEW_SESSION:
				SessionMaster.newSession(alt);
				break;
			case CONTINUE_SESSION:
				SessionMaster.continueSession(alt);
				break;
			case LAUNCH_AV:
				ArcaneTower.launchAV();
				break;
			case CLEAN_UP:
				if (alt) {
					String choice = ListChooser.chooseEnum(AT_OBJ_TYPE.class,
							SELECTION_MODE.MULTIPLE);
					List<AT_OBJ_TYPE> types = new EnumMaster<AT_OBJ_TYPE>().getEnumList(
							AT_OBJ_TYPE.class, choice);
					MainWriter.cleanUp(true, types.toArray(new AT_OBJ_TYPE[types.size()]));
                } else {
                    MainWriter.cleanUpAll();
                }
                break;
			case CREATE_TYPE:
				String chooseEnum = ListChooser.chooseEnum(AT_OBJ_TYPE.class);
				type = (AT_OBJ_TYPE) ContentManager.getOBJ_TYPE(chooseEnum);
				CreationHelper.create(type);
				break;
			case SAVE_TYPES:
				ArcaneTower.saveAll();
				break;
		}
	}
}
