package main.gui.components.table;

import main.content.properties.G_PROPS;
import main.data.TableDataManager;
import main.gui.builders.EditViewPanel;
import main.launch.ArcaneVault;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class AV_TableCellRenderer extends DefaultTableCellRenderer {

	private static final FONT FONT_TYPE = FONT.NYALA;
	private boolean widthSet = false;
	private Map<Object, Component> cache = new HashMap<Object, Component>();
	private Map<Object, Component> cache2 = new HashMap<Object, Component>();
	private EditViewPanel tablePanel;

	public AV_TableCellRenderer(EditViewPanel editViewPanel) {
		super();
		tablePanel = editViewPanel;
	}

	public Component getTableCellComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		Component component = null;

		// if (StringMaster.isEmpty(value.toString())) {
		// component.setBackground(Color.BLACK);
		// return component;
		// }

		if (value == null) {
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
					column);
		}
		if (value.toString().equals(G_PROPS.EMPTY_VALUE.toString())) {
			component = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row,
					column);
			if (!tablePanel.isColorsInverted())
				component.setBackground(ColorManager.OBSIDIAN);
			else
				component.setForeground(Color.WHITE);
			table.setRowHeight(row, table.getRowHeight() / 2);
			return component;
		}

		if (ArcaneVault.getSelectedType() == null)
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
					column);

		String name = (String) table.getValueAt(row, TableDataManager.NAME_COLUMN);

		if (column == TableDataManager.NAME_COLUMN) {

			return renderValueName(table, value, isSelected, hasFocus, row, column, name);

		}
		if (StringMaster.compare(name, G_PROPS.EMPTY_VALUE.toString())) {
			component = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row,
					column);
			if (!tablePanel.isColorsInverted())
				component.setBackground(ColorManager.OBSIDIAN);
			else
				component.setForeground(Color.WHITE);
			table.setRowHeight(row, table.getRowHeight() / 2);
		}
		if (StringMaster.compareByChar(name, G_PROPS.IMAGE.toString(), true)) {
			table.setRowHeight(row, TableDataManager.ROW_HEIGHT * 2);

			return renderImage(table, value, isSelected, hasFocus, row, column, component);
		}
		return renderValueName(table, value, isSelected, hasFocus, row, column, name);
		// return super.getTableCellRendererComponent(table, value, isSelected,
		// hasFocus, row, column);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		Component component = getCache(isSelected).get(value);
		if (component != null)
			return component;
		component = getTableCellComponent(table, value, isSelected, hasFocus, row, column);
		getCache(isSelected).put(value, component);
		return component;
	}

	private Map<Object, Component> getCache(boolean isSelected) {
		return isSelected ? cache : cache2;
	}

	private Component renderValueName(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column, String name) {
		JLabel component =
		// super.getTableCellRendererComponent(table, value, isSelected,
		// hasFocus, row, column);
		new JLabel(value + "");
		if (isSelected) {
			component.setBorder(BorderFactory.createLineBorder(ColorManager.PURPLE));
		}
		// component.setFont(FontMaster.getFont(FONT_TYPE,
		// TableDataManager.FONT_SIZE, Font.PLAIN));

		Color color = null;
		Color color2 = null;
		color = ColorManager.MILD_WHITE;
		color2 = ColorManager.DEEP_GRAY;

		if (isSelected)
			color = ColorManager.getDarkerColor(color, 33);

		// if (row % 2 != 0) {
		if (tablePanel.isColorsInverted()) {
			color = ColorManager.getInvertedColor(color);
			color2 = ColorManager.getInvertedColor(color2);

			color2 = color2.brighter();
			color = color.darker();
			color2 = color2.brighter();
			color = color.darker();
		}
		// else {
		// color = color.brighter();
		// color2 = color2.darker();
		// color = color.brighter();
		// color2 = color2.darker();
		// }
		component.setBackground(color);
		component.setForeground(color2);

		component.setFont(FontMaster.getFont(FONT_TYPE, TableDataManager.FONT_SIZE, Font.ITALIC));

		return component;
	}

	private Component renderImage(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column, Component component) {

		JLabel lbl = ImageManager.getLabel(value.toString(), TableDataManager.ROW_HEIGHT * 2,
				TableDataManager.ROW_HEIGHT * 2);
		table.setBackground(Color.black);
		lbl.setBackground(Color.black);

		return lbl;
	}
}
