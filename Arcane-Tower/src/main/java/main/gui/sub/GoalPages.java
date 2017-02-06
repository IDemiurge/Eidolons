package main.gui.sub;

import main.ArcaneTower;
import main.enums.StatEnums.TASK_STATUS;
import main.gui.SessionWindow.VIEW_OPTION;
import main.logic.AT_PARAMS;
import main.logic.CreationHelper;
import main.logic.Goal;
import main.session.Session;
import main.swing.components.TextComp;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.panels.G_TabbedPagePanel;
import main.system.SortMaster;
import main.system.auxiliary.EnumMaster;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class GoalPages extends G_TabbedPagePanel<Goal> {

	private static final int VERSION = 0;
	private static final int CAPACITY = 1;
	private Session session;
	private VIEW_OPTION viewOption;
	private boolean viewCreated;

	public GoalPages(Session session) {
		super(CAPACITY, false, 1);
		this.session = session;
	}

	@Override
	protected int getArrowOffsetX2() {
		return GoalPanel.getWIDTH() - 80;
	}

    public VIEW_OPTION getViewOption() {
        return viewOption;
    }

	public void setViewOption(VIEW_OPTION viewOption) {
		this.viewOption = viewOption;
		viewCreated = false;
		refresh();
	}

	@Override
	public void flipPage(boolean forward) {
        if (viewOption != null) {
            viewOption = VIEW_OPTION.OFF;
        }
        super.flipPage(forward);
		ArcaneTower.selectEntity(data.get(currentIndex));
	}

	@Override
	public void refresh() {
		if (viewOption == VIEW_OPTION.OFF) {
			session.removeCustomGoals();
		} else if (!viewCreated) {
			if (viewOption != null) {
				// session.removeCustomGoals();
				switch (viewOption) {
					case ALL_TASKS:
						session.addGoal(CreationHelper.getAllSessionTasks(session));
						// if (isGoToLastView())
						// currentIndex = getData().size() - 1;//
						// getData().size() - 1;
						break;
					case OFF:
						currentIndex = 0;
						viewOption = null;
						break;
					case ACTIVE:
					case BLOCKED:
					case PINNED:
					case PENDING:
						TASK_STATUS status = new EnumMaster<TASK_STATUS>().retrieveEnumConst(
								TASK_STATUS.class, viewOption.toString());
						session.addGoal(CreationHelper.getFilteredGoal(status));
						// currentIndex = 0;// getData().size() - 1;
						break;
					case CHOOSE_GROUP:
					case GROUP_LAST:
					case NEW_GROUP:
						Goal groupGoal = CreationHelper.getGroupGoal(viewOption);
                        if (groupGoal == null) {
                            break;
                        }
                        session.addGoal(groupGoal);
						currentIndex = getData().size() - 1;
						break;

				}
				viewCreated = true;
			}
		}
		data = session.getDisplayedGoals();
		setDirty(true);
		super.refresh();
	}

	@Override
	public List<Goal> getData() {
		return session.getDisplayedGoals();
	}

	@Override
	protected Component createTab(Goal sub, int i) {
		TextComp createTab = (TextComp) super.createTab(sub, i);
		if (!session.getGoals().contains(sub)) {
			// createTab.setDefaultFont(defaultFont);
			createTab.setColor(Color.gray);
		}
		return createTab;
	}

	@Override
	protected G_Component createPageComponent(List<Goal> list) {
		G_Panel goalsPanel = new G_Panel();
		for (Goal goal : list) {
            if (goal == null) {
                continue;
            }
            GoalPanel goalPanel = new GoalPanel(goal);
			goalsPanel.add(goalPanel);
		}
		return goalsPanel;
	}

	public GoalPanel getSelectedPanel() {
		int i = 0;
        if (ArcaneTower.getSelectedGoal() != null) {
            i = getData().indexOf(ArcaneTower.getSelectedGoal());
        }
        i = i % getPageSize();
		return (GoalPanel) getCurrentComponent().getComponents()[i];
	}

	@Override
	protected List<List<Goal>> getPageData() {
		List<Goal> goals = new LinkedList<>(session.getDisplayedGoals());
		goals = (List<Goal>) SortMaster.sortByValue(goals, AT_PARAMS.DYNAMIC_PRIORITY);

		return splitList(goals);
	}

}
