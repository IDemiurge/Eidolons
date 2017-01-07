package main.gui.sub;

import main.ArcaneTower;
import main.content.ContentManager;
import main.content.VALUE;
import main.content.properties.G_PROPS;
import main.entity.type.ObjType;
import main.enums.StatEnums.TASK_STATUS;
import main.gui.AT_EntityNode;
import main.io.AT_EntityMouseListener;
import main.io.PromptMaster;
import main.logic.AT_PARAMS;
import main.logic.Task;
import main.swing.components.panels.page.info.element.IconValueComp;
import main.swing.generic.components.editors.ImageChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.swing.generic.services.listener.MouseClickListener;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;
import main.system.auxiliary.StringMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class TaskComp extends AT_EntityNode<Task> implements ActionListener {
	private IconValueComp gloryComp;
	private JComboBox<TASK_STATUS> comboBox;

	public TaskComp(Task e) {
		super(e);
		gloryComp = new IconValueComp(14, FONT.NYALA, AT_PARAMS.GLORY, entity);
		comboBox = new JComboBox<TASK_STATUS>(TASK_STATUS.values());
		resetStatusComboBox();
		comboBox.addActionListener(this);
		headerComp.removeMouseListener(getMouseListener());
		headerComp.addMouseListener(new AT_EntityMouseListener(entity));
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		String pos = "pos 0 image.y2";
		add(gloryComp, pos);
		pos = "id box, @pos max_x max_y@";
		add(comboBox, pos);
		valuePanel.refresh();
	}

	@Override
	protected boolean isCollapsable() {
		return super.isCollapsable();
	}

	@Override
	protected boolean isShowingDetails() {
		return true;
	}

	@Override
	public void refresh() {
		if (entity.getStatusEnum() != comboBox.getSelectedItem()) {
			comboBox.removeActionListener(this);
			resetStatusComboBox();
			comboBox.addActionListener(this);
		}
		gloryComp.refresh();
		imageComp.setImg(entity.getImage());
		super.refresh();

	}

	@Override
	protected void adjustSize() {
		super.adjustSize();
		panelSize = new Dimension(getPanelWidth(), getPanelHeight());
	}

	@Override
	public int getPanelWidth() {
		return GoalPanel.getWIDTH();
	}

	@Override
	public int getPanelHeight() {
		int fontHeight = FontMaster.getFontHeight(getHeaderFont());
		int fontHeight2 = FontMaster.getFontHeight(getDescrFont()) - 2;
		List<String> textLines = descrPanel.getTextLines();
		return 40 + fontHeight + fontHeight2 * (textLines == null ? 1 : textLines.size());
	}

	@Override
	protected String getValuePanelPos() {
		return super.getValuePanelPos();
	}

	@Override
	protected String getDescrText() {
		// TODO Auto-generated method stub
		return super.getDescrText();
	}

	// protected void drawRect(Graphics g, int offset) {
	// g.drawRect(offset + GoalPanel.offsetX, offset, getPanelWidth() - 1 -
	// offset,
	// getPanelHeight() - 1 - offset);
	// }

	private void resetStatusComboBox() {
		comboBox.setSelectedItem(entity.getStatusEnum());
	}

	@Override
	public VALUE[] getDisplayedValues() {
		// TODO G_PROPS.DEV_NOTES
		return new VALUE[] { G_PROPS.DEV_NOTES, AT_PARAMS.TIME_CREATED, AT_PARAMS.TIME_ESTIMATED,
				AT_PARAMS.TIME_SPENT, AT_PARAMS.TIME_CREATED, AT_PARAMS.DEADLINE, };
	}

	protected String getValueText(VALUE v) {
		String value = ContentManager.getFormattedValue(v, entity.getValue(v));
		if (value.isEmpty())
			return "";
		return v.getName().replace("Time ", "") + ": " + value;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		entity.setStatus((TASK_STATUS) comboBox.getSelectedItem());
		ArcaneTower.getSessionWindow().refresh();

	}

	@Override
	protected MouseListener getMouseListener() {
		return new MouseClickListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				handleButtonMouseClick(arg0);
			}
		};
	}

	@Override
	protected String getHeaderText() {
		if (ArcaneTower.getSessionWindow() != null)
			if (entity.getGoal() != ArcaneTower.getSession().getCurrentlyDisplayedGoal())
				if (entity.getGoal().getDirection() != null)
					return StringMaster.wrapInBraces(getDirectionShorthand(entity.getGoal()
							.getDirection().getType()))
							+ " " + super.getHeaderText();

		return super.getHeaderText();
	}

	public static String getDirectionShorthand(ObjType type) {
		String string = StringMaster.getAbbreviation(type.getName());
		if (string.length() > 4)
			string = string.substring(0, 3);
		if (string.length() < 2)
			string = type.getName().substring(0, 2);
		// if (string.length()>4|| string.length()<2)
		// string = StringMaster.getFirstConsonants(type.getName(), 2);
		return string.toUpperCase();
	}

	protected void handleButtonMouseClick(MouseEvent arg0) {
		boolean alt = arg0.isAltDown();

		if (arg0.getSource() == imageComp) {
			String newPath = new ImageChooser()
					.launch(entity.getImagePath(), entity.getImagePath());
			entity.setImage(newPath);
			refresh();
		} else if (arg0.getSource() == gloryComp) {
			Integer i = DialogMaster.inputInt("Set Glory reward...", entity
					.getIntParam(AT_PARAMS.GLORY));
			entity.setParam(AT_PARAMS.GLORY, i);
		} else if (arg0.getSource() == descrPanel) {
			if (!SwingUtilities.isRightMouseButton(arg0))
				if (arg0.getClickCount() == 1)
					return;
			String descr = DialogMaster.inputText("", entity.getProperty(G_PROPS.DESCRIPTION));
			entity.setProperty(G_PROPS.DESCRIPTION, descr);
		} else if (arg0.getSource() == headerComp) {
			if (SwingUtilities.isRightMouseButton(arg0))
				// handleClick(getRightClickCommand(), alt);
				PromptMaster.fillOut(entity, !alt);
			else if (arg0.getClickCount() > 1) {
				// handleClick(getDoubleClickCommand(), alt);
			}
		}
		// else if (arg0.getSource() == buttonPanel)
		// for (Rectangle r : mouseMap.keySet()) {
		// if (r.contains(arg0.getPoint())) {
        // handleClick(mouseMap.getOrCreate(r), alt);
        // return;
        // }
		// }
	}
}
