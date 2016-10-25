package main.music.gui;

import main.data.DataManager;
import main.music.MusicCore;
import main.music.entity.MusicList;
import main.swing.components.TextComp;
import main.swing.components.panels.page.log.WrappedTextComp;
import main.swing.generic.components.G_Panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;

public class MusicDisplayPanel extends G_Panel {
	WrappedTextComp selectedComp;
	TextComp argComp;
	private JComboBox<MusicList> lastPlayed;

	public MusicDisplayPanel(MusicListPanel musicListPanel) {
		super("flowy");

		selectedComp = new WrappedTextComp(null, false) {
			@Override
			protected Color getColor() {
				return MusicCore.getTextColor();
			}

		};
		argComp = new TextComp(MusicCore.getTextColor());
		add(argComp);

		List<String> lines = DataManager
				.toStringList(MusicMouseListener.getSelectedTracks());
		if (lines.isEmpty())
			lines = DataManager.toStringList(MusicMouseListener.getSelectedLists());
		lines.add(0, "Selected: ");
		selectedComp.setTextLines(lines);
		selectedComp.setPanelSize(selectedComp.getPanelSize());
		add(selectedComp);
		panelSize = new Dimension(210, 205);
		selectedComp.setDefaultSize(new Dimension(210, 200));

		lastPlayed = new JComboBox<MusicList>(new Vector(MusicCore.getLastPlayed()));
		lastPlayed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MusicList list = (MusicList) lastPlayed.getSelectedItem();
				list.getMouseListener().handleClick(arg0.getModifiers(), null);

			}
		});
		add(lastPlayed);
	}

	@Override
	public void refresh() {
		argComp.setText("Arg: " + MusicMouseListener.getArg());
		argComp.refresh();
		List<String> lines = DataManager
				.toStringList(MusicMouseListener.getSelectedTracks());
		if (lines.isEmpty())
			lines = DataManager.toStringList(MusicMouseListener.getSelectedLists());
		lines.add(0, "Selected: ");
		selectedComp.setTextLines(lines);
		selectedComp.refresh();

	}

}
