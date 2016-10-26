package main.io;

import main.ArcaneTower;
import main.logic.ArcaneEntity;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class AT_EntityMouseListener implements MouseListener {
	ArcaneEntity entity;

	public AT_EntityMouseListener(ArcaneEntity entity) {
		this.entity = entity;
	}

	public void mouseClicked(MouseEvent e) {
		ArcaneTower.selectEntity(entity);

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
