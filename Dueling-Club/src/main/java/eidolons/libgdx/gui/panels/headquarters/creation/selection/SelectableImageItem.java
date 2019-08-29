package eidolons.libgdx.gui.panels.headquarters.creation.selection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import main.content.enums.GenericEnums;

import java.util.Arrays;

/**
 * Created by JustMe on 7/3/2018.
 */
public class SelectableImageItem extends FadeImageContainer{
    private final ImageContainer highlight;
    private final SelectionImageTable table;
    SelectableItemData data;
    private boolean selected;

    public SelectableImageItem(SelectionImageTable selectionImageTable, SelectableItemData data) {
        super(selectionImageTable.getDisplayablePath(data));
        table = selectionImageTable;
        addActor(highlight =new ImageContainer( (data.getBorderSelected())));
        highlight.setColor(new Color(1,1,1,0));
        highlight.setPosition((Arrays.asList( table.getData()) .indexOf(data)*(
          getWidth()+selectionImageTable.getSpace()))-selectionImageTable.getSpace()/2+
         GdxMaster.centerWidth(highlight),
         //dirty hack
         GdxMaster.centerHeight(highlight));
        setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.ATB_POS);
        this.data = data;
        if (data.isSelectionUnderneath())
        highlight.setZIndex(0);
        highlight.setTouchable(Touchable.disabled);
        addListener(new ValueTooltip("Select " + data.getName()).getController());
//            debug();
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
            if (highlight.getColor().a>0) {
                if (highlight.getActions().size==0)
                    ActionMaster.addFadeOutAction(highlight,1, false);
            }
        }
    }

    @Override
    public boolean isAlphaFluctuationOn() {
        return table.getSelectedItem()==this;
    }

    public SelectableItemData getData() {
        return data;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (!selected)
            ActionMaster.addFadeOutAction(highlight,1, false);
            else {
                return;
        }
    }

    public boolean isSelected() {
        return selected;
    }
}



