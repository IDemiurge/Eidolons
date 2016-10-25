package main.ability.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import main.swing.generic.components.G_Component;
import net.miginfocom.swing.MigLayout;

public class AE_ButtonPanel extends G_Component implements ActionListener {

	String[] commands = new String[] { "Save as template", "Use template" };

	public AE_ButtonPanel() {

		setLayout(new MigLayout("wrap, flowy"));
		// component.setLayout(new FlowLayout());
		for (String command : commands) {
			JButton button = new JButton(command);
			button.setActionCommand(command);
			button.addActionListener(this);
			// sync with Menu???
			add(button);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (((JButton) e.getSource()).getActionCommand()) {
		case "Save as template": {

			return;
		}
		case "Use template": {

			return;
		}

		}
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

}