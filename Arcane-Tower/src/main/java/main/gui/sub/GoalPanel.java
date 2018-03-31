package main.gui.sub;

import main.ArcaneMaster;
import main.ArcaneTower;
import eidolons.client.cc.gui.neo.tree.logic.HT_MapBuilder;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.enums.StatEnums.TASK_STATUS;
import main.io.AT_EntityMouseListener;
import main.logic.AT_OBJ_TYPE;
import main.logic.Goal;
import main.logic.Task;
import main.session.SessionMaster;
import eidolons.swing.components.panels.page.info.element.TextCompDC;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.panels.G_ScrolledPanel;
import main.system.graphics.FontMaster.FONT;
import main.system.auxiliary.data.ListMaster;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GoalPanel extends G_Panel {
    public final static int offsetX = 40;
    private Goal goal;
    private G_ScrolledPanel<Task> tasksPanel;

    // TaskControlPanel;

    public GoalPanel(Goal goal) {
        super("flowy");
        // background =HC_Master.getT3View()
        this.goal = goal;
        panelSize = new Dimension(getWIDTH(), getHEIGHT());
        TextCompDC header = new TextCompDC(null, goal.getName(), getFontSize(), FONT.AVQ,
                getTextColor());

        header.setPanelSize(panelSize);
        add(header);
        header.addMouseListener(new AT_EntityMouseListener(goal));

        // priorityBox = new JComboBox<PRIORITY>();
        // add(priorityBox, "pos @center_x 45");

        tasksPanel = new G_ScrolledPanel<Task>(true, 4, panelSize) {
            @Override
            public List<Task> getData() {
                if (data == null) {
                    data = getGoal().getTasks();
                }
                return data;
            }

            @Override
            protected G_Panel createComponent(Task d) {
                return new TaskComp(d);
            }
        };
        add(tasksPanel);
        tasksPanel.refresh();

    }

    public static int getWIDTH() {
        return HT_MapBuilder.defTreeWidth + offsetX;
    }

    public static int getHEIGHT() {
        return HT_MapBuilder.defTreeHeight;
    }

    @Override
    public void refresh() {
        // isSelected();
        List<Task> tasks = getTaskData();
        tasksPanel.setData(tasks);
        tasksPanel.refresh();
        if (isSelected()) {
            // addButtons();
        }
        // super.refreshComponents();
    }

    private List<Task> getTaskData() {
        List<Task> tasks = goal.getTasks();
        if (isPinnedAppended()) {
            for (ObjType type : DataManager.getTypes(AT_OBJ_TYPE.TASK)) {
                Task task = (Task) ArcaneTower.getEntity(type);
                if (task.getGoal() != null) {
                    if (task.getStatusEnum() == TASK_STATUS.PINNED) {
                        if (task.getGoal().getDirection() == goal.getDirection()) {
                            tasks.add(task);
                        }
                    }
                }
            }
        }
        for (Task task : new ArrayList<>(tasks)) {
            if (!ArcaneMaster.checkDisplayed(task)) {
                tasks.remove(task);
            }
        }
        if (SessionMaster.getSession().isLocked()) {
            tasks = new ListMaster<Task>().getCommonElements(tasks, SessionMaster.getSession()
                    .getTasks());
        }
        return tasks;
    }

//	private void addButtons() {
//		int i = 0;
//		int wrap = getPanelHeight() / getButtonMaxHeight();
//		String pos = "";
//		int x = 0;
//		int y = 0;
//		int width = getButtonMaxWidth();
//		int height = getButtonMaxHeight();
//		for (TASK_COMMAND cmd : TASK_COMMAND.values()) {
//			if (!goal.checkCommandShown(cmd))
//				continue;
//			Image image = cmd.getEmitterPath();
//			if (image == null)
//				image = ImageManager.getEmptyItemIcon(true).getEmitterPath();
//			CustomButton btn = new ActionButtonEnum<GOAL_COMMAND>(null, image, cmd, this);
////			i++;
////			y += height;
////			if (i >= wrap) {
////				y = 0;
////				x += width;
////				pos = "pos " + x + " " + y;
////			}
////			mouseMap.put(new Rectangle(x, y, image.getWidth(null), image.getHeight(null)), cmd);
////			buttonPanel.add(btn, pos);
//		}

    private boolean isPinnedAppended() {
        return true;
    }

    // buttonPanel.revalidate();
    // descrPanel.refresh();
    // headerComp.refresh();
    // super.refresh();
    // panelSize = new Dimension(getPanelWidth(), getPanelHeight());
    private boolean isSelected() {
        return ArcaneTower.getSelectedEntity() == goal;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    private Color getTextColor() {
        return Color.black;
    }

    private int getFontSize() {
        return 28;
    }

    private void addTaskComps(List<Task> list) {
        int i = 0;
        for (Task task : list) {
            TaskComp comp = new TaskComp(task);
            String pos = "";
            if (i % 2 == 0) {
                pos = "wrap";
            }
            add(comp, pos);
        }
    }

    public enum GOAL_COMMAND {
        TOGGLE,

    }

}
