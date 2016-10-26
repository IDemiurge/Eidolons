package main.gui.gateway;

import main.ArcaneTower;
import main.gui.gateway.node.TopGatewayComp;
import main.logic.AT_OBJ_TYPE;
import main.logic.ArcaneEntity;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.Zoomable;
import main.swing.generic.components.panels.G_PagePanel;

import java.util.Collections;
import java.util.List;

public class TopGatewayContainer<E extends ArcaneEntity> extends G_PagePanel<E> implements Zoomable // WrappedContainer
{
	private static final boolean VERTICAL = false;
	private static final int VERSION = 3;
	private AT_OBJ_TYPE TYPE;

	public TopGatewayContainer(AT_OBJ_TYPE topLevelType, int levelsOfDepth, int defaultDetailLevel) {
		// super(true, VERSION, GatewayWindow.getGatewaySize());
		super(3, VERTICAL, VERSION);

		this.TYPE = topLevelType;

	}

	protected boolean isCentering() {
		return true;
	}

	// @Override
	protected G_Panel createComponent(E e) {
		TopGatewayComp<E> comp = new TopGatewayComp<E>(e);
		comp.refresh();
		return comp;
	}

	@Override
	public List<E> getData() {
		if (TYPE == AT_OBJ_TYPE.DIRECTION)
			data = (List<E>) ArcaneTower.getDirections();
		else
			data = (List<E>) ArcaneTower.getPeriods(TYPE);
		Collections.reverse(data);
		return data;
	}

	@Override
	protected boolean isFillWithNullElements() {
		return false;
	}

	@Override
	protected G_Component createPageComponent(List<E> list) {
		G_Panel panel = new G_Panel("flowy");
		for (E e : list)
			panel.add(createComponent(e));
		return panel;
	}

	@Override
	protected List<List<E>> getPageData() {
		return splitList(getData());
	}

	@Override
	public void zoom(int n) {
		// TODO Auto-generated method stub

	}

}
