package main.gui;

import main.ArcaneTower;
import main.content.ContentManager;
import main.content.VALUE;
import main.gui.gateway.GatewayView;
import main.logic.ArcaneEntity;
import main.swing.SwingMaster;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.swing.components.panels.page.log.WrappedTextComp;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.services.listener.MouseClickListener;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class AT_EntityNode<E extends ArcaneEntity> extends G_Panel {
	protected TextCompDC headerComp;
	protected WrappedTextComp descrPanel;
	protected E entity;
	protected GraphicComponent imageComp;
	protected WrappedTextComp valuePanel;
	protected TextCompDC toggleButton;
	protected boolean descriptionExpanded;
    private boolean expanded;

	public AT_EntityNode(E e) {
		this.entity = e;
		expanded = GatewayWindow.isInitiallyExpanded() || isInitiallyExpanded();
		headerComp = new TextCompDC(null, null, getFontSize(), FONT.MAIN, getTextColor());
		headerComp.addMouseListener(getMouseListener());

		imageComp = new GraphicComponent(entity.getImage());
		imageComp.addMouseListener(getMouseListener());

		valuePanel = new WrappedTextComp(null, true, 5, Color.black, getDescrFont(), false);
		valuePanel.addMouseListener(getMouseListener());

		descrPanel = new WrappedTextComp(null, true, 5, Color.black, getDescrFont(), false);
		descrPanel.addMouseListener(getMouseListener());

		toggleButton = new TextCompDC(null, "+");
		toggleButton.setDefaultFont(getToggleFont());
		toggleButton.addMouseListener(getMouseListener());
	}

	protected boolean isInitiallyExpanded() {
		return false;
	}

	public void toggleExpanded() {
		setExpanded(!isExpanded());
		refresh();
	}

	protected void toggleDescriptionExpanded() {
		descriptionExpanded = !descriptionExpanded;
		refresh();
	}

	protected String getValuesText() {
		String text = "";
		for (VALUE v : getDisplayedValues()) {
			String valueText = getValueText(v);
            if (!valueText.isEmpty()) {
                text += valueText + "  ";
            }
        }
		return text;
	}

	protected String getValueText(VALUE v) {
		String value = ContentManager.getFormattedValue(v, entity.getValue(v));
        if (value.isEmpty()) {
            return "";
        }
        return v.getName() + ": " + value;

	}

	protected String getHeaderText() {
		return entity.getDisplayedName();
	}

	protected String getDescrText() {
		return entity.getDescription();
	}

	protected Font getToggleFont() {
		return FontMaster.getFont(FONT.AVQ, 22, Font.PLAIN);
	}

	protected Font getDescrFont() {
		return FontMaster.getFont(FONT.NYALA, 14, Font.PLAIN);
	}

	protected Font getHeaderFont() {
		return FontMaster.getFont(FONT.AVQ, 16, Font.PLAIN);
	}

	@Override
	public void refresh() {
		removeAll();
		refreshComponents();
		adjustSize();
		addComponents();
		revalidate();
		repaint();
	}

	@Override
	public void refreshComponents() {
		descrPanel.setText(getDescrText());
		valuePanel.setText(getValuesText());
		headerComp.setText(getHeaderText());
		super.refreshComponents();
	}

	protected void addComponents() {
		if (isShowingDetails()) {
			add(valuePanel, getValuePanelPos() + ", id valuePanel");
			add(descrPanel, "pos image.x2 values.y2");
		}

		if (isCollapsable()) {
			add(toggleButton, "pos @max_x center_y");
			toggleButton.setText(isExpanded() ? "-" : "+");
		}
		add(headerComp, "pos @image.x2 0, id header");
		add(imageComp, "pos 0 0" + ", id image");
	}

	protected String getValuePanelPos() {
		return "pos @max_x header.y2";
	}

	protected boolean isShowingDetails() {
		return isSelected() || isExpanded();
	}

	protected void adjustSize() {
		if (isExpanded()) {
			// only different for taskComps why not let it remain automatic??
		}
		descrPanel.initalizeSizeFromGetText();
		headerComp.initalizeSizeFromGetText();
		valuePanel.initalizeSizeFromGetText();

		// int width = 0;
		// int height = 0;
		// if (!isDescriptionExpanded()) {
		// width = getLinesCollapsed() *
		// FontMaster.getFontHeight(getDescrFont());
		// descrPanel.setDefaultSize(new Dimension(width, height));
		// }
		// valuePanel.setDefaultSize(new Dimension(width, height)); wrapped
		// auto-sized?
		// width += headerComp.getPanelWidth();
		// height += headerComp.getPanelHeight();
		// panelSize = new Dimension(width, height);

	}

	@Override
	public Dimension getPanelSize() {
		return SwingMaster.getMinMaxSize(super.getPreferredSize(), 200, 100, 200, 400);
		// return super.getPreferredSize();
	}

	@Override
	public boolean isAutoSizingOn() {
		return true;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		// if (!ArcaneTower.isSwingGraphicsMode())
		// return;

	}

	@Override
	public int getBorderWidth() {
        if (isSelected()) {
            return 2;
        }
        return 1;
	}

	@Override
	public Color getBorderColor() {
        if (isSelected()) {
            return (ColorManager.ESSENCE);
        }
        return super.getBorderColor();
	}

	protected MouseListener getMouseListener() {
		// return new GatewayMouseListener(this);
		return new MouseClickListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean alt = e.isAltDown();
				boolean right = SwingUtilities.isRightMouseButton(e);
				if (e.getSource() == descrPanel) {
					descriptionClicked(right, alt);
				} else if (e.getSource() == toggleButton) {
					toggleExpanded();
				} else if (e.getSource() == headerComp) {

				} else if (e.getSource() == valuePanel) {

				}

				entitySelected();
			}
		};
	}

	protected void entitySelected() {
		GatewayView.getInstance().getEditPanel().selectType(entity.getType());

	}

	private boolean isExpansionSycned() {
		return true;
	}

	protected void descriptionClicked(boolean right, boolean alt) {
		toggleDescriptionExpanded();
	}

	protected int getLinesCollapsed() {
		return 1;
	}

	public boolean isDescriptionExpanded() {
		return descriptionExpanded;
	}

	public boolean isExpanded() {
		return expanded;
	}

    public void setExpanded(boolean expanded) {
        if (isExpansionSycned()) {
            descriptionExpanded = expanded;
        }
        this.expanded = expanded;
    }

	protected boolean isCollapsable() {
		return false;
	}

	protected boolean isSelected() {
		return ArcaneTower.getSelectedEntity() == entity;
	}

	public abstract VALUE[] getDisplayedValues();

	protected Color getTextColor() {
		return Color.black;
	}

	protected int getFontSize() {
		return 20;
	}

}
