package main.utilities.sorting;

import java.util.Arrays;
import java.util.Vector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import main.swing.generic.components.G_CompHolder;
import main.swing.generic.components.G_Panel;
import main.utilities.sorting.TypeSorter.SORT_BY;

public class SortBox extends G_CompHolder implements ActionListener {

	private static final String TEXT = "Sort by:";
	private Vector<SORT_BY> items;
	private TypeSorter sorter;

	public SortBox() {
		super();
		this.sorter = new TypeSorter();
	}

	public void initComp() {
		comp = new G_Panel();
		JLabel lbl = new JLabel(TEXT);
		comp.add(lbl);
		initItems();
		JComboBox<SORT_BY> dropbox = new JComboBox<SORT_BY>(items);
		dropbox.addActionListener(this);
		comp.add(dropbox, "");
	}

	private void initItems() {
		items = new Vector<SORT_BY>(Arrays.asList(SORT_BY.values()));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
//		sorter.appendLast(items.getOrCreate(((JComboBox<SORT_BY>) e.getSource())
//				.getSelectedIndex()));
	}
}
