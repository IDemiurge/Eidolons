package main.gui.gateway.node;

import main.content.VALUE;
import main.gui.AT_EntityNode;
import main.gui.gateway.GatewayContainer;
import main.gui.gateway.GoalContainer;
import main.logic.AT_PARAMS;
import main.logic.ArcaneEntity;
import main.logic.Direction;
import main.swing.SwingMaster;

import java.awt.Dimension;

public class TopGatewayComp<E extends ArcaneEntity> extends AT_EntityNode<E> {
	private static final VALUE[] directionValues = { AT_PARAMS.TIME_SPENT,
			AT_PARAMS.TIME_ESTIMATED, AT_PARAMS.DEADLINE, };
	private static final int DEFAULT_WIDTH = 300;
	private static final int DEFAULT_HEIGHT = 200;
	boolean direction;
	boolean vertical = true;
	private GatewayContainer<? extends ArcaneEntity> container;

	public TopGatewayComp(E e) {
		super(e);
		panelSize = getHeaderSize();
		direction = (entity instanceof Direction);
	}

	private Dimension getHeaderSize() {
		return new Dimension(DEFAULT_WIDTH * getZoom() / 100, DEFAULT_HEIGHT * getZoom() / 100);
	}

	@Override
	protected boolean isInitiallyExpanded() {
		return true;
	}

	@Override
	protected void addComponents() {
		super.addComponents();

		if (isExpanded()) {
			if (direction)
				container = new GoalContainer((Direction) entity);
			else {
				// TODO
			}
			container.refresh();
			int y = getPanelHeight();
			int x = getPanelWidth();
			panelSize = SwingMaster.getModifiedSize(getHeaderSize(), vertical ? 0 : container
					.getPanelWidth(), !vertical ? 0 : container.getPanelHeight());
			if (vertical)
				add(container, "pos 0 " + y);
			else
				add(container, "pos " + x + " 0");
			// add(container);
		}
	}

	@Override
	public Dimension getPanelSize() {
		return panelSize;
	}

	private int getZoom() {
		return 100;
	}

	@Override
	public boolean isAutoSizingOn() {
		return false;
	}

	@Override
	public VALUE[] getDisplayedValues() {
		switch (entity.getOBJ_TYPE_ENUM()) {
			case DIRECTION:
				return directionValues;
		}
		return null;
	}
}
