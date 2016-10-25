package main.utilities.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import main.ability.gui.AE_MainPanel;
import main.data.ability.construct.ConstructionManager;
import main.data.xml.XML_Converter;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Panel;

import org.w3c.dom.Document;

/**
 * Type Filter conditions (any number)
 * 
 * @author JustMe
 * 
 */
public class FilterView extends G_Panel implements ActionListener {

	private static final String DONE = "Done";
	private static final String CANCEL = "Cancel";
	private String xml;
	private AE_MainPanel cPanel;
	Document document;

	public FilterView() {

//		cPanel = new AE_MainPanel(document);
		addComponents();
		addControls();

	}

	private void addControls() {
		// TODO Auto-generated method stub
		JButton doneButton = new JButton(DONE);
		doneButton.setActionCommand(DONE);
		doneButton.addActionListener(this);
		add(doneButton, "sg buttons, wrapy");

	}

	private void addComponents() {
		add(cPanel, "");
//		add(typeBox, "");
//		add(valueBox, "");

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case DONE: {
			xml = XML_Converter.getXMLfromTree(cPanel.getTree());
			document = XML_Converter.getDoc(xml);
			Condition c = (Condition) new ConstructionManager()
					.construct(document);
//			Filter<ObjType> filter = new Filter<>(ref, conditions, TYPE);
//			TypeFilter.filter(filter, groupingValue);
		}
		}
	}

}
