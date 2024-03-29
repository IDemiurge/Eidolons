package libgdx.gui.dungeon.panels.headquarters.creation.selection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.bf.generic.ImageContainer;
import libgdx.gui.dungeon.menu.selection.ItemListPanel;
import libgdx.gui.dungeon.tooltips.ValueTooltip;
import main.content.enums.GenericEnums;

public class SelectableImageItem extends FadeImageContainer {
    private final ImageContainer highlight;
    private final SelectionImageTable table;
    ItemListPanel.SelectableItemData data;
    private boolean selected;

    public SelectableImageItem(SelectionImageTable selectionImageTable, ItemListPanel.SelectableItemData data) {
        super(selectionImageTable.getDisplayablePath(data));
        table = selectionImageTable;

        addActor(highlight = new ImageContainer((data.getBorderSelected())));
        highlight.setColor(new Color(1, 1, 1, 0));
        setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.ATB_POS);
        this.data = data;
        if (data.isSelectionUnderneath())
            highlight.setZIndex(0);
        highlight.setTouchable(Touchable.disabled);
        addListener(new ValueTooltip("Select " + data.getName()).getController());
        //            debug();

    }

    @Override
    public void act(float delta) {
        highlight.setSize(getWidth(), getHeight());
        super.act(delta);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        highlight.setPosition(getX(), getY());
        super.draw(batch, parentAlpha);
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
        highlight.setScale(scaleXY);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
    }

    @Override
    protected void alphaFluctuation(float delta) {
        if (isAlphaFluctuationOn())
            alphaFluctuation(highlight, delta);
        else {
            if (highlight.getColor().a > 0) {
                if (highlight.getActions().size == 0)
                    ActionMasterGdx.addFadeOutAction(highlight, 1, false);
            }
        }
    }

    @Override
    public boolean isAlphaFluctuationOn() {
        return table.getSelectedItem() == this;
    }

    public ItemListPanel.SelectableItemData getData() {
        return data;
    }

    public void setSelected(boolean selected) {
        if (this.selected == selected) {
            return;
        }
        this.selected = selected;
        if (!selected) {
            ActionMasterGdx.addScaleAction(getContent(), highlight.getScaleX(), 0.3f);
            setZIndex(0);
        } else {
            ActionMasterGdx.addScaleAction(getContent(), highlight.getScaleX() * 1.3f, 0.3f);
            setZIndex(999);
        }
    }

    public boolean isSelected() {
        return selected;
    }
}
