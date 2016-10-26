package main.gui.gateway;

import main.logic.ArcaneEntity;
import main.swing.generic.components.panels.WrappedContainer;

import java.awt.Graphics;

public abstract class GatewayContainer<E extends ArcaneEntity> extends WrappedContainer<E> {

	public GatewayContainer() {
		super(true);
	}

	@Override
	public void refresh() {
		super.refresh();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}
}
